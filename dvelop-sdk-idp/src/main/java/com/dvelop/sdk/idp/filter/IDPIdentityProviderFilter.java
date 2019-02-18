package com.dvelop.sdk.idp.filter;

import com.dvelop.sdk.idp.IDPClient;
import com.dvelop.sdk.idp.IDPConstants;
import com.dvelop.sdk.idp.dto.IDPUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * A base class to interpret authentication headers of the d.velop identity provider
 * Implement this abstract class and add it to your jax-rs pipeline, use the {@link #setIDPIdentity} and
 * {@link #setIDPAuthsessionId} methods to store the current user for this request somewhere.
 * Ensure to set the baseUri before delegating the {@link #filter} method.
 *
 * <pre>
 * @Provider
 * @PreMatching
 * public class InjectableIDPIdentityProviderFilter extends IDPIdentityProviderFilter {
 *
 *     @Override
 *     public void filter(ContainerRequestContext request){
 *         setBaseUri("https://some.where"); // get this from somewhere sensible
 *         super.filter(request);
 *     }
 *
 *     @Override
 *     public void setIDPIdentity(IDPUser s){
 *         System.out.println("User is "+s);
 *     }
 *
 *     @Override
 *     public void setIDPAuthsessionId(String s){
 *         System.out.println("AuthsessionId is "+s);
 *     }
 * }
 * </pre>
 */
public abstract class IDPIdentityProviderFilter implements ContainerRequestFilter {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String AUTHSESSION_ID_COOKIE = "AuthSessionId";

    private String baseUri;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        String authorizationHeader = request.getHeaderString("Authorization");
        Cookie authSessionIdCookie = request.getCookies().get(AUTHSESSION_ID_COOKIE);
        IDPUser identity = null;
        String authsessionId = "";

        if (authorizationHeader != null && authorizationHeader.startsWith(IDPConstants.BEARER_HEADER_PREFIX)) {
            log.info("Using authorizationHeader "+authorizationHeader);
            String authSessionIdFromHeader = authorizationHeader.substring(IDPConstants.BEARER_HEADER_PREFIX.length());

            if (authSessionIdFromHeader.length() > 0) {
                authsessionId = authSessionIdFromHeader;
                identity = validate(authSessionIdFromHeader);

            }
        } else if (authSessionIdCookie != null) {
            log.info("Using authorizationCookie "+authSessionIdCookie.getValue());
            String authSessionIdFromCookie = authSessionIdCookie.getValue();
            if (authSessionIdFromCookie.length() > 0) {
                String authSessionIdDecoded = URLDecoder.decode(authSessionIdFromCookie, StandardCharsets.UTF_8.name());

                authsessionId = authSessionIdDecoded;
                identity = validate(authSessionIdDecoded);
            }
        }

        if (!"".equals(authsessionId)){
            setIDPAuthsessionId(authsessionId);
        }

        setIDPIdentity(identity);
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public abstract void setIDPIdentity(IDPUser identity);

    public abstract void setIDPAuthsessionId( String authsessionId);

    private IDPUser validate(String authSessionId) {
        IDPClient idpClient = new IDPClient(baseUri, authSessionId);
        return idpClient.validate(true);
    }
}
