package io.doortags.android.api;

public class BadRequestException extends DoortagsApiException {
    public BadRequestException() {
        super();
    }

    public BadRequestException(Throwable tr) {
        super(tr);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable tr) {
        super(message, tr);
    }
}
