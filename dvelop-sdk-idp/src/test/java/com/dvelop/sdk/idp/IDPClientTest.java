package com.dvelop.sdk.idp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

class IDPClientTest {

    @Mock
    Client client;

    @Mock
    Response response;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        Mockito.when(builder.get()).thenReturn(response);
        Mockito.when(builder.post(ArgumentMatchers.any())).thenReturn(response);
        Mockito.when(builder.put(ArgumentMatchers.any())).thenReturn(response);
        Mockito.when(builder.delete()).thenReturn(response);

        final WebTarget webTarget = Mockito.mock(WebTarget.class);
        Mockito.when(webTarget.path(anyString())).thenReturn(webTarget);
        Mockito.when(webTarget.request(anyString())).thenReturn(builder);

        Mockito.when(client.target(anyString())).thenReturn(webTarget);
    }

//    @Test
//    void testIsAuthSessionIdValid_AuthSessionIdIsValid_ReturnsTrue() {
//
//        when(response.getStatus()).thenReturn(200);
//
//        IDPClient idpClient = new IDPClient(client, "1234");
//        boolean isAuthSessionIdValid = idpClient.validate(false);
//
//        assertThat(isAuthSessionIdValid, is(true));
//    }
//
//    @Test
//    void testIsAuthSessionIdValid_AuthSessionIdIsInvalid_ReturnsFalse(){
//        when(response.getStatus()).thenReturn(401);
//
//        IDPClient idpClient = new IDPClient(client, "1234");
//        boolean isAuthSessionIdValid = idpClient.validate(false);
//
//        assertThat(isAuthSessionIdValid, is(false));
//    }

}