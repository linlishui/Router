package lishui.lib.router.api.template;

import java.util.Map;

/**
 * Root element.
 */
public interface IRouteRoot {

    /**
     * Load routes to input
     * @param routes input
     */
    void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
}
