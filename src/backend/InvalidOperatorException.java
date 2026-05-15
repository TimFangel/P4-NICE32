package backend;

class InvalidOperatorException extends RuntimeException {
    public InvalidOperatorException(String s) {
        super(s);
    }
}