package towssome.server.exception;

public class NotFoundPhotoException extends RuntimeException  {

    public NotFoundPhotoException() {
        super();
    }

    public NotFoundPhotoException(String message) {
        super(message);
    }

    public NotFoundPhotoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundPhotoException(Throwable cause) {
        super(cause);
    }

    protected NotFoundPhotoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
