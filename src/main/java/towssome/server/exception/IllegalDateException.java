package towssome.server.exception;

public class IllegalDateException extends RuntimeException {

    public IllegalDateException() {
        super();
    }

    public IllegalDateException(String message) {
        super(message);
    }

    public IllegalDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDateException(Throwable cause) {
        super(cause);
    }

    protected IllegalDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
