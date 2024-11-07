package ExceptionHandler.Exceptions;

public class SuchCurrenciesExists extends RuntimeException{
    String message;

    public SuchCurrenciesExists(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
