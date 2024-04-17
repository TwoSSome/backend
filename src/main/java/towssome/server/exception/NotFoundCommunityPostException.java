package towssome.server.exception;

public class NotFoundCommunityPostException extends RuntimeException{
    public NotFoundCommunityPostException() {
        super();
    }

    public NotFoundCommunityPostException(String message) {
        super(message);
    }

    public NotFoundCommunityPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCommunityPostException(Throwable cause) {
        super(cause);
    }

    protected NotFoundCommunityPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
