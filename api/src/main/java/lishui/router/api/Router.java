package lishui.lib.router.api;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import java.util.concurrent.ThreadPoolExecutor;

import lishui.lib.router.api.callback.NavigationCallback;
import lishui.lib.router.api.exception.InitException;
import lishui.lib.router.api.template.ILogger;
import lishui.lib.router.api.template.IRouteGroup;
import lishui.lib.router.api.utils.Consts;

/**
 * Router
 */
public final class Router {
    // Key of raw uri
    public static final String RAW_URI = "NTeRQWvye18AkPd6G";

    private volatile static Router instance = null;
    private volatile static boolean hasInit = false;
    public static ILogger logger;

    private Router() {
    }

    /**
     * Init, it must be call before used router.
     */
    public static void init(Application application) {
        if (!hasInit) {
            logger = RouterImpl.logger;
            RouterImpl.logger.info(Consts.TAG, "Router init start.");
            hasInit = RouterImpl.init(application);

            if (hasInit) {
                RouterImpl.afterInit();
            }

            RouterImpl.logger.info(Consts.TAG, "Router init over.");
        }
    }

    /**
     * Get instance of router. A
     * All feature U use, will be starts here.
     */
    public static Router getInstance() {
        if (!hasInit) {
            throw new InitException("Router::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (Router.class) {
                    if (instance == null) {
                        instance = new Router();
                    }
                }
            }
            return instance;
        }
    }

    public static synchronized void openDebug() {
        RouterImpl.openDebug();
    }

    public static boolean debuggable() {
        return RouterImpl.debuggable();
    }

    public static synchronized void openLog() {
        RouterImpl.openLog();
    }

    public static synchronized void printStackTrace() {
        RouterImpl.printStackTrace();
    }

    public static synchronized void setExecutor(ThreadPoolExecutor tpe) {
        RouterImpl.setExecutor(tpe);
    }

    public synchronized void destroy() {
        RouterImpl.destroy();
        hasInit = false;
    }

    public static synchronized void monitorMode() {
        RouterImpl.monitorMode();
    }

    public static boolean isMonitorMode() {
        return RouterImpl.isMonitorMode();
    }

    public static void setLogger(ILogger userLogger) {
        RouterImpl.setLogger(userLogger);
    }

    /**
     * Build the roadmap, draw a postcard.
     *
     * @param path Where you go.
     */
    public Postcard build(String path) {
        return RouterImpl.getInstance().build(path);
    }

    /**
     * Build the roadmap, draw a postcard.
     *
     * @param path  Where you go.
     * @param group The group of path.
     */
    @Deprecated
    public Postcard build(String path, String group) {
        return RouterImpl.getInstance().build(path, group, false);
    }

    /**
     * Build the roadmap, draw a postcard.
     *
     * @param url the path
     */
    public Postcard build(Uri url) {
        return RouterImpl.getInstance().build(url);
    }

    /**
     * Launch the navigation by type
     *
     * @param service interface of service
     * @param <T>     return type
     * @return instance of service
     */
    public <T> T navigation(Class<? extends T> service) {
        return RouterImpl.getInstance().navigation(service);
    }

    /**
     * Launch the navigation.
     *
     * @param mContext    .
     * @param postcard    .
     * @param requestCode Set for startActivityForResult
     * @param callback    cb
     */
    public Object navigation(Context mContext, Postcard postcard, int requestCode, NavigationCallback callback) {
        return RouterImpl.getInstance().navigation(mContext, postcard, requestCode, callback);
    }

    /**
     * Add route group dynamic.
     * @param group route group.
     * @return add result.
     */
    public boolean addRouteGroup(IRouteGroup group) {
        return RouterImpl.getInstance().addRouteGroup(group);
    }
}
