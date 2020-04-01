package bcp.cdi.jpa;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.jdbcx.JdbcDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationTestResources {

  public static JdbcDataSource ds;
  private static EntityManagerFactory emf;
  static {
    try {
      File tempDBFile = File.createTempFile("file", ".db");
      tempDBFile.deleteOnExit();
      String dbURL = "jdbc:h2:file:" + tempDBFile.getAbsolutePath() + ";INIT=RUNSCRIPT FROM 'classpath:create.sql'";

      ds = new JdbcDataSource();
      ds.setURL(dbURL);
      ds.setUser("sa");
      
      Map<String, String> dbConnectionDetails = new HashMap<>();
      // Overwrite default URL in the persistence context
      dbConnectionDetails.put("javax.persistence.jdbc.url", dbURL);
      emf = Persistence.createEntityManagerFactory("test", dbConnectionDetails);
    } catch (Exception e) {
      log.error("could not create EntityManagerFactory", e);
    }
  }


  @Produces
  @RequestScoped
  public EntityManager produceEntityManager() {
    EntityManager em = emf.createEntityManager();
    log.debug("produced entityManager {}", System.identityHashCode(em));
    return em;
  }

  public void closeEntityManager(@Disposes EntityManager em) {
    log.debug("closed entityManager {}", System.identityHashCode(em));
    em.close();
  }
  
  @Produces
  @ApplicationScoped
  public ExecutorService produceExecutorService() {
    ExecutorService executorService =  Executors.newCachedThreadPool();
    log.debug("Produced {}", executorService);
    return executorService;
  }
  
  public void destroyExecutorService(@Disposes ExecutorService executorService) {
    executorService.shutdown();
    log.debug("ExecutorService {} shut down", executorService);
  }
}
