package lishui.lib.router.core.callback;


import lishui.lib.router.core.Postcard;

/**
 * Callback after navigation.
 */
public interface NavigationCallback {

    void onFound(Postcard postcard);

    void onLost(String path);

    void onError(Throwable throwable);

    void onArrival(Postcard postcard);
}
