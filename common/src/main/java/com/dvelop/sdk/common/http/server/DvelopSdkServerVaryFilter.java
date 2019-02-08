package com.dvelop.sdk.common.http.server;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class DvelopSdkServerVaryFilter implements ContainerResponseFilter {

    private static final String HEADER_DV_SIG_1 = "x-dv-sig-1";

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        response.getHeaders().add("vary", HEADER_DV_SIG_1);
    }
}
