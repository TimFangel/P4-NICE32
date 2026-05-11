package exception;

public class MissingLabelException extends RuntimeException {
    public MissingLabelException(String msg) {
        super(msg);
    }
}
