package com.dvelop.sdk.idp;

import com.dvelop.sdk.idp.dto.IDPUser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class IDPClient {

    private Client client;
    private String baseuri;
    private String authSessionId;

    public IDPClient(String baseuri, String authSessionId) {
        this(ClientBuilder.newClient(), baseuri, authSessionId);
    }

    public IDPClient(Client client, String baseuri, String authSessionId) {
        this.client = client;
        this.baseuri = baseuri;
        this.authSessionId = authSessionId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getBaseuri() {
        return baseuri;
    }

    public void setBaseuri(String baseuri) {
        this.baseuri = baseuri;
    }

    public String getAuthSessionId() {
        return authSessionId;
    }

    public void setAuthSessionId(String authSessionId) {
        this.authSessionId = authSessionId;
    }

    /**
     * Calls the validate endpoint for this IDPClients authSessionId.
     * See the IdentityproviderApp documentation for details on how to use "external validation".
     *
     * @param allowExternalValidation allow validation of external users in the IDP
     * @return {@link IDPUser} if the current user is a valid idp user, null otherwise
     */
    public IDPUser validate(boolean allowExternalValidation) {
        Response response = client.target(baseuri)
                .path("identityprovider")
                .path("validate")
                .queryParam("allowExternalValidation", allowExternalValidation)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer "+authSessionId)
                .get();

        if (response.getStatus() == 401) {
            return null;
        }

        return response.readEntity(IDPUser.class);
    }

    /**
     * Calls the scim/users/{userId} endpoint for the given userId
     *
     * @param userId idp id of the user to retrieve
     * @return {@link IDPUser} if userId is valid, null otherwise
     */
    public IDPUser getUserById(String userId) {
        Response response = client.target(baseuri)
                .path("identityprovider")
                .path("scim")
                .path("users")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer "+authSessionId)
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(IDPUser.class);
        }

        return null;
    }



}
