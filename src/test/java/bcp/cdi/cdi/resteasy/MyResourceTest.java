package bcp.cdi.cdi.resteasy;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import bcp.cdi.conf.ApplicationResources;
import bcp.cdi.model.City;
import bcp.cdi.resource.CityController;
import bcp.cdi.service.CityService;

@EnableAutoWeld
//@RunWith(JUnitPlatform.class)
//@ExtendWith(WeldJunit5Extension.class)
@AddPackages({ ApplicationResources.class})
@ActivateScopes({RequestScoped.class})
@AddBeanClasses(CityService.class)
public class MyResourceTest {

//  @WeldSetup
//  public WeldInitiator weld = WeldInitiator.from(MyResource.class, CloseableResource.class, 
//      CityService.class, UserService.class, ApplicationResources.class, TestQualifier.class)
//          .activate(RequestScoped.class, ApplicationScoped.class).build();
  
 
//  @Inject
//  MyResource myResource;
  
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
