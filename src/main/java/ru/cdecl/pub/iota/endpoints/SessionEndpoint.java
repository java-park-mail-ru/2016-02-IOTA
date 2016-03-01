package ru.cdecl.pub.iota.endpoints;

import ru.cdecl.pub.iota.models.UserLoginRequest;
import ru.cdecl.pub.iota.models.UserProfile;
import ru.cdecl.pub.iota.models.base.BaseApiResponse;
import ru.cdecl.pub.iota.services.AuthenticationService;
import ru.cdecl.pub.iota.services.UserProfileService;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionEndpoint {

    private UserProfileService userProfileService;
    private AuthenticationService authenticationService;

    public SessionEndpoint(UserProfileService userProfileService, AuthenticationService authenticationService) {
        this.userProfileService = userProfileService;
        this.authenticationService = authenticationService;
    }

    @GET
    public Response getUserId(@Context HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession != null) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (httpSession) {
                try {
                    final Object userId = httpSession.getAttribute("user_id");

                    if (userId != null && userId instanceof Long) {
                        final UserProfile userProfile = userProfileService.getUserById((Long) userId);

                        if (userProfile != null) {
                            return Response.ok(userProfile).build();
                        }
                    }
                } catch (IllegalStateException ignored) {
                }
            }
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity(new BaseApiResponse()).build();
    }

    @PUT
//    public Response doLogin(UserLoginRequest userLoginRequest, @Context HttpServletRequest httpServletRequest) {
//        // todo
//
//        final HttpSession httpSession = httpServletRequest.getSession();
//
//        return Response.ok(userLoginRequest).build(); // todo
//    }

    @DELETE
    public Response doLogout(@Context HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession != null) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (httpSession) {
                try {
                    httpSession.invalidate();
                } catch (IllegalStateException ignored) {
                }
            }
        }

        return Response.ok(new BaseApiResponse()).build();
    }

}
