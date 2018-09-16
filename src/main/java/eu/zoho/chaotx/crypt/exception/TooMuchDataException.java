package eu.zoho.chaotx.crypt.exception;

public class TooMuchDataException extends IllegalArgumentException {
    private static final long serialVersionUID = 42;

    public TooMuchDataException(String msg) {
        super(msg);
    }
}