package lishui.lib.router.table;

import java.util.ArrayList;
import java.util.List;

/**
 * author : linlishui
 * time   : 2021/11/25
 * desc   : 路由表
 */
public class RouterTable {

    private final List<RouterItem> routerItemList = new ArrayList<>();

    private RouterTable() {
    }

    public List<RouterItem> getRouterItemList() {
        return routerItemList;
    }

    public static class Builder {

        private final RouterTable routerTable = new RouterTable();

        public Builder addItem(RouterItem routerItem) {
            if (routerItem != null) {
                routerTable.routerItemList.add(routerItem);
            }
            return this;
        }

        public RouterTable build() {
            return routerTable;
        }
    }

}
