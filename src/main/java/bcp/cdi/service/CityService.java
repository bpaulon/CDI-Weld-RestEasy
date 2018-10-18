package bcp.cdi.service;

import static bcp.cdi.util.LogUtil.identity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;

import bcp.cdi.model.City;
import lombok.extern.slf4j.Slf4j;

@RequestScoped
@Slf4j
public class CityService implements ICityService {

  public CityService() {
    log.debug("/// Constructor {}", identity(this));
  }

  public List<City> findAll() {
    List<City> cities = new ArrayList<>();

    cities.add(new City(1L, "Bratislava", 432000));
    cities.add(new City(2L, "Budapest", 1759000));
    cities.add(new City(3L, "Prague", 1280000));
    cities.add(new City(4L, "Warsaw", 1748000));
    cities.add(new City(5L, "Los Angeles", 3971000));
    cities.add(new City(6L, "New York", 8550000));
    cities.add(new City(7L, "Edinburgh", 464000));
    cities.add(new City(8L, "Berlin", 3671000));

    return cities;
  }

  @PreDestroy
  public void destroy() {
    log.debug("/// PreDestroy {}", identity(this));
  }

}