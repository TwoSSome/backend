package towssome.server.exception;

public class NotFoundDateCourseException extends NotFoundEntityException {

    public NotFoundDateCourseException() {
        super();
    }

    public NotFoundDateCourseException(String message) {
        super(message);
    }

    public NotFoundDateCourseException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundDateCourseException(Throwable cause) {
        super(cause);
    }

    protected NotFoundDateCourseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
