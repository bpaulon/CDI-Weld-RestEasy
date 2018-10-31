package bcp.cdi.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.api.validation.ResteasyViolationException;

import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class ValidationExceptionHandler implements ExceptionMapper<ResteasyViolationException> {

	@Override
	public Response toResponse(ResteasyViolationException e) {
		log.error("Validation error:", e);
		return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.APPLICATION_JSON).build();
	}
}