package exception;

public class UnrecognizedTypeException extends RuntimeException {
    public UnrecognizedTypeException(String msg) {
        super(msg);
    }
}
