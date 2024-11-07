package ExceptionHandler.Exceptions;

public class AbsentFormException extends RuntimeException{
    String message;

    public AbsentFormException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
