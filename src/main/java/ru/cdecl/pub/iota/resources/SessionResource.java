package ru.cdecl.pub.iota.resources;

import org.glassfish.hk2.api.Immediate;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    public SessionResource() {
    }

}
