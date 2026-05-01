package exception;

public class NameAlreadyBoundException extends RuntimeException {
    public NameAlreadyBoundException(String msg) {
        super(msg);
    }
}
