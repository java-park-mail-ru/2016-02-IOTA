package ru.cdecl.pub.iota.endpoints;

import ru.cdecl.pub.iota.models.UserCreateRequest;
import ru.cdecl.pub.iota.models.UserCreateResponse;
import ru.cdecl.pub.iota.models.UserEditRequest;
import ru.cdecl.pub.iota.models.base.BaseApiResponse;
import ru.cdecl.pub.iota.services.AuthenticationService;
import ru.cdecl.pub.iota.services.UserProfileService;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Singleton
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserEndpoint {

    private final UserProfileService userProfileService;
    private final AuthenticationService authenticationService;

    public UserEndpoint(UserProfileService userProfileService, AuthenticationService authenticationService) {
        this.userProfileService = userProfileService;
        this.authenticationService = authenticationService;
    }

    @GET
    @Path("{id}")
    public Response getUserById(@PathParam("id") long userId, @Context HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession != null) {
            synchronized (httpSession.getId().intern()) {
                try {
                    final Object userIdFromSession = httpSession.getAttribute("user_id");

                    if (userIdFromSession != null && userIdFromSession instanceof Long && userId == (long) userIdFromSession) {
                        final UserProfile userProfile = userProfileService.getUserById(userId);

                        if (userProfile != null) {
                            return Response.ok(userProfile).build();
                        }
                    }
                } catch (IllegalStateException ignored) {
                }
            }
        }

        return Response.status(Response.Status.NOT_FOUND).entity(new BaseApiResponse()).build();
    }

    @POST
    public Response createUser(UserCreateRequest userCreateRequest) {
        final char[] userPassword = userCreateRequest.getPassword().toCharArray();

        userCreateRequest.eraseSensitiveData();

        if (userProfileService.addUser(userCreateRequest.getUserId(), userCreateRequest)) {
            authenticationService.setPasswordForUser(userCreateRequest.getUserId(), userPassword);

            return Response.status(Response.Status.OK).entity(new UserCreateResponse(userCreateRequest.getUserId())).build();
        }

        return Response.status(Response.Status.FORBIDDEN).entity(new BaseApiResponse()).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") long userId, @Context HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession != null) {
            synchronized (httpSession.getId().intern()) {
                try {
                    final Object userIdFromSession = httpSession.getAttribute("user_id");

                    if (userIdFromSession == null || !(userIdFromSession instanceof Long)) {
                        return Response.status(Response.Status.NOT_FOUND).entity(new BaseApiResponse()).build();
                    }

                    if (userId == (long) userIdFromSession) {
                        userProfileService.deleteUser(userId);
                        authenticationService.deletePasswordForUser(userId);
                        httpSession.invalidate();

                        return Response.ok(new BaseApiResponse()).build();
                    }
                } catch (IllegalStateException ignored) {
                }
            }
        }

        return Response.status(Response.Status.FORBIDDEN).entity(new BaseApiResponse()).build();
    }

    @POST
    @Path("{id}")
    public Response editUser(@PathParam("id") long userId, UserEditRequest userEditRequest, @Context HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession != null) {
            synchronized (httpSession.getId().intern()) {
                try {
                    final Object userIdFromSession = httpSession.getAttribute("user_id");

                    if (userIdFromSession == null || !(userIdFromSession instanceof Long)) {
                        return Response.status(Response.Status.UNAUTHORIZED).entity(new BaseApiResponse()).build();
                    }

                    if (userId == (long) userIdFromSession) {
                        userProfileService.updateUser(userId, userEditRequest);
                        authenticationService.setPasswordForUser(userId, userEditRequest.getPassword());
                        httpSession.invalidate();

                        return Response.ok(new BaseApiResponse()).build();
                    }

                } catch (IllegalStateException ignored) {
                }
            }
        }

        return Response.status(Response.Status.FORBIDDEN).entity(new BaseApiResponse()).build();
    }

}
