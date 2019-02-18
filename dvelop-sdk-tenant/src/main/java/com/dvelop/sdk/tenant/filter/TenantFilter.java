package com.dvelop.sdk.tenant.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * A base class to interpret tenant header of the d.velop multitenancy model.
 * Implement this abstract class and add it to your jax-rs pipeline, to have it verify the signature (x-dv-sig-1) and
 * reject the request with a status 403 if the signature does not match. Use the {@link #setBaseUri(String)} and
 * {@link #setTenantId(String)} methods to store the values of x-dv-baseuri and x-dv-tenant-id for this request
 * somewhere.
 * Ensure this filter is called before any other filter, for example using the {@link javax.ws.rs.container.PreMatching}
 * annotation.
 *
 * <pre>
 * public class InjectableTenantFilter extends TenantFilter {
 *
 *     public void filter(ContainerRequestContext request){
 *         setSignatureSecret(someBytes);
 *         super.filter(request);
 *     }
 *
 *     public void setTenantId(String s){
 *         System.out.println("Tenant id is "+s);
 *     }
 *
 *     public void setBaseUri(String s){
 *         System.out.println("Baseuri is "+s);
 *     }
 * }
 * </pre>
 *
 */
public abstract class TenantFilter implements ContainerRequestFilter {
    String HMAC_SIGNATURE_ALGORITHM = "HmacSHA256";

    private static final String HEADER_DV_TENANT_ID = "x-dv-tenant-id";
    private static final String HEADER_DV_BASEURI = "x-dv-baseuri";
    private static final String HEADER_DV_SIG_1 = "x-dv-sig-1";

    private final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
    private String defaultBaseUri;
    private String defaultTenantId;
    private byte[] signatureSecret;

    public TenantFilter(byte[] signatureSecret, String defaultBaseUri, String defaultTenantId) {
        this.signatureSecret = signatureSecret;
        this.defaultBaseUri = defaultBaseUri;
        this.defaultTenantId = defaultTenantId;
    }

    public String getDefaultBaseUri() {
        return defaultBaseUri;
    }

    public void setDefaultBaseUri(String defaultBaseUri) {
        this.defaultBaseUri = defaultBaseUri;
    }

    public String getDefaultTenantId() {
        return defaultTenantId;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    public byte[] getSignatureSecret() {
        return signatureSecret;
    }

    public void setSignatureSecret(byte[] signatureSecret) {
        this.signatureSecret = signatureSecret;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        String systemBaseUri = request.getHeaderString(HEADER_DV_BASEURI);
        String tenantId = request.getHeaderString(HEADER_DV_TENANT_ID);

        if (systemBaseUri == null && tenantId == null) {
            setBaseUri(defaultBaseUri);
            setTenantId(defaultTenantId);
            return;
        }

        if (systemBaseUri == null) {
            systemBaseUri = defaultBaseUri;
        }

        if (tenantId == null) {
            tenantId = defaultTenantId;
        }

        setBaseUri(systemBaseUri);
        setTenantId(tenantId);

        String givenSignature = request.getHeaderString(HEADER_DV_SIG_1);
        String expectedSignature = null;
        try {
            expectedSignature = base64Signature(systemBaseUri + tenantId);
        } catch (Exception e) {
            logger.error("Failed to calculate signature: {}", e.getMessage());
            request.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
            return;
        }

        if (!expectedSignature.equals(givenSignature)) {
            logger.warn("Invalid signature {}", givenSignature);
            request.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            return;
        }
    }

    private String base64Signature(String message) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(signatureSecret, HMAC_SIGNATURE_ALGORITHM);
        Mac instance = Mac.getInstance(HMAC_SIGNATURE_ALGORITHM);
        instance.init(signingKey);

        instance.reset();
        byte[] signatureBytes = instance.doFinal(message.getBytes(Charset.forName("UTF-8")));
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public abstract void setTenantId(String tenantId);

    public abstract void setBaseUri(String baseUri);
}
