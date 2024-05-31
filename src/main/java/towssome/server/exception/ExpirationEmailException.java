package towssome.server.exception;

public class ExpirationEmailException extends RuntimeException {

    public ExpirationEmailException() {
        super();
    }

    public ExpirationEmailException(String message) {
        super(message);
    }

    public ExpirationEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpirationEmailException(Throwable cause) {
        super(cause);
    }

    protected ExpirationEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
