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
import javax.persistence.LockModeType;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import bcp.cdi.model.VersionedItem;
import bcp.cdi.resource.AsyncProcessor;

@EnableAutoWeld
@AddBeanClasses({ ApplicationTestResources.class })
@ActivateScopes({ RequestScoped.class })
public class OptimisticLockingTest {

  static final Operation DELETE_ALL = deleteAllFrom("item");
  static final Operation INSERT_ITEMS = insertInto("item")
      .columns("id", "name", "quantity", "version")
      .values(1L, "Dell monitor", 0, 1).build();

  @Inject
  EntityManager em;
  
  @Inject
  AsyncProcessor<VersionedItem> asyncProcessor;
      
  @BeforeEach
  public void setup() throws Exception {
    Operation operation = sequenceOf(DELETE_ALL, INSERT_ITEMS);
    
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(ApplicationTestResources.ds), operation);
    dbSetup.launch();
  }
  
  VersionedItem incrementQuantity(Long id, int timeout) {
    return runInTransaction(em, () -> {
      VersionedItem item = em.find(VersionedItem.class, 1L, LockModeType.OPTIMISTIC);

      sleep(timeout);

      int qty = item.getQuantity();
      item.setQuantity(++qty);

      return item;
    });
  }

  @Test
  public void testShouldThrowOptimisticLockingException() throws InterruptedException, ExecutionException {

    Callable<VersionedItem> callable1 = () -> incrementQuantity(1L, 500);
    Callable<VersionedItem> callable2 = () -> incrementQuantity(1L, 0);

    Future<VersionedItem> future1 = asyncProcessor.doit(callable1);
    // Delaying the second transaction more than 500 ms makes the test pass 
    // sleep(600);
    // make sure the transactions are executed in this sequence
    sleep(100);
    Future<VersionedItem> future2 = asyncProcessor.doit(callable2);

    VersionedItem item1 = future1.get();
    VersionedItem item2 = future2.get();
    
    System.out.println(item1);
    System.out.println(item2);
    assertEquals(1, item1.getQuantity());
    assertEquals(2, item2.getQuantity());
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

