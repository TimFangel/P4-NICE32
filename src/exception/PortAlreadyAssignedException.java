package exception;

public class PortAlreadyAssignedException extends RuntimeException {
    public PortAlreadyAssignedException(String msg) {
        super(msg);
    }
}
