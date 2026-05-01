package exception;

public class NonMatchingTypeException extends RuntimeException {
    public NonMatchingTypeException(String msg) {
        super(msg);
    }
}
