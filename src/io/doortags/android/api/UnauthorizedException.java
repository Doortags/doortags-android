package io.doortags.android.api;

public class UnauthorizedException extends DoortagsApiException {
    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(Throwable tr) {
        super(tr);
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable tr) {
        super(message, tr);
    }
}
