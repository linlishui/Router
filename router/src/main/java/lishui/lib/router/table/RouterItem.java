package lishui.lib.router.table;

/**
 * author : linlishui
 * time   : 2021/11/25
 * desc   : 路由表里的数据项
 */
public class RouterItem {

    private final String path;            // 路由地址
    private final Class<?> target;        // 路由目标类
    private final RouteType routeType;    // 路由类型

    private int priority = -1;            // 优先级，值越高优先跳转

    public RouterItem(String path, Class<?> target, RouteType routeType) {
        if (path == null || path.length() == 0) {
            throw new IllegalStateException("RouterItem path is empty.");
        }
        if (target == null) {
            throw new IllegalStateException("RouterItem target class is null.");
        }
        this.path = path;
        this.target = target;
        this.routeType = routeType;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPath() {
        return path;
    }

    public Class<?> getTarget() {
        return target;
    }

    public int getPriority() {
        return priority;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    @Override
    public String toString() {
        return "RouterItem{" +
                "path='" + path + '\'' +
                ", target=" + target +
                ", routeType=" + routeType +
                ", priority=" + priority +
                '}';
    }
}
