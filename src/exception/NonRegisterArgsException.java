package exception;

public class NonRegisterArgsException extends RuntimeException {
    public NonRegisterArgsException(String s) {
        super(s);
    }
}