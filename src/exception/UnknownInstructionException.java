package exception;

public class UnknownInstructionException extends RuntimeException {
    public UnknownInstructionException(String msg) {
        super(msg);
    }
}