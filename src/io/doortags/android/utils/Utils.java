package io.doortags.android.utils;

import java.io.InputStream;
import java.util.Scanner;

public class Utils {
    public static final String MIME_JSON = "application/json";
    public static final String MIME_FORM = "application/x-www-form-urlencoded";
    public static final String MIME = "application/io.doortags.android";

    public static final String SENDER_ID = "343180867553";

    /* Fragment tags for dialogs */
    public static final String FTAG_PROGRESS  = "progress_dialog";
    public static final String FTAG_EDIT_CARD = "edit_card";
    public static final String FTAG_NFC_WRITE = "write_tag";
    public static final String FTAG_ADD_TAG   = "add_tag";
    public static final String FTAG_MESSAGE   = "message_dialog";

    /* Fragment tags for other fragments */
    public static final String FTAG_TAGMAN = "tag_manager";


    /**
     * Reads an InputStream into a String
     *
     * @param in    The InputStream
     * @return  A String representation of the contents of the stream, or
     *          null if the stream is empty.
     */
    public static String inputStreamToString (InputStream in) {
        return inputStreamToString(in, "UTF-8");
    }

    /**
     * Same as the above {@link #inputStreamToString(java.io.InputStream)},
     * except that one can specify the encoding.
     *
     * @param in    The InputStream
     * @param encoding  The encoding to use
     * @return  A String representation of the contents of the stream, or
     *          null if the stream is empty.
     */
    public static String inputStreamToString (InputStream in, String encoding) {
        Scanner s = new Scanner(in, encoding).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
