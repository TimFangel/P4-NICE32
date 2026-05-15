package backend;

class NonRegisterArgsException extends RuntimeException {
    public NonRegisterArgsException(String s) {
        super(s);
    }
}