package com.dvelop.sdk.common.http.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DvelopSdkServerVaryFilterTest {

    @Mock
    ContainerRequestContext request;

    @Mock
    ContainerResponseContext response;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testNoVaryHeaderExists_addsVaryHeader() throws IOException {
        MultivaluedHashMap<String, Object> responseHeaders = new MultivaluedHashMap<>();

        when(response.getHeaders()).thenReturn(responseHeaders);

        new DvelopSdkServerVaryFilter().filter(request, response);

        assertThat(responseHeaders.get("vary"), hasItems("x-dv-sig-1"));
    }

    @Test
    void testVaryHeaderIsEmptyString_addsVaryHeader() throws IOException {
        MultivaluedHashMap<String, Object> responseHeaders = new MultivaluedHashMap<>();

        when(response.getHeaders()).thenReturn(responseHeaders);

        new DvelopSdkServerVaryFilter().filter(request, response);

        assertThat(responseHeaders.get("vary"), hasItems("x-dv-sig-1"));
    }

    @Test
    void testVaryHeaderExists_addsToExistingVaryHeader() throws IOException {
        MultivaluedHashMap<String, Object> responseHeaders = new MultivaluedHashMap<>();
        responseHeaders.add("vary", "something");

        when(response.getHeaders()).thenReturn(responseHeaders);

        new DvelopSdkServerVaryFilter().filter(request, response);

        assertThat(responseHeaders.get("vary"), hasItems("x-dv-sig-1","something"));
    }

    @Test
    void testVaryHeaderExistsAndHasDvSigV1_doesNotAddSecond() throws IOException {
        MultivaluedHashMap<String, Object> responseHeaders = new MultivaluedHashMap<>();
        responseHeaders.add("vary", "x-dv-sig-1");

        when(response.getHeaders()).thenReturn(responseHeaders);

        new DvelopSdkServerVaryFilter().filter(request, response);

        assertThat(responseHeaders.get("vary"), hasItems("x-dv-sig-1"));
    }

}