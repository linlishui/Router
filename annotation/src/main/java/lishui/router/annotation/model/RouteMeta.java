package lishui.lib.router.annotation.model;

import javax.lang.model.element.Element;

import lishui.lib.router.annotation.Route;
import lishui.lib.router.annotation.enums.RouteType;

/**
 * It contains basic route information.
 */
public class RouteMeta {
    private RouteType type;         // Type of route
    private Element rawType;        // Raw type of route
    private Class<?> destination;   // Destination
    private String path;            // Path of route
    private String group;           // Group of route
    private int priority = -1;      // The smaller the number, the higher the priority
    private String name;

    public RouteMeta() {
    }

    /**
     * @param type        type
     * @param destination destination
     * @param path        path
     * @param group       group
     * @param priority    priority
     * @return this
     */
    public static RouteMeta build(RouteType type, Class<?> destination, String path, String group, int priority) {
        return new RouteMeta(type, null, destination, null, path, group, priority);
    }

    /**
     * Type
     *
     * @param route       route
     * @param destination destination
     * @param type        type
     */
    public RouteMeta(Route route, Class<?> destination, RouteType type) {
        this(type, null, destination, route.name(), route.path(), route.group(), route.priority());
    }

    /**
     * Type
     *
     * @param route   route
     * @param rawType rawType
     * @param type    type
     */
    public RouteMeta(Route route, Element rawType, RouteType type) {
        this(type, rawType, null, route.name(), route.path(), route.group(), route.priority());
    }

    /**
     * Type
     *
     * @param type        type
     * @param rawType     rawType
     * @param destination destination
     * @param path        path
     * @param group       group
     * @param priority    priority
     */
    public RouteMeta(RouteType type, Element rawType, Class<?> destination, String name, String path, String group, int priority) {
        this.type = type;
        this.name = name;
        this.destination = destination;
        this.rawType = rawType;
        this.path = path;
        this.group = group;
        this.priority = priority;
    }


    public Element getRawType() {
        return rawType;
    }

    public RouteMeta setRawType(Element rawType) {
        this.rawType = rawType;
        return this;
    }

    public RouteType getType() {
        return type;
    }

    public RouteMeta setType(RouteType type) {
        this.type = type;
        return this;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public RouteMeta setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public String getPath() {
        return path;
    }

    public RouteMeta setPath(String path) {
        this.path = path;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RouteMeta setGroup(String group) {
        this.group = group;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public RouteMeta setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RouteMeta{" +
                "type=" + type +
                ", rawType=" + rawType +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", priority=" + priority +
                ", name='" + name + '\'' +
                '}';
    }
}