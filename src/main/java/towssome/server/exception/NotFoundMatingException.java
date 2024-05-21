package towssome.server.exception;

public class NotFoundMatingException extends RuntimeException {

    public NotFoundMatingException() {
        super();
    }

    public NotFoundMatingException(String message) {
        super(message);
    }

    public NotFoundMatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundMatingException(Throwable cause) {
        super(cause);
    }

    protected NotFoundMatingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
