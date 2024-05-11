package towssome.server.exception;

public class NotFoundBookMarkException extends RuntimeException {
    public NotFoundBookMarkException() {
        super();
    }

    public NotFoundBookMarkException(String message) {
        super(message);
    }

    public NotFoundBookMarkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundBookMarkException(Throwable cause) {
        super(cause);
    }

    protected NotFoundBookMarkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
