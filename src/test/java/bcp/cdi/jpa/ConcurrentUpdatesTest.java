package bcp.cdi.jpa;

import static bcp.cdi.jpa.DBUtil.runInTransaction;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

import bcp.cdi.model.Item;
import bcp.cdi.resource.AsyncProcessor;

@EnableAutoWeld
@AddBeanClasses({ ApplicationTestResources.class })
@ActivateScopes({ RequestScoped.class })
public class ConcurrentUpdatesTest {

  static final Operation DELETE_ALL = deleteAllFrom("item");
  static final Operation INSERT_ITEMS = insertInto("item")
      .columns("id", "name", "quantity", "version")
      .values(1L, "Dell monitor", 0, 1).build();

  @Inject
  EntityManager em;
  
  @Inject
  AsyncProcessor<Item> asyncProcessor;
      
  @BeforeEach
  public void setup() throws Exception {
    Operation operation = sequenceOf(DELETE_ALL, INSERT_ITEMS);
    
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(ApplicationTestResources.ds), operation);
    dbSetup.launch();
  }

  Item incrementQuantity(Long id, int timeout) {
    return runInTransaction(em, () -> {
      Item item = em.find(Item.class, 1L);

      sleep(timeout);

      int qty = item.getQuantity();
      item.setQuantity(++qty);

      return item;
    });
  }

  @Test
  public void testConcurrentIncrement() throws InterruptedException, ExecutionException {

    Callable<Item> callable1 = () -> incrementQuantity(1L, 500);
    Callable<Item> callable2 = () -> incrementQuantity(1L, 0);

    // start execution in parallel
    Future<Item> future1 = asyncProcessor.doit(callable1);
    Future<Item> future2 = asyncProcessor.doit(callable2);

    // wait and get results
    Item item1 = future1.get();
    Item item2 = future2.get();

    System.out.println(item1);
    System.out.println(item2);
    assertEquals(1, item2.getQuantity());
    assertEquals(2, item1.getQuantity());
  }

  private void sleep(int timeout) {
    try {
      Thread.sleep(timeout);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
