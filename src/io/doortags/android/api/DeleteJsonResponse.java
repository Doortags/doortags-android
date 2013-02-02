package io.doortags.android.api;

import io.doortags.android.utils.Tuple;

import java.io.IOException;

public class DeleteJsonResponse extends JsonResponse {
    private int responseCode;
    private String body;

    private DeleteJsonResponse(int responseCode, String body) {
        super(responseCode, body);
    }

    public static DeleteJsonResponse makeRequest(String resource, String contents)
            throws IOException {
        Tuple<Integer, String> result = ApiUtils.requestHelper("DELETE", resource,
                contents);
        return new DeleteJsonResponse(result.getFirst(), result.getSecond());
    }

    public static DeleteJsonResponse makeRequest(String resource,
                                               ApiUtils.Params urlParams,
                                               String contents) throws IOException {
        return makeRequest(ApiUtils.concatQuery(resource, urlParams), contents);
    }

    public static DeleteJsonResponse makeAuthRequest(String resource,
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
