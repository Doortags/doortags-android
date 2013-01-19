package io.doortags.android.api;

public class InternalServerException extends DoortagsApiException {
    public InternalServerException() {
        super();
    }

    public InternalServerException(String msg) {
        super(msg);
    }

    public InternalServerException(String msg, Throwable tr) {
        super(msg, tr);
    }

    public InternalServerException(Throwable tr) {
        super(tr);
    }
}
