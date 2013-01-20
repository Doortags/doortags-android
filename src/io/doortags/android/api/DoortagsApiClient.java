package io.doortags.android.api;

import java.io.IOException;

import static io.doortags.android.api.ApiUtils.Params;

public class DoortagsApiClient {
    private static final int HTTP_OK                  = 200,
                             HTTP_BAD_REQUEST         = 400,
                             HTTP_UNAUTHORIZED        = 401,
                             HTTP_NOT_FOUND           = 404,
                             HTTP_SERVER_OOPS         = 500,
                             HTTP_SERVICE_UNAVAILABLE = 503;

    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    private DoortagsApiClient (String authToken) {
        this.authToken = authToken;
    }

    private static class JsonStatusResponse {
        private int status;
        private String msg;

        JsonStatusResponse () {};
    }

    private static void defaultErrorHandler (JsonResponse response) throws
            DoortagsApiException {
        JsonStatusResponse jsr = response.fromJson(JsonStatusResponse.class);

        switch (jsr.status) {
            case HTTP_BAD_REQUEST:
                throw new BadRequestException(jsr.msg);
            case HTTP_UNAUTHORIZED:
                throw new UnauthorizedException(jsr.msg);
            case HTTP_SERVER_OOPS:
                throw new InternalServerException(jsr.msg);
            case HTTP_SERVICE_UNAVAILABLE:
                throw new InternalServerException(jsr.msg);
            default:
                String message = String.format("(%i) %s", jsr.status, jsr.msg);
                throw new DoortagsApiException(message);
        }
    }

    public static DoortagsApiClient authorize (String email, String password)
            throws IOException, DoortagsApiException {
        String params = Params.start("email", email)
                              .addParam("password", password)
                              .finish();
        JsonResponse response = PostJsonResponse.makeRequest("/auth", params);

        if (response.getResponseCode() == HTTP_OK) {
            AuthData data = response.fromJson(AuthData.class);
            return new DoortagsApiClient(data.token);
        }

        // errors
        JsonStatusResponse jsr = response.fromJson(JsonStatusResponse.class);
        switch (jsr.status) {
            case HTTP_BAD_REQUEST:
                throw new BadRequestException(jsr.msg);
            case HTTP_UNAUTHORIZED:
                throw new UnauthorizedException(jsr.msg);
            default:
                String message = String.format("(%i) %s", jsr.status, jsr.msg);
                throw new DoortagsApiException(message);
        }
    }
    
    public void sendMessage (String message, String phoneNumber, int tag_code, String name) 
    		throws IOException, DoortagsApiException {
    	Params params = Params.start("message", message)
    						  .addParam("tag_id", tag_code)
    						  .addParam("name", String.format("%s (%s)", name,
                                      phoneNumber));
    	JsonResponse response = PostJsonResponse.makeAuthRequest("/message", authToken, params, null);
    	if (response.getResponseCode() == HTTP_OK) {
    		return;
    	}
    	
    	defaultErrorHandler(response);
    }
    
    public Tag[] getTagsOfUser() throws IOException, DoortagsApiException{
    	JsonResponse response = GetJsonResponse.makeAuthRequest("/tags", authToken, null);
    	
    	if (response.getResponseCode() == HTTP_OK) {
    		TagInfo userTags = response.fromJson(TagInfo.class);
    		return userTags.tags;
    	}
    	
    	switch (response.responseCode) {
    	case HTTP_NOT_FOUND:
    		return null;
    	default:
    		defaultErrorHandler(response);
    		return null;
    	}
    }
    
    private static class TagInfo{
    	private int status;
    	private Tag[] tags;
    	
    	TagInfo() {}
    }
    
    
    private class AuthData {
        private String token;
        AuthData() {};
    }

    public static DoortagsApiClient fromAuthToken (String authToken) {
        return new DoortagsApiClient(authToken);
    }

    public boolean invalidate() throws IOException {
        JsonResponse response = PostJsonResponse.makeAuthRequest("/auth/invalidate",
                authToken, null, null);
        return response.getResponseCode() == HTTP_OK;
    }
    
    public static Tag getTag(int tagCode) throws IOException, DoortagsApiException {
    	JsonResponse response = GetJsonResponse.makeRequest("/tags/" + tagCode);
    	
    	if (response.getResponseCode() == HTTP_OK) {
    		Tag tag = response.fromJson(Tag.class);
    		return tag;
    	}
    	
    	switch (response.responseCode) {
    	case HTTP_NOT_FOUND:
    		return null;
    	default:
    		defaultErrorHandler(response);
    		return null;
    	}
    }


    public static void main (String[] args) throws IOException, DoortagsApiException {
        DoortagsApiClient client = DoortagsApiClient.authorize("CHANGE_THIS_EMAIL",
                "CHANGE_THIS_PASSWORD");
        Tag tag = client.getTag(2);
        System.out.println(tag.getLocation());
    }
}
