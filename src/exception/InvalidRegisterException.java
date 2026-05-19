package exception;

public class InvalidRegisterException extends RuntimeException {
    public InvalidRegisterException(String msg) {
        super(msg);
    }
}
