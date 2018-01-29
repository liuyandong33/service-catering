package build.dream.catering.exceptions;

public class CanNotEditAndDeleteException extends RuntimeException {
    public CanNotEditAndDeleteException() {
    }

    public CanNotEditAndDeleteException(String message) {
        super(message);
    }

    public CanNotEditAndDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotEditAndDeleteException(Throwable cause) {
        super(cause);
    }

    public CanNotEditAndDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
