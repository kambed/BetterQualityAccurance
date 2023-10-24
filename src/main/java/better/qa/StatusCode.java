package better.qa;

/**
 * Status codes.
 */
public class StatusCode {

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * Hidden constructor.
     */
    private StatusCode() {}

    /**
     * Check if status code is success.
     * @param statusCode statusCode
     * @return is success
     */
    public static boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }
}
