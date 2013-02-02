package io.doortags.android.api;

import io.doortags.android.utils.Tuple;
import io.doortags.android.utils.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

class ApiUtils {
    // FIXME: USE HTTPS
    public static final String API_URL = "http://www.getdoortags.com/api";

    public static URL endpoint (String resource) throws MalformedURLException {
        return new URL(API_URL + resource);
    }

    static class Params {
        private StringBuilder result = new StringBuilder("");
        private Params() {}

        public Params addParam (String field, String value) {
            if (result.length() > 0) {
                result.append('&');
            }

            result.append(field);
            result.append('=');
            try {
                result.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return this;
        }

        public Params addParam (String field, int value) {
            return addParam(field, String.valueOf(value));
        }

        public static Params start() {
            return new Params();
        }

        public static Params start (String field, String value) {
            return Params.start().addParam(field, value);
        }

        public static Params start (String field, int value) {
            return Params.start().addParam(field, value);
        }

        public boolean isEmpty() {
            return result.length() == 0;
        }

        public String finish() {
            return this.toString();
        }

        @Override
        public String toString() {
            return result.toString();
        }
    }

    public static String concatQuery (String resource, Params query) {
        String res;
        if (query == null || query.isEmpty()) {
            res = resource;
        } else {
            res = resource + '?' + query.finish();
        }

        return res;
    }

    public static Tuple<Integer, String> requestHelper (String method, String resource,
                                                        String contents) throws
            IOException {
        contents = (contents == null) ? "" : contents;

        HttpURLConnection connection =
                (HttpURLConnection) ApiUtils.endpoint(resource).openConnection();

        try {
            connection.setDoOutput(true); // sets this to make a POST request
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept", Utils.MIME_JSON);
            connection.setRequestProperty("Content-Type", Utils.MIME_FORM);
            connection.setRequestProperty("Content-Length", String.valueOf(contents.length()));

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(contents.getBytes());
            out.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            String responseBody = Utils.inputStreamToString(in);
            in.close();

            return new Tuple<Integer, String>(connection.getResponseCode(), responseBody);
        } finally {
            connection.disconnect();
        }
    }
}
