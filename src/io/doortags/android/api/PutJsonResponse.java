package io.doortags.android.api;

import java.io.IOException;

public class PutJsonResponse extends JsonResponse {
    public PutJsonResponse(int responseCode, String body) {
        super(responseCode, body);
    }

    public static PutJsonResponse makeRequest (String resource, String contents)
            throws IOException {
        ApiUtils.Tuple<Integer, String> result = ApiUtils.requestHelper("PUT", resource,
                contents);
        return new PutJsonResponse(result.getFirst(), result.getSecond());
    }

    public static PutJsonResponse makeRequest(String resource,
                                               ApiUtils.Params urlParams,
                                               String contents) throws IOException {
        return makeRequest(ApiUtils.concatQuery(resource, urlParams), contents);
    }

    public static PutJsonResponse makeAuthRequest(String resource,
                                                   String token,
                                                   ApiUtils.Params urlParams,
                                                   String contents) throws IOException {
        if (urlParams == null) {
            ApiUtils.Params params = ApiUtils.Params.start("auth_token", token);
            return makeRequest(resource, params, contents);
        } else {
            urlParams.addParam("auth_token", token);
            return makeRequest(resource, urlParams, contents);
        }
    }
}
