package hub.lol.flargs.exceptions;

public class BuildException extends RuntimeException {
    public BuildException(String message) {
        super(message);
    }

    public BuildException(Throwable cause) {
        super(cause);
    }
}
