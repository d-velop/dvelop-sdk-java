package com.dvelop.sdk.common.http.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.mockito.Mockito.*;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DvelopSdkOriginHeaderClientFilterTest {

    @Mock
    ClientRequestContext requestContext;

    @Test
    void requestHasOriginHeader_keepsHeaderValue() throws URISyntaxException, IOException {
        MockitoAnnotations.initMocks(this);

        DvelopSdkOriginHeaderClientFilter filter = new DvelopSdkOriginHeaderClientFilter();

        when(requestContext.getUri()).thenReturn(new URI("https://www.irgendwo.cloud"));
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        MultivaluedMap<String, Object> spy = spy(map);

        map.put("Origin", Arrays.asList("https://www.woanders.de"));
        when(requestContext.getHeaders()).thenReturn(map);

        filter.filter(requestContext);

        verifyZeroInteractions(spy);
        assertThat(map, hasEntry(is("Origin"), hasItem(is("https://www.woanders.de"))));
    }


    @Test
    void requestHasNoOriginHeader_addsOriginHeaderFromRequestUri() throws URISyntaxException, IOException {
        MockitoAnnotations.initMocks(this);

        DvelopSdkOriginHeaderClientFilter filter = new DvelopSdkOriginHeaderClientFilter();

        when(requestContext.getUri()).thenReturn(new URI("https://www.irgendwo.cloud"));

        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        MultivaluedMap<String, Object> spy = spy(map);
        when(requestContext.getHeaders()).thenReturn(spy);

        filter.filter(requestContext);

        assertThat(map, hasEntry(is("Origin"), hasItem(is("https://www.irgendwo.cloud"))));
    }


}