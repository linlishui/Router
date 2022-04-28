package lishui.lib.router.api.callback;


import lishui.lib.router.api.Postcard;

/**
 * The callback of interceptor.
 */
public interface InterceptorCallback {

    /**
     * Continue process
     *
     * @param postcard route meta
     */
    void onContinue(Postcard postcard);

    /**
     * Interrupt process, pipeline will be destroy when this method called.
     *
     * @param exception Reson of interrupt.
     */
    void onInterrupt(Throwable exception);
}
