package ru.cdecl.pub.iota.resources;

import org.glassfish.hk2.api.Immediate;
import ru.cdecl.pub.iota.services.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    AccountService accountService;

    public UserResource() {
        System.out.println("on resource init ! " + hashCode());
    }

    @GET
    public Response doGet() {
        return Response.ok(accountService.getHello()).build();
    }
}
