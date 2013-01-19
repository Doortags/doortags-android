package io.doortags.android.api;

public class DoortagsApiException extends Exception {
    public DoortagsApiException () {
        super();
    }

    public DoortagsApiException (String msg) {
        super(msg);
    }

    public DoortagsApiException (String msg, Throwable tr) {
        super(msg, tr);
    }

    public DoortagsApiException (Throwable tr) {
        super(tr);
    }
}
