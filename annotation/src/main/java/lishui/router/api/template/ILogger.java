package lishui.lib.router.api.template;

/**
 * Logger
 */
public interface ILogger {

    boolean isShowLog = false;
    boolean isShowStackTrace = false;

    void showLog(boolean isShowLog);

    void showStackTrace(boolean isShowStackTrace);

    void debug(String tag, String message);

    void info(String tag, String message);

    void warning(String tag, String message);

    void error(String tag, String message);

    void error(String tag, String message, Throwable e);

    void monitor(String message);

    boolean isMonitorMode();

    String getDefaultTag();
}
