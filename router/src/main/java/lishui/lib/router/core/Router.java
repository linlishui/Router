package lishui.lib.router.core;

import android.content.Context;

import androidx.annotation.WorkerThread;

import lishui.lib.router.core.callback.NavigationCallback;

/**
 * author : linlishui
 * time   : 2021/11/25
 * desc   : 全局路由入口
 */
public final class Router {

    private volatile static Router instance = null;
    private volatile static boolean hasInit = false;

    private Router() {
    }

    public static Router getInstance() {
        if (!hasInit) {
            throw new IllegalStateException("Router::Init::Invoke init() first!");
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

    @WorkerThread
    public static void init(Context context) {
        if (hasInit) {
            return;
        }

        hasInit = RouterImpl.tryToInit(context);

        if (hasInit) {
            RouterImpl.afterInit();
        }
    }

    public Postcard build(String path) {
        return RouterImpl.build(path);
    }

    public void navigation(Context context, Postcard postcard, int requestCode, NavigationCallback callback) {
        RouterImpl.navigation(context, postcard, requestCode, callback);
    }
}
