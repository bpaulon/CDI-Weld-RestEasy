package bcp.cdi.resource;

import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

/**
 * Parameter validation example for asynchronous services
 * 
 * @author bogdan_paulon
 */
@RequestScoped
@Path("asyncValidation")
public class ParamValidatedAsyncController {

	@GET
	public void asyncValidation(@QueryParam("param") @NotEmpty String param, @Suspended AsyncResponse response) {
		response.resume(param);
	}

}
