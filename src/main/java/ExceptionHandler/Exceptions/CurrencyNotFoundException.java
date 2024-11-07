package ExceptionHandler.Exceptions;

public class CurrencyNotFoundException extends RuntimeException{
    String message;
    public CurrencyNotFoundException(String message){
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
