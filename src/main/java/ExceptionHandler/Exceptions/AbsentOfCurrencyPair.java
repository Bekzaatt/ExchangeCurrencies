package ExceptionHandler.Exceptions;

public class AbsentOfCurrencyPair extends RuntimeException{
    String message;
    public AbsentOfCurrencyPair(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
