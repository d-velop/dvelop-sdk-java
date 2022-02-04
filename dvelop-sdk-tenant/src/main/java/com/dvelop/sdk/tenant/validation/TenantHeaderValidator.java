package com.dvelop.sdk.tenant.validation;

import com.dvelop.sdk.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.container.ContainerRequestContext;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TenantHeaderValidator {
    String HMAC_SIGNATURE_ALGORITHM = "HmacSHA256";

    private static final String HEADER_DV_TENANT_ID = "x-dv-tenant-id";
    private static final String HEADER_DV_BASEURI = "x-dv-baseuri";
    private static final String HEADER_DV_SIG_1 = "x-dv-sig-1";

    private final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    private final byte[] signatureSecret;
    private final String defaultBaseUri;
    private final String defaultTenantId;

    public static class TenantHeaderValidationFailedException extends Exception {
        boolean isServerError;

        public TenantHeaderValidationFailedException(String message, boolean isServerError) {
            super(message);
            this.isServerError = isServerError;
        }

        public boolean isServerError() {
            return isServerError;
        }

    }

    public TenantHeaderValidator(byte[] signatureSecret, String defaultBaseUri, String defaultTenantId) {
        this.signatureSecret = signatureSecret;
        this.defaultBaseUri = defaultBaseUri;
        this.defaultTenantId = defaultTenantId;
    }

    public Tenant validate(ContainerRequestContext request) throws TenantHeaderValidationFailedException {
        String systemBaseUri = request.getHeaderString(HEADER_DV_BASEURI);
        String tenantId = request.getHeaderString(HEADER_DV_TENANT_ID);
        String givenSignature = request.getHeaderString(HEADER_DV_SIG_1);

        if (systemBaseUri == null && tenantId == null && givenSignature == null) {
            return new Tenant(defaultBaseUri, defaultTenantId);
        }

        if (systemBaseUri == null) {
            throw new TenantHeaderValidationFailedException("missing systemBaseUri", false);
        }

        if (tenantId == null) {
            throw new TenantHeaderValidationFailedException("missing tenantId", false);
        }

        String expectedSignature = null;
        try {
            expectedSignature = base64Signature(systemBaseUri + tenantId);
        } catch (Exception e) {
            logger.error("Failed to calculate signature: {}", e.getMessage());
            throw new TenantHeaderValidationFailedException("failed to calculare expected signature: " + e.getMessage(), true);
        }

        if(givenSignature == null) {
            logger.warn("Invalid signature {}", givenSignature);
            throw new TenantHeaderValidationFailedException("received invalid signature", false);
        }

        if (!MessageDigest.isEqual(Base64.getDecoder().decode(expectedSignature), Base64.getDecoder().decode(givenSignature))) {
            logger.warn("Invalid signature {}", givenSignature);
            throw new TenantHeaderValidationFailedException("received invalid signature", false);
        }

        return new Tenant(systemBaseUri, tenantId);
    }


    private String base64Signature(String message) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(signatureSecret, HMAC_SIGNATURE_ALGORITHM);
        Mac instance = Mac.getInstance(HMAC_SIGNATURE_ALGORITHM);
        instance.init(signingKey);

        instance.reset();
        byte[] signatureBytes = instance.doFinal(message.getBytes(Charset.forName("UTF-8")));
        return Base64.getEncoder().encodeToString(signatureBytes);
    }


}
