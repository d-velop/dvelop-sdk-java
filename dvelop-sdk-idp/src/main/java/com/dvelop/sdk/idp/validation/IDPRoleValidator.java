package com.dvelop.sdk.idp.validation;

import com.dvelop.sdk.idp.IDPConstants;
import com.dvelop.sdk.idp.dto.IDPUser;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public class IDPRoleValidator {

    public static void validate(ContainerRequestContext request, IDPUser identity, ResourceInfo resourceInfo) throws UnsupportedEncodingException {

        IDPRole.IDPRoles role = IDPRole.IDPRoles.ANONYMOUS;

        IDPRole annotationOnClass = resourceInfo.getResourceClass().getAnnotation(IDPRole.class);
        if (annotationOnClass != null) {
            role = annotationOnClass.value();
        }

        IDPRole annotationOnMethod = resourceInfo.getResourceMethod().getAnnotation(IDPRole.class);
        if (annotationOnMethod != null) {
            role = annotationOnMethod.value();
        }

        if (role == IDPRole.IDPRoles.ANONYMOUS) {
            return;
        }

        boolean allowExternalValidation = role == IDPRole.IDPRoles.USER_EXTERNAL;

        if (identity != null) {

            if (role == IDPRole.IDPRoles.ADMIN_TENANT && identity.isUserInGroup(IDPConstants.GROUP_ID_ADMIN_TENANT)) {
                return;
            }

            if (role == IDPRole.IDPRoles.USER_INTERNAL && !identity.isExternal()) {
                return;
            }

            if (role == IDPRole.IDPRoles.USER_EXTERNAL && identity.isExternal()) {
                return;
            }

        }

        if (isRequestRedirectable(request)) {
            URI currentUri = request.getUriInfo().getRequestUri();

            String encoded = getEncodedPathAndQuery(currentUri);

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

    private static String getEncodedPathAndQuery(URI currentUri) throws UnsupportedEncodingException {
        String decoded = isQueryPresent(currentUri) ? currentUri.getPath() + "?" + currentUri.getQuery() : currentUri.getPath();
        return URLEncoder.encode(decoded, "ascii");
    }

    private static boolean isQueryPresent(URI currentUri) {
        return currentUri.getQuery() != null && !currentUri.getQuery().isEmpty();
    }

    private static boolean isRequestRedirectable(ContainerRequestContext request) {

        switch (request.getMethod().toLowerCase()) {
            case "post":
            case "put":
            case "patch":
            case "delete":
                return false;
        }

        return request.getAcceptableMediaTypes().stream().anyMatch(mediaType -> mediaType.toString().equals("text/html"));
    }
}
