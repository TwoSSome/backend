package towssome.server.exception;

public class NotFoundCalendarException extends RuntimeException {

    public NotFoundCalendarException() {
        super();
    }

    public NotFoundCalendarException(String message) {
        super(message);
    }

    public NotFoundCalendarException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCalendarException(Throwable cause) {
        super(cause);
    }

    protected NotFoundCalendarException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
