package towssome.server.exception;

public class BodyOverException extends RuntimeException {

    public BodyOverException() {
        super();
    }

    public BodyOverException(String message) {
        super(message);
    }

    public BodyOverException(String message, Throwable cause) {
        super(message, cause);
    }

    public BodyOverException(Throwable cause) {
        super(cause);
    }

    protected BodyOverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
