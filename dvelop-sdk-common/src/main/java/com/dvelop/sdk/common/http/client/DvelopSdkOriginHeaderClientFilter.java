package com.dvelop.sdk.common.http.client;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

public class DvelopSdkOriginHeaderClientFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext request) throws IOException {
        request.getHeaders().computeIfAbsent("Origin", s -> {
            String baseUri = request.getUri().resolve("/").toString();
            while(baseUri.endsWith("/")){
                baseUri = baseUri.substring(0, baseUri.length()-1);
            }
            return Arrays.asList(baseUri);
        });
    }

}
