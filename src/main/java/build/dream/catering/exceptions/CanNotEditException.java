package build.dream.catering.exceptions;

public class CanNotEditException extends RuntimeException {
    public CanNotEditException() {
    }

    public CanNotEditException(String message) {
        super(message);
    }

    public CanNotEditException(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotEditException(Throwable cause) {
        super(cause);
    }

    public CanNotEditException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
