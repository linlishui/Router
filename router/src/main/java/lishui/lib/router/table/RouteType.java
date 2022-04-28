package lishui.lib.router.table;

/**
 * Type of route enum.
 */
public enum RouteType {
    ACTIVITY(0, "android.app.Activity"),
    SERVICE(1, "android.app.Service"),
    UNKNOWN(-1, "Unknown route type");

    private final int id;
    private final String className;

    RouteType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }
}
