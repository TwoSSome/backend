package towssome.server.exception;

import java.io.IOException;

public class PhotoSaveException extends RuntimeException {

    public PhotoSaveException() {
        super();
    }

    public PhotoSaveException(String message) {
        super(message);
    }

    public PhotoSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhotoSaveException(Throwable cause) {
        super(cause);
    }

    protected PhotoSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
