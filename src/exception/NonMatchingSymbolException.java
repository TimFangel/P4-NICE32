package exception;

public class NonMatchingSymbolException extends RuntimeException {
    public NonMatchingSymbolException(String msg) {
        super(msg);
    }
}
