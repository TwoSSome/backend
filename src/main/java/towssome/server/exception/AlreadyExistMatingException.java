package towssome.server.exception;

public class AlreadyExistMatingException extends RuntimeException {

    public AlreadyExistMatingException() {
        super();
    }

    public AlreadyExistMatingException(String message) {
        super(message);
    }

    public AlreadyExistMatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistMatingException(Throwable cause) {
        super(cause);
    }

    protected AlreadyExistMatingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
