package com.dvelop.sdk.tenant.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import java.nio.charset.Charset;
import java.util.Base64;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TenantFilterTest {

    private final String givenTenantId = "4711";
    private final String givenSystemBaseUri = "https://sample.mydomain.de";
    private final String givenDefaultBaseUri = "https://default.mydomain.de";
    private final String givenDefaultTenantId = "0";

    private class TenantFilterImpl extends TenantFilter {
        String tenantId;
        String baseUri;

        public TenantFilterImpl(byte[] signatureSecret, String fallbackBaseuri, String fallbackTenantId) {
            super(signatureSecret, fallbackBaseuri, fallbackTenantId);
        }

        @Override
        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        @Override
        public void setBaseUri(String baseUri) {
            this.baseUri = baseUri;
        }
    }

    private static final String HEADER_DV_TENANT_ID = "x-dv-tenant-id";
    private static final String HEADER_DV_BASEURI = "x-dv-baseuri";
    private static final String HEADER_DV_SIG_1 = "x-dv-sig-1";

    private static final byte[] signatureSecret = new byte[]{(byte) 0xa6, (byte) 0xdb, (byte) 0x90, (byte) 0xd1, (byte) 0xbd, (byte) 0x1, (byte) 0xb2, (byte) 0x49, (byte) 0x8b, (byte) 0x2f, (byte) 0x15, (byte) 0xec, (byte) 0x8e, (byte) 0x38, (byte) 0x47, (byte) 0xf5, (byte) 0x2b, (byte) 0xbc, (byte) 0xa3, (byte) 0x34, (byte) 0xef, (byte) 0x66, (byte) 0x5e, (byte) 0x99, (byte) 0xff, (byte) 0x9f, (byte) 0xc7, (byte) 0x95, (byte) 0xa3, (byte) 0x91, (byte) 0xa1, (byte) 0x18};

    @Mock
    ContainerRequestContext request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private String base64Signature(String message, byte[] secret) throws Exception {
        String hmacSHA256 = "HmacSHA256";
        SecretKeySpec signingKey = new SecretKeySpec(secret, hmacSHA256);
        Mac instance = Mac.getInstance(hmacSHA256);
        instance.init(signingKey);
        byte[] signatureBytes = instance.doFinal(message.getBytes(Charset.forName("UTF-8")));
        String s = Base64.getEncoder().encodeToString(signatureBytes);
        System.out.println(s);
        return s;
    }

    @Test
    void tenantHeadersGivenAndEmptyDefaultValues_UsesHeaders() throws Exception {

        String givenSignature = "HqGWpZmB3QE8nvhu8t+A2T9Ao64eLMjn8Hc/gIxyug0=";

        String givenDefaultBaseUri = "";
        String givenDefaultTenantId = "";

        when(request.getHeaderString(HEADER_DV_BASEURI)).thenReturn(givenSystemBaseUri);
        when(request.getHeaderString(HEADER_DV_TENANT_ID)).thenReturn(givenTenantId);
        when(request.getHeaderString(HEADER_DV_SIG_1)).thenReturn(givenSignature);

        TenantFilterImpl tfi = new TenantFilterImpl(signatureSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        assertThat(tfi.tenantId, is(equalTo(givenTenantId)));
        assertThat(tfi.baseUri, is(equalTo(givenSystemBaseUri)));
    }

    @Test
    void noTenantHeadersAndDefaultValuesGiven_UsesDefaultValues() {
        String givenDefaultTenantId = "0";

        TenantFilterImpl tfi = new TenantFilterImpl(signatureSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        assertThat(tfi.baseUri, is(equalTo(givenDefaultBaseUri)));
        assertThat(tfi.tenantId, is(equalTo(givenDefaultTenantId)));
    }

    @Test
    void tenantHeadersAndDefaultValuesGiven_UsesHeaders() {

        String givenSignature = "HqGWpZmB3QE8nvhu8t+A2T9Ao64eLMjn8Hc/gIxyug0=";

        String givenDefaultTenantId = "0";

        when(request.getHeaderString(HEADER_DV_BASEURI)).thenReturn(givenSystemBaseUri);
        when(request.getHeaderString(HEADER_DV_TENANT_ID)).thenReturn(givenTenantId);
        when(request.getHeaderString(HEADER_DV_SIG_1)).thenReturn(givenSignature);

        TenantFilterImpl tfi = new TenantFilterImpl(signatureSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        assertThat(tfi.tenantId, is(equalTo(givenTenantId)));
        assertThat(tfi.baseUri, is(equalTo(givenSystemBaseUri)));
    }

    @Test
    void noHeadersGivenAndEmptyDefaultValues_GivesEmptyValues() {

        String givenDefaultBaseUri = "";
        String givenDefaultTenantId = "";

        TenantFilterImpl tfi = new TenantFilterImpl(signatureSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        assertThat(tfi.baseUri, is(equalTo(givenDefaultBaseUri)));
        assertThat(tfi.tenantId, is(equalTo(givenDefaultTenantId)));

    }

    @Test
    void noHeadersButDefaultSystemBaseUriAndNoSignatureSecretKey_UsesDefaultBaseUriAndTenantIdZero() {
        String givenDefaultBaseUri = "";
        String givenDefaultTenantId = "0";

        TenantFilterImpl tfi = new TenantFilterImpl(null, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        assertThat(tfi.baseUri, is(equalTo(givenDefaultBaseUri)));
        assertThat(tfi.tenantId, is(equalTo(givenDefaultTenantId)));
    }

    @Test
    void wrongDataSignedWithValidSignatureKey_Returns403() throws Exception {
        String givenSignature = "CGiFEpK7w2xHJ5vij14CCYvr4B9oGvK6msKTdpQdMf0=";

        String givenDefaultTenantId = "0";

        when(request.getHeaderString(HEADER_DV_BASEURI)).thenReturn(givenSystemBaseUri);
        when(request.getHeaderString(HEADER_DV_TENANT_ID)).thenReturn(givenTenantId);
        when(request.getHeaderString(HEADER_DV_SIG_1)).thenReturn(givenSignature);

        TenantFilterImpl tfi = new TenantFilterImpl(signatureSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        verify(request).abortWith(any());
    }

    @Test
    void noneBase64Signature_Returns403() {
        String givenSignature = "acbdef";

        String givenDefaultTenantId = "0";

        when(request.getHeaderString(HEADER_DV_SIG_1)).thenReturn(givenSignature);
        when(request.getHeaderString(HEADER_DV_BASEURI)).thenReturn(givenSystemBaseUri);
        when(request.getHeaderString(HEADER_DV_TENANT_ID)).thenReturn(givenTenantId);

        TenantFilterImpl tfi = new TenantFilterImpl(signatureSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), allOf(
                hasProperty("status", is(403))
        ));
    }

    @Test
    void wrongSignatureKey_Returns403() {
        String givenSignature = "CGiFEpK7w2xHJ5vij14CCYvr4B9oGvK6msKTdpQdMf0=";

        String givenDefaultTenantId = "0";

        when(request.getHeaderString(HEADER_DV_BASEURI)).thenReturn(givenSystemBaseUri);
        when(request.getHeaderString(HEADER_DV_TENANT_ID)).thenReturn(givenTenantId);
        when(request.getHeaderString(HEADER_DV_SIG_1)).thenReturn(givenSignature);

        byte[] differentSecret = new byte[]{(byte) 0xa1, (byte) 0xab, (byte) 0xcd, (byte) 0xef, (byte) 0x01, (byte) 0x12, (byte) 0x23, (byte) 0x49, (byte) 0x8b, (byte) 0x2f, (byte) 0x15, (byte) 0xec, (byte) 0x8e, (byte) 0x38, (byte) 0x47, (byte) 0xf5, (byte) 0x2b, (byte) 0xbc, (byte) 0xa3, (byte) 0x34, (byte) 0xef, (byte) 0x66, (byte) 0x5e, (byte) 0x99, (byte) 0xff, (byte) 0x9f, (byte) 0xc7, (byte) 0x95, (byte) 0xa3, (byte) 0x91, (byte) 0xa1, (byte) 0x18};
        TenantFilterImpl tfi = new TenantFilterImpl(differentSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), allOf(
                hasProperty("status", is(403))
        ));
    }

    @Test
    void headersWithoutSignature_Returns403() {
        String givenTenantId = "4711";

        String givenDefaultTenantId = "0";

        when(request.getHeaderString(HEADER_DV_BASEURI)).thenReturn(givenSystemBaseUri);
        when(request.getHeaderString(HEADER_DV_TENANT_ID)).thenReturn(givenTenantId);

        byte[] differentSecret = new byte[]{(byte) 0xa1, (byte) 0xab, (byte) 0xcd, (byte) 0xef, (byte) 0x01, (byte) 0x12, (byte) 0x23, (byte) 0x49, (byte) 0x8b, (byte) 0x2f, (byte) 0x15, (byte) 0xec, (byte) 0x8e, (byte) 0x38, (byte) 0x47, (byte) 0xf5, (byte) 0x2b, (byte) 0xbc, (byte) 0xa3, (byte) 0x34, (byte) 0xef, (byte) 0x66, (byte) 0x5e, (byte) 0x99, (byte) 0xff, (byte) 0x9f, (byte) 0xc7, (byte) 0x95, (byte) 0xa3, (byte) 0x91, (byte) 0xa1, (byte) 0x18};
        TenantFilterImpl tfi = new TenantFilterImpl(differentSecret, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), allOf(
                hasProperty("status", is(403))
        ));
    }

    @Test
    void headersAndNoSignatureSecretKey_Returns500() {
        String givenSignature = "CGiFEpK7w2xHJ5vij14CCYvr4B9oGvK6msKTdpQdMf0=";
        String givenDefaultTenantId = "0";

        when(request.getHeaderString(HEADER_DV_BASEURI)).thenReturn(givenSystemBaseUri);
        when(request.getHeaderString(HEADER_DV_TENANT_ID)).thenReturn(givenTenantId);
        when(request.getHeaderString(HEADER_DV_SIG_1)).thenReturn(givenSignature);

        TenantFilterImpl tfi = new TenantFilterImpl(null, givenDefaultBaseUri, givenDefaultTenantId);

        tfi.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), allOf(
                hasProperty("status", is(500))
        ));
    }
}
