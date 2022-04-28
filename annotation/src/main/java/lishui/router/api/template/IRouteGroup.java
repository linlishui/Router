package lishui.lib.router.api.template;

import java.util.Map;

import lishui.lib.router.annotation.model.RouteMeta;

/**
 * Group element.
 */
public interface IRouteGroup {
    /**
     * Fill the atlas with routes in group.
     */
    void loadInto(Map<String, RouteMeta> atlas);
}
