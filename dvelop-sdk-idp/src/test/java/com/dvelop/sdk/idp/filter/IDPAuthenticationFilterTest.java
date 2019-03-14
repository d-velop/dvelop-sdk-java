package com.dvelop.sdk.idp.filter;

import com.dvelop.sdk.idp.dto.IDPUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IDPAuthenticationFilterTest {

    @Mock
    IDPUser identity;

    @Mock
    ContainerRequestContext request;

    @Mock
    UriInfo uriInfo;

    IDPAuthenticationFilter authenticationFilter;

    public static String GROUP_ID_ADMIN_TENANT = "6DB690CB-EA1B-4D45-B00B-63A2E7B21816";
    public static String GROUP_ID_EXTERNAL_USER = "3E093BE5-CCCE-435D-99F8-544656B98681";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        authenticationFilter = new IDPAuthenticationFilter();


        when(request.getUriInfo()).thenReturn(uriInfo);
    }

    void setupRequestDefaults( String method, String uriPathAndQuery, String acceptHeader) throws URISyntaxException {
        when(request.getMethod()).thenReturn(method);
        when(request.getHeaderString("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new HashMap<>());
        when(request.getHeaderString("accept")).thenReturn(acceptHeader);
        when(request.getAcceptableMediaTypes()).thenReturn( Arrays.asList( MediaType.valueOf(acceptHeader)));
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://localhost" + uriPathAndQuery));
    }

    @ParameterizedTest
    @ValueSource(strings = {"get", "head"})
    void redirectableRequestWithoutAuthorizationInfo_redirectsToIdp(String method) throws Exception {
        setupRequestDefaults(
                "get",
                "/myresource/subresource?query1=abc&query2=123",
                "text/html"
        );

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);

        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleInternal.class, "");
        authenticationFilter.filter(request);

        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), allOf(
                hasProperty("status", is(302)),
                hasProperty("headers", hasEntry(is("Location"), hasItem(is("/identityprovider/login?redirect=%2Fmyresource%2Fsubresource%3Fquery1%3Dabc%26query2%3D123"))))
        ));

    }

    @Test
    void redirectableRequestWithoutAuthorizationInfoWithoutQueryPart_redirectsToIdp() throws Exception {
        setupRequestDefaults(
                "get",
                "/myresource/subresource",
                "text/html"
        );

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);

        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleInternal.class, "");
        authenticationFilter.filter(request);

        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), allOf(
                hasProperty("status", is(302)),
                hasProperty("headers", hasEntry(is("Location"), hasItem(is("/identityprovider/login?redirect=%2Fmyresource%2Fsubresource"))))
        ));

    }

    @ParameterizedTest
    @ValueSource(strings = {"post", "put", "patch", "delete"})
    void modifyingRequestWithoutAuthorizationInfo_sends401(String method) throws Exception {
        when(request.getMethod()).thenReturn(method);
        when(request.getHeaderString("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new HashMap<>());

        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleInternal.class, "");
        authenticationFilter.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), allOf(
                hasProperty("status", is(401)),
                hasProperty("headers", not(hasKey("Location")))
        ));
    }

    @Test
    void requestAcceptsTextHtml_sends302() throws Exception{

        setupRequestDefaults("get", "/some/where", "text/html");
        when(request.getHeaderString("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new HashMap<>());

        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleInternal.class, "");
        authenticationFilter.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), hasProperty("status", is(302)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "application/json",
            "application/hal+json",
            "some/thing"
    })
    void requestAcceptsOther_sends403(String acceptHeader) throws Exception{

        setupRequestDefaults("get", "/some/where", acceptHeader);
        when(request.getHeaderString("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new HashMap<>());

        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleInternal.class, "");
        authenticationFilter.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        assertThat(captor.getValue(), hasProperty("status", is(401)));
    }

    @Test
    void resourceHasNoAnnotations_doesNotInterfere() throws Exception {
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithNoAnnotation.class, "toString");
        authenticationFilter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceClassRoleIsAnonymous_doesNotValidate() throws Exception {
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleAnonymous.class, "MockMethodWithNoAnnotation");
        authenticationFilter.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceClassRoleIsExternal_checksForExteralUser() throws Exception {
        setupRequestDefaults("get", "/some/where", "text/html");
        when(identity.isExternal()).thenReturn(true);

        authenticationFilter.setIDPIdentity(identity);
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleExternal.class, "MockMethodWithNoAnnotation");
        authenticationFilter.filter(request);

        verify(identity).isExternal();
        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceClassRoleIsInternal_checksForInternalUser() throws Exception {
        setupRequestDefaults("get", "/some/where", "text/html");
        when(identity.isExternal()).thenReturn(false);

        authenticationFilter.setIDPIdentity(identity);
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleInternal.class, "MockMethodWithNoAnnotation");
        authenticationFilter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceClassRoleIsAdminTenant_checksForTenantAdminGroup() throws Exception {
        setupRequestDefaults("get", "/some/where", "text/html");
        when(identity.isUserInGroup(anyString())).thenReturn(true);

        authenticationFilter.setIDPIdentity(identity);
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleAdminTenant.class, "MockMethodWithNoAnnotation");
        authenticationFilter.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);

        verify(identity).isUserInGroup(GROUP_ID_ADMIN_TENANT);
        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceMethodRoleIsAnonymous_doesNotValidate() throws Exception {
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockClass.class, "MockMethodWithRoleAnonymous");
        authenticationFilter.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceMethodRoleIsExternal_checksForExteralUser() throws Exception {
        setupRequestDefaults("get", "/some/where", "text/html");
        when(identity.isExternal()).thenReturn(true);

        authenticationFilter.setIDPIdentity(identity);
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockClass.class, "MockMethodWithRoleExternal");
        authenticationFilter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceMethodRoleIsInternal_checksForInternalUser() throws Exception {
        setupRequestDefaults("get", "/some/where", "text/html");
        when(identity.isExternal()).thenReturn(false);

        authenticationFilter.setIDPIdentity(identity);
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockClass.class, "MockMethodWithRoleInternal");
        authenticationFilter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceMethodRoleIsAdminTenant_checksForAdminGroup() throws Exception {
        setupRequestDefaults("get", "/some/where", "text/html");
        when(identity.isUserInGroup(GROUP_ID_ADMIN_TENANT)).thenReturn(true);

        authenticationFilter.setIDPIdentity(identity);
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockClass.class, "MockMethodWithRoleAdminTenant");
        authenticationFilter.filter(request);

        verify(request, never()).abortWith(any());
    }

    @Test
    void resourceClassAndMethodHaveAnnotations_prefersMethodAnnotation() throws Exception {
        authenticationFilter.resourceInfo = makeMockResourceInfo(MockResourceWithRoleInternal.class, "MockMethodWithRoleAnonymous");
        authenticationFilter.filter(request);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request, never()).abortWith(any());
    }

    private ResourceInfo makeMockResourceInfo(Class clazz, String methodName) throws NoSuchMethodException {
        Method method = Stream.of(MockClass.class.getMethods())
                .filter(m -> methodName.equals(m.getName()))
                .findFirst()
                .orElse(clazz.getMethod("toString"));

        ResourceInfo resourceInfo = mock(ResourceInfo.class);
        when(resourceInfo.getResourceClass()).thenReturn(clazz);
        when(resourceInfo.getResourceMethod()).thenReturn(method);

        return resourceInfo;
    }


    // Mock Resources
    @IDPRole(IDPRole.IDPRoles.ANONYMOUS)
    public static class MockResourceWithRoleAnonymous{}

    @IDPRole(IDPRole.IDPRoles.USER_INTERNAL)
    public static class MockResourceWithRoleInternal{}

    @IDPRole(IDPRole.IDPRoles.USER_EXTERNAL)
    public static class MockResourceWithRoleExternal{}

    @IDPRole(IDPRole.IDPRoles.ADMIN_TENANT)
    public static class MockResourceWithRoleAdminTenant{}

    public static class MockResourceWithNoAnnotation{}


    // Mock Method
    public static class MockClass {
        @IDPRole(IDPRole.IDPRoles.ANONYMOUS)
        public void MockMethodWithRoleAnonymous(){}

        @IDPRole(IDPRole.IDPRoles.USER_INTERNAL)
        public void MockMethodWithRoleInternal(){}

        @IDPRole(IDPRole.IDPRoles.USER_EXTERNAL)
        public void MockMethodWithRoleExternal(){}

        @IDPRole(IDPRole.IDPRoles.ADMIN_TENANT)
        public void MockMethodWithRoleAdminTenant(){}

        public void MockMethodWithNoAnnotation(){}
    }
}
