package bcp.cdi.jpa.query_samples;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import bcp.cdi.jpa.ApplicationTestResources;
import bcp.cdi.model.Book;

@EnableAutoWeld
@AddBeanClasses({ ApplicationTestResources.class })
@ActivateScopes({ RequestScoped.class })
public class OneToOneTest {

  static final Operation DELETE_ALL = deleteAllFrom("book", "bookToc");
  static final Operation INSERT_TASKS = insertInto("book")
      .columns("id", "title")
      .values(1L, "User Manual 1")
      .values(2L, "Review User Manual Section 2")
      .values(3L, "Review User Manual Section 3")
      .build();
  static final Operation INSERT_TASK_TOC = insertInto("bookToc")
      .columns("book_id", "content")
      .values(1L, "The table of contents")
      .build();
  
  @Inject
  EntityManager em;

  @BeforeEach
  public void setup() throws Exception {
    Operation operation = sequenceOf(DELETE_ALL, INSERT_TASKS, INSERT_TASK_TOC);
    
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(ApplicationTestResources.ds), operation);
    dbSetup.launch();
  }
  
  @Test
  public void testOneToOneOptionalFalseExecutesMultipleQueries() {
    
    Book task = em.find(Book.class, 1L);
    
    assertNotNull(task);
  }
}
