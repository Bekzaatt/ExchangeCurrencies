package ExceptionHandler.Exceptions;

public class DBException extends RuntimeException{
    String message;
    public DBException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
