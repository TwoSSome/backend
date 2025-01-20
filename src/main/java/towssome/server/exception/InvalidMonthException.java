package towssome.server.exception;

public class InvalidMonthException extends RuntimeException {

    public InvalidMonthException() {
        super();
    }

    public InvalidMonthException(String message) {
        super(message);
    }

    public InvalidMonthException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMonthException(Throwable cause) {
        super(cause);
    }

    protected InvalidMonthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
