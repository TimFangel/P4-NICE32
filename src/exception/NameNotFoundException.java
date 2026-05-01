package exception;

public class NameNotFoundException extends RuntimeException {
    public NameNotFoundException(String msg) {
        super(msg);
    }
}
