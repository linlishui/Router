package lishui.lib.router.api.exception;

public class NoRouteFoundException extends RuntimeException {
    /**
     * Constructs a new {@code RuntimeException} with the current stack trace
     * and the specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public NoRouteFoundException(String detailMessage) {
        super(detailMessage);
    }
}
