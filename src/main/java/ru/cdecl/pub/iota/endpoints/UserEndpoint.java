package ru.cdecl.pub.iota.endpoints;

import ru.cdecl.pub.iota.services.UserProfileService;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Singleton
@Path("/user")
public class UserEndpoint {

    private UserProfileService userProfileService;

    public UserEndpoint(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") long userId) {
        // todo: переписать
        final UserProfile userProfile = userProfileService.getUserById(userId);

        if (userProfile != null) {
            return Response.ok(userProfile).build();
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user, @Context HttpHeaders headers) {
        // TODO: пользователь с таким именем может существовать
        if (userProfileService.addUser(user.getUserId(), user)) {
            return Response.status(Response.Status.OK).entity(user.getLogin()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

}
