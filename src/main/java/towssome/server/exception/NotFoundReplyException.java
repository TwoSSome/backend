package towssome.server.exception;

public class NotFoundReplyException extends RuntimeException {

    public NotFoundReplyException() {
        super();
    }

    public NotFoundReplyException(String message) {
        super(message);
    }

    public NotFoundReplyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundReplyException(Throwable cause) {
        super(cause);
    }

    protected NotFoundReplyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
