package build.dream.catering.exceptions;

public class CanNotDeleteException extends RuntimeException {
    public CanNotDeleteException() {
    }

    public CanNotDeleteException(String message) {
        super(message);
    }

    public CanNotDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotDeleteException(Throwable cause) {
        super(cause);
    }

    public CanNotDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
