package ru.cdecl.pub.iota.endpoints;

import ru.cdecl.pub.iota.models.UserCreateRequest;
import ru.cdecl.pub.iota.services.AuthenticationService;
import ru.cdecl.pub.iota.services.UserProfileService;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Singleton
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserEndpoint {

    private UserProfileService userProfileService;
    private AuthenticationService authenticationService;

    public UserEndpoint(UserProfileService userProfileService, AuthenticationService authenticationService) {
        this.userProfileService = userProfileService;
        this.authenticationService = authenticationService;
    }

    @GET
    public Response getAllUsers() {
        final Collection<UserProfile> allUsers = userProfileService.getAllUsers();

        return Response.ok(allUsers.toArray(new UserProfile[allUsers.size()])).build();
    }

//    @GET
//    @Path("{name}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getUserByName(@PathParam("name") String name) {
//        final UserProfile user = userProfileService.getUserByName(name);
//        if (user == null) {
//            return Response.status(Response.Status.FORBIDDEN).build();
//        } else {
//            return Response.status(Response.Status.OK).entity(user).build();
//        }
//    }

    @GET
    @Path("{id}")
    public Response getUserById(@PathParam("id") long userId, @Context HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession != null) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (httpSession) {
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

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    public Response createUser(UserCreateRequest userCreateRequest) { // todo
        if (userProfileService.addUser(userCreateRequest.getUserId(), userCreateRequest)) {
//            authenticationService.setPasswordForUser(user.getUserId(),);

            return Response.status(Response.Status.OK).entity(userCreateRequest.getLogin()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

}
