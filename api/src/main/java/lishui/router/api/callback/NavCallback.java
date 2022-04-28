package lishui.lib.router.api.callback;


import lishui.lib.router.api.Postcard;

/**
 * Easy to use navigation callback.
 */
public abstract class NavCallback implements NavigationCallback {
    @Override
    public void onFound(Postcard postcard) {
        // Do nothing
    }

    @Override
    public void onLost(Postcard postcard) {
        // Do nothing
    }

    @Override
    public abstract void onArrival(Postcard postcard);

    @Override
    public void onInterrupt(Postcard postcard) {
        // Do nothing
    }
}
