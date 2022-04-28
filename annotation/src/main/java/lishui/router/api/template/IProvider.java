package lishui.lib.router.api.template;

/**
 * Provider interface, base of other interface.
 */
public interface IProvider {

    /**
     * Do your init work in this method, it well be call when processor has been load.
     */
    void init();
}
