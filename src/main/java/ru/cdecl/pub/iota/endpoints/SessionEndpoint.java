package ru.cdecl.pub.iota.endpoints;

import ru.cdecl.pub.iota.services.AccountService;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Singleton
@Path("/session")
public class SessionEndpoint {

    private AccountService accountService;

    public SessionEndpoint(AccountService accountService) {
        this.accountService = accountService;
    }

//    @GET
//    public Response getUserIdFromSession(@Context HttpServletRequest httpServletRequest) {
//        final HttpSession httpSession = httpServletRequest.getSession();
//        Long userId = null;
//
//        if(httpSession != null) {
//            userId = (Long)httpSession.getAttribute("user_id");
//        }
//
//        if (userId == null) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        } else {
//            return Response.status(Response.Status.OK).
//        }
//    }

}
