package exception;

public class UnrecognizedOperatorException extends RuntimeException {
    public UnrecognizedOperatorException(String msg) {
        super(msg);
    }
}
