package com.dvelop.sdk.idp.filter;

import com.dvelop.sdk.idp.IDPConstants;
import com.dvelop.sdk.idp.dto.IDPUser;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * The IDPAuthenticationFilter implements a jax-rs server filter to ensure that for resources annotated with
 * {@link IDPRole}, the {@link IDPUser} given with {@link #setIDPIdentity } has that role. If the user does
 * not have that role.
 * Combine this with {@link IDPIdentityProviderFilter} to ensure the request contains a valid {@link IDPUser}.
 */
public class IDPAuthenticationFilter implements ContainerRequestFilter {

    private IDPUser identity;

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        IDPRole.IDPRoles role = IDPRole.IDPRoles.ANONYMOUS;

        IDPRole annotationOnClass = resourceInfo.getResourceClass().getAnnotation(IDPRole.class);
        if (annotationOnClass != null) {
            role = annotationOnClass.value();
        }

        IDPRole annotationOnMethod = resourceInfo.getResourceMethod().getAnnotation(IDPRole.class);
        if (annotationOnMethod != null) {
            role = annotationOnMethod.value();
        }

        if(role == IDPRole.IDPRoles.ANONYMOUS){
            return;
        }

        boolean allowExternalValidation = role == IDPRole.IDPRoles.USER_EXTERNAL;

        if (identity != null) {

            if( role == IDPRole.IDPRoles.ADMIN_TENANT && identity.isUserInGroup(IDPConstants.GROUP_ID_ADMIN_TENANT)) {
                return;
            }

            if( role == IDPRole.IDPRoles.USER_INTERNAL && !identity.isExternal()){
                return;
            }

            if( role == IDPRole.IDPRoles.USER_EXTERNAL && identity.isExternal()){
                return;
            }

        }

        if (isRequestRedirectable(request)) {
            URI currentUri = request.getUriInfo().getRequestUri();

            String encoded = URLEncoder.encode(currentUri.toString(), "ascii");
            String redirectUri = "/identityprovider/login?redirect=" + encoded;

            Response redirect = Response
                    .status(Response.Status.FOUND)
                    .header("Location", redirectUri)
                    .build();
            request.abortWith(redirect);
        } else {
            Response redirect = Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
            request.abortWith(redirect);
        }
    }

    private boolean isRequestRedirectable(ContainerRequestContext request) {

        switch (request.getMethod().toLowerCase()) {
            case "post":
            case "put":
            case "patch":
            case "delete":
                return false;
        }

        return request.getAcceptableMediaTypes().stream().anyMatch(mediaType -> mediaType.toString().equals("text/html"));
    }

    public void setIDPIdentity(IDPUser identity){
        this.identity = identity;
    };

}
