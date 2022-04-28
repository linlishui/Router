package lishui.lib.router.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import lishui.lib.router.BuildConfig;
import lishui.lib.router.core.callback.NavigationCallback;
import lishui.lib.router.table.RouterItem;
import lishui.lib.router.table.RouterMeta;
import lishui.lib.router.table.RouterTable;

/**
 * author : linlishui
 * time   : 2021/11/25
 * desc   : 路由管理者，负责解析路由表、模块跳转等
 */
final class RouterImpl {

    private static final String TAG = Router.class.getSimpleName();

    private static Context sContext;
    private static Handler sHandler;
    private static boolean sIsInit = false;
    private final static Map<String, RouterItem> routes = new HashMap<>();

    private RouterImpl() {
    }

    @WorkerThread
    static synchronized boolean tryToInit(Context context) {
        if (sIsInit) {
            return true;
        }
        sContext = context.getApplicationContext();
        sHandler = new Handler(Looper.getMainLooper());
        boolean initSuccess = true;
        try {
            ServiceLoader<RouterMeta> serviceLoader = ServiceLoader.load(RouterMeta.class);
            Iterator<RouterMeta> iterator = serviceLoader.iterator();
            final boolean hasRouterMap = iterator.hasNext();
            Log.i(TAG, "Meta has data --> " + hasRouterMap);
            while (iterator.hasNext()) {
                try {
                    parseRouterMeta(iterator.next());
                } catch (java.util.ServiceConfigurationError error) {
                    Log.e(TAG, "parseRouterMeta occur error.", error);
                }
            }
        } catch (Exception exception) {
            initSuccess = false;
        }
        sIsInit = initSuccess;
        return true;
    }

    @WorkerThread
    private static void parseRouterMeta(RouterMeta routerMeta) {
        RouterTable routerTable = routerMeta.buildTable();
        routerTable.getRouterItemList().forEach(routerItem -> {
            debugLog("parseRouterMeta -> " + routerItem.toString());
            routes.put(routerItem.getPath(), routerItem);
        });
    }

    @WorkerThread
    static void afterInit() {
        // do some work after init finished.
    }

    static Postcard build(String path) {
        RouterItem routerItem = routes.get(checkPath(path));
        return new Postcard(path, routerItem);
    }

    static void navigation(
            final Context context,
            final Postcard postcard,
            final int requestCode,
            NavigationCallback callback
    ) {

        if (callback == null && BuildConfig.DEBUG) {
            callback = new NavDebugCallback();
        }

        // Not fount destination from path
        if (postcard.getRouterItem() == null) {
            if (callback != null) {
                callback.onLost(postcard.getPath());
            }
            return;
        }

        if (callback != null) {
            callback.onFound(postcard);
        }

        _navigation(context, postcard, requestCode, callback);
    }

    @SuppressLint("WrongConstant")
    private static void _navigation(final Context context,
                                    final Postcard postcard,
                                    final int requestCode,
                                    final NavigationCallback callback) {

        Context currentContext = context == null ? sContext : context;

        final RouterItem routerItem = postcard.getRouterItem();
        switch (routerItem.getRouteType()) {
            case ACTIVITY:
                if (!Activity.class.isAssignableFrom(routerItem.getTarget())) {
                    if (callback != null) {
                        callback.onError(new IllegalStateException(routerItem.getTarget().getSimpleName() + " is not extend Activity"));
                        return;
                    }
                }
                // Build intent
                final Intent intent = new Intent(currentContext, routerItem.getTarget());
                intent.putExtras(postcard.getExtras());

                // Set flags.
                int flags = postcard.getFlags();
                if (0 != flags) {
                    intent.setFlags(flags);
                }

                // Non activity, need FLAG_ACTIVITY_NEW_TASK
                if (!(currentContext instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                // Set Actions
                String action = postcard.getAction();
                if (!TextUtils.isEmpty(action)) {
                    intent.setAction(action);
                }

                // Set data and mime type. uri and type default null
                Uri uri = postcard.getUri();
                String type = postcard.getType();
                intent.setDataAndType(uri, type);

                // Navigation in main looper.
                runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startActivity(requestCode, currentContext, intent, postcard, callback);
                        } catch (Exception exception) {
                            if (callback != null) {
                                callback.onError(exception);
                            }
                        }
                    }
                });
                break;
            default:
                return;
        }
    }

    private static class NavDebugCallback implements NavigationCallback {

        @Override
        public void onFound(Postcard postcard) {
            debugLog(">>>>> onFound in path: " + postcard.getPath());
        }

        @Override
        public void onLost(String path) {
            debugLog(">>>>> onLost in path: " + path);
        }

        @Override
        public void onError(Throwable throwable) {
            debugLog(">>>>> onError with message: " + throwable.getMessage());
        }

        @Override
        public void onArrival(Postcard postcard) {
            debugLog(">>>>> onArrival with " + postcard);
        }
    }

    /* 相关调用辅助方法 */
    private static String checkPath(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalStateException("path is empty or not start with '/' ");
        }

        return path;
    }

    private static void runInMainThread(@NonNull Runnable runnable) {

        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    private static void startActivity(int requestCode, Context currentContext, Intent intent, Postcard postcard, NavigationCallback callback) {
        if (requestCode >= 0) {  // Need start for result
            if (currentContext instanceof Activity) {
                startActivityForResult((Activity) currentContext, intent, requestCode, postcard.getOptionsBundle());
            }
        } else {
            startActivity(currentContext, intent, postcard.getOptionsBundle());
        }

        if ((-1 != postcard.getEnterAnim() && -1 != postcard.getExitAnim()) && currentContext instanceof Activity) {    // Old version.
            ((Activity) currentContext).overridePendingTransition(postcard.getEnterAnim(), postcard.getExitAnim());
        }

        if (null != callback) {
            callback.onArrival(postcard);
        }
    }

    private static void startActivity(@NonNull Context context, @NonNull Intent intent,
                                      @Nullable Bundle options) {
        if (Build.VERSION.SDK_INT >= 16) {
            context.startActivity(intent, options);
        } else {
            context.startActivity(intent);
        }
    }

    private static void startActivityForResult(@NonNull Activity activity, @NonNull Intent intent,
                                               int requestCode, @Nullable Bundle options) {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    private static void debugLog(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

}
