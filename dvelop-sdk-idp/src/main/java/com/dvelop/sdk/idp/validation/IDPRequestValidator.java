package com.dvelop.sdk.idp.validation;

import com.dvelop.sdk.idp.IDPClient;
import com.dvelop.sdk.idp.IDPConstants;
import com.dvelop.sdk.idp.dto.IDPUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class IDPRequestValidator {
    static Logger log = LoggerFactory.getLogger(IDPRequestValidator.class);

    private static final String AUTHSESSION_ID_COOKIE = "AuthSessionId";

    public static IDPUser validate(String baseuri, ContainerRequestContext request) throws UnsupportedEncodingException {


        String authorizationHeader = request.getHeaderString("Authorization");
        Cookie authSessionIdCookie = request.getCookies().get(AUTHSESSION_ID_COOKIE);
        String authsessionId = "";

        if (authorizationHeader != null && authorizationHeader.startsWith(IDPConstants.BEARER_HEADER_PREFIX)) {
            log.info("Using authorizationHeader "+authorizationHeader);
            String authSessionIdFromHeader = authorizationHeader.substring(IDPConstants.BEARER_HEADER_PREFIX.length());

            if (authSessionIdFromHeader.length() > 0) {
                authsessionId = authSessionIdFromHeader;
            }
        } else if (authSessionIdCookie != null) {
            log.info("Using authorizationCookie "+authSessionIdCookie.getValue());
            String authSessionIdFromCookie = authSessionIdCookie.getValue();
            if (authSessionIdFromCookie.length() > 0) {
                String authSessionIdDecoded = URLDecoder.decode(authSessionIdFromCookie, StandardCharsets.UTF_8.name());

                authsessionId = authSessionIdDecoded;
            }
        }

        if (authsessionId.isEmpty()) {
            return null;
        } else {
            return validate(authsessionId, baseuri);
        }
    }

    private static IDPUser validate(String authSessionId, String baseUri) {
        IDPClient idpClient = new IDPClient(baseUri, authSessionId);
        return idpClient.validate(true);
    }

}
