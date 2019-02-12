package com.dvelop.sdk.idp.filter;

import com.dvelop.sdk.idp.IDPClient;
import com.dvelop.sdk.idp.dto.IDPUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public abstract class IDPIdentityProviderFilter implements ContainerRequestFilter {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String BEARER_HEADER = "Bearer ";
    private static final String AUTHSESSION_ID_COOKIE = "AuthSessionId";

    private String baseUri;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        String authorizationHeader = request.getHeaderString("Authorization");
        Cookie authSessionIdCookie = request.getCookies().get(AUTHSESSION_ID_COOKIE);
        IDPUser identity = null;

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_HEADER)) {
            log.info("Using authorizationHeader "+authorizationHeader);
            String authSessionIdFromHeader = authorizationHeader.substring(BEARER_HEADER.length());

            if (authSessionIdFromHeader.length() > 0) {
                identity = validate(authSessionIdFromHeader);

            }
        } else if (authSessionIdCookie != null) {
            log.info("Using authorizationCookie "+authSessionIdCookie.getValue());
            String authSessionIdFromCookie = authSessionIdCookie.getValue();
            if (authSessionIdFromCookie.length() > 0) {
                String authSessionIdDecoded = URLDecoder.decode(authSessionIdFromCookie, StandardCharsets.UTF_8.name());
                identity = validate(authSessionIdDecoded);
            }
        }

        setIDPIdentity(identity);
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public abstract void setIDPIdentity(IDPUser identity);

    private IDPUser validate(String authSessionId) {
        IDPClient idpClient = new IDPClient(baseUri, authSessionId);
        return idpClient.validate(true);
    }
}
