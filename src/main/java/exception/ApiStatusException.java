package exception;

public class ApiStatusException extends ApiException {

    private final int statusCode;

    public ApiStatusException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
