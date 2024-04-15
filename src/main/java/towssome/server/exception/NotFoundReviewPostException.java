package towssome.server.exception;

public class NotFoundReviewPostException extends RuntimeException{
    public NotFoundReviewPostException() { super(); }

    public NotFoundReviewPostException(String message) {
        super(message);
    }

    public NotFoundReviewPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundReviewPostException(Throwable cause) {
        super(cause);
    }

    protected NotFoundReviewPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
