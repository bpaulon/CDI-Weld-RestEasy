package bcp.cdi.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/pptest/{" + PathParamTest.ID + "}")
public class PathParamTest {

  public static final String ID = "id";

  @PathParam(ID)
  String id;

  @GET
  @Path("/bar")
  public Response getBar() {
    System.out.println("bar");
    return Response.ok().build();
  }

  @GET
  @Path("/baz")
  public Response getBaz() {
    System.out.println("baz");
    return Response.ok().build();
  }

}
