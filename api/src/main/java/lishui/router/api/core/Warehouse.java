package lishui.lib.router.api.core;


import java.util.HashMap;
import java.util.Map;

import lishui.lib.router.annotation.model.RouteMeta;
import lishui.lib.router.api.template.IProvider;
import lishui.lib.router.api.template.IRouteGroup;

/**
 * Storage of route meta and other data.
 */
class Warehouse {
    // Cache route and metas
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String, RouteMeta> routes = new HashMap<>();

    // Cache provider
    static Map<Class, IProvider> providers = new HashMap<>();
    static Map<String, RouteMeta> providersIndex = new HashMap<>();

    static void clear() {
        routes.clear();
        groupsIndex.clear();
        providers.clear();
        providersIndex.clear();
    }
}
