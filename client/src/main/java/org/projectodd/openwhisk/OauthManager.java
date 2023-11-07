package org.projectodd.openwhisk;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.Map.Entry;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


public class OauthManager implements Interceptor, Authenticator {

    private final OkHttpClient clientForToken = new OkHttpClient();
    private final JsonParser jsonParser = new JsonParser();
    private final String tokenUrl;
    private final Map<String, String> formParameters;
    private final Map<String, String> headers;

    private String token;

    public OauthManager(String tokenUrl,
            Map<String, String> formParameters,
            Map<String, String> headers) throws IOException {
        this.tokenUrl = tokenUrl;
        this.formParameters = formParameters;
        this.headers = headers;
        token = getNewToken();
    }

    private String getNewToken() throws IOException {

        StringBuilder bodyBuilder = new StringBuilder();
        for (Entry<String, String> parameter : formParameters.entrySet()) {
            if (bodyBuilder.length() > 0) {
                bodyBuilder.append("&");
            }
            bodyBuilder.append(parameter.getKey() + "=" +
                    parameter.getValue());
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(tokenUrl)
                .post(RequestBody.create(
                        MediaType.parse("application/x-www-form-urlencoded"),
                        bodyBuilder.toString()));

        for (Entry<String, String> header : headers.entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }
        requestBuilder.header("Accept", "application/json");
        requestBuilder.header("Content-Type",
                "application/x-www-form-urlencoded");
        Request request = requestBuilder.build();

        Response response = clientForToken.newCall(request).execute();
        JsonObject parsedResponse = jsonParser.parse(
                response.body().string()).getAsJsonObject();
        return parsedResponse.get("access_token").getAsString();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(request);
    }

    @Override
    public Request authenticate(Proxy route, Response response) throws IOException {
        if (!response.request().header("Authorization").equals(token)) {
            return null;
        }
        token = getNewToken();
        if (token != null) {
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        } else {
            return null;
        }
    }

    @Override
    public Request authenticateProxy(Proxy arg0, Response arg1) throws IOException {
        return authenticate(arg0, arg1);
    }
}
