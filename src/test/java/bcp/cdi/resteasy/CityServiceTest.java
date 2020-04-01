package bcp.cdi.resteasy;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import bcp.cdi.model.City;
import bcp.cdi.resource.CityController;
import bcp.cdi.service.CityService;

@EnableAutoWeld
@ActivateScopes({ RequestScoped.class })
@AddBeanClasses(CityService.class)
public class CityServiceTest {

  @Test
  public void testService(CityController myResource) {
    List<City> cities = myResource.allCities();

    System.out.println(cities);
  }

  @Test
  public void testSearchService(CityController myResource) {
    List<City> cities = myResource.searchCities("name");

    System.out.println(cities);
  }
}
