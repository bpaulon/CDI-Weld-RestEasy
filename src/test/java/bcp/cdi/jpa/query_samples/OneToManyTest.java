package bcp.cdi.jpa.query_samples;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import bcp.cdi.jpa.ApplicationTestResources;
import bcp.cdi.model.Task;
import bcp.cdi.model.Task_;

@EnableAutoWeld
@AddBeanClasses({ ApplicationTestResources.class })
@ActivateScopes({ RequestScoped.class })
public class OneToManyTest {

  static final Operation DELETE_ALL = deleteAllFrom("task", "taskFile");
  static final Operation INSERT_TASKS = insertInto("task")
      .columns("id", "title")
      .values(1L, "Review User Manual Section 1")
      .values(2L, "Review User Manual Section 2")
      .values(3L, "Review User Manual Section 3")
      .build();
  static final Operation INSERT_TASK_FILE = insertInto("taskFile")
      .columns("id", "task_id", "name")
      .values(1L, 1L, "name_file_001")
      .values(2L, 2L, "name_file_002")
      .values(3L, 3L, "name_file_003")
      .build();

  @Inject
  EntityManager em;

  @BeforeEach
  public void setup() throws Exception {
    Operation operation = sequenceOf(DELETE_ALL, INSERT_TASKS, INSERT_TASK_FILE);
    
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(ApplicationTestResources.ds), operation);
    dbSetup.launch();
  }
  

  @Test
  public void testOneToManyExecutesMultipleQueries() {
    List<Task> tasks = em.createQuery("SELECT t FROM task t JOIN t.files", Task.class).getResultList();
    
    tasks.stream().forEach(t -> System.out.println(t));
  }
  
  @Test
  public void testOneToManyFetchExecutesOneQuery() {
    List<Task> tasks = em.createQuery("SELECT t FROM task t JOIN FETCH t.files", Task.class).getResultList();
   
    tasks.stream().forEach(t -> System.out.println(t));
  }
  
  @Test
  public void testOneToManyMatchesStatistics() {
    Statistics stats = getHibernateStatistics();
    
    List<Task> tasks = em.createQuery("SELECT t FROM task t JOIN t.files", Task.class).getResultList();
    tasks.stream().forEach(t -> System.out.println(t));
    
    assertEquals(4, stats.getPrepareStatementCount());
  }
  
  @Test
  public void testOneToManyFetchMatchesStatistics() {
    Statistics stats = getHibernateStatistics();
    
    List<Task> tasks = em.createQuery("SELECT t FROM task t JOIN FETCH t.files", Task.class).getResultList();
    tasks.stream().forEach(t -> System.out.println(t));
    
    assertEquals(1, stats.getPrepareStatementCount());
  }
  
  
  @Test
  public void testCriteriaQueryReturnsData() {
    CriteriaBuilder cb = this.em.getCriteriaBuilder(); 
    CriteriaQuery<Task> q = cb.createQuery(Task.class);

    Root<Task> a = q.from(Task.class);
    // use metadata class to define the where clause
    q.where(cb.like(a.get(Task_.title), "%Manual%"));

    List<Task> tasks = em.createQuery(q).getResultList();
    assertEquals(3, tasks.size());
  }
  
  private Statistics getHibernateStatistics() {
    SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
    Statistics statistics = sf.getStatistics();
    statistics.setStatisticsEnabled(true);
    statistics.clear();
    return statistics;
  }
}
