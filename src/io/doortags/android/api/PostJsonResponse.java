package io.doortags.android.api;

import java.io.IOException;

import static io.doortags.android.api.ApiUtils.Params;
import static io.doortags.android.api.ApiUtils.Tuple;

class PostJsonResponse extends JsonResponse{
    private int responseCode;
    private String body;

    private PostJsonResponse(int responseCode, String body) {
        super(responseCode, body);
    }

    public static PostJsonResponse makeRequest(String resource, String contents)
            throws IOException {
        Tuple<Integer, String> result = ApiUtils.requestHelper("POST", resource,
                contents);
        return new PostJsonResponse(result.getFirst(), result.getSecond());
    }

    public static PostJsonResponse makeRequest(String resource,
                                               Params urlParams,
                                               String contents) throws IOException {
        return makeRequest(ApiUtils.concatQuery(resource, urlParams), contents);
    }

    public static PostJsonResponse makeAuthRequest(String resource,
                                                   String token,
                                                   Params urlParams,
                                                   String contents) throws IOException {
        if (urlParams == null) {
            Params params = Params.start("auth_token", token);
            return makeRequest(resource, params, contents);
        } else {
            urlParams.addParam("auth_token", token);
            return makeRequest(resource, urlParams, contents);
        }
    }
}