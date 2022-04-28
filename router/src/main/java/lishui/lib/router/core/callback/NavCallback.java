package lishui.lib.router.core.callback;


import lishui.lib.router.core.Postcard;

/**
 * Easy to use navigation callback.
 */
public abstract class NavCallback implements NavigationCallback {
    @Override
    public void onFound(Postcard postcard) {
        // Do nothing
    }

    @Override
    public void onLost(String path) {
        // Do nothing
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public abstract void onArrival(Postcard postcard);

}
