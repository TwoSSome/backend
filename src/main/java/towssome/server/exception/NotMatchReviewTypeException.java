package towssome.server.exception;

public class NotMatchReviewTypeException extends RuntimeException {

    public NotMatchReviewTypeException() {
        super();
    }

    public NotMatchReviewTypeException(String message) {
        super(message);
    }

    public NotMatchReviewTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotMatchReviewTypeException(Throwable cause) {
        super(cause);
    }

    protected NotMatchReviewTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
