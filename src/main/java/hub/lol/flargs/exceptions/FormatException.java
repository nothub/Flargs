package hub.lol.flargs.exceptions;

public class FormatException extends RuntimeException {
    public FormatException(String message) {
        super(message);
    }

    public FormatException(Throwable cause) {
        super(cause);
    }
}
