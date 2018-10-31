package bcp.cdi.resource;

import static bcp.cdi.util.LogUtil.CONSTRUCTOR_MSG;
import static bcp.cdi.util.LogUtil.PREDESTROY_MSG;
import static bcp.cdi.util.LogUtil.identity;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import bcp.cdi.model.City;
import bcp.cdi.service.ICityService;
import bcp.cdi.service.Logged;
import bcp.cdi.service.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Path("cities")
@ApplicationScoped
@Logged
@NoArgsConstructor
@Slf4j
public class CityController {

	@Inject
	private ICityService cityService;

	@Inject
	public CityController(UserService us) {
		log.debug(CONSTRUCTOR_MSG, identity(this));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<City> allCities() {
		List<City> cities = cityService.findAll();
		return cities;
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public List<City> searchCities(@QueryParam("param") @NotEmpty String param) {
		log.debug("search param: {} ", param);
		return cityService.findAll();
	}

	@PreDestroy
	public void destroy() {
		log.debug(PREDESTROY_MSG, identity(this));
	}

}