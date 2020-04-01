package bcp.cdi.jpa;

import static bcp.cdi.jpa.DBUtil.runInTransaction;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bcp.cdi.model.Item;
import bcp.cdi.model.VersionedItem;
import bcp.cdi.resource.AsyncProcessor;

@EnableAutoWeld
@AddPackages({ ApplicationTestResources.class })
@ActivateScopes({ RequestScoped.class })
public class VersioningTest {

  @Inject
  EntityManager em;

  @BeforeEach
  public void setup() throws Exception {
    runInTransaction(em, () -> {
      Item item = Item.builder().name("pc").quantity(0).build();
      em.persist(item);

      VersionedItem vItem = VersionedItem.builder().name("pc").quantity(0).build();
      em.persist(vItem);

      return null;
    });
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

  VersionedItem incrementQuantityPesimistic(Long id, int timeout) {
    return runInTransaction(em, () -> {
      VersionedItem item = em.find(VersionedItem.class, 1L, LockModeType.PESSIMISTIC_WRITE);

      sleep(timeout);

      int qty = item.getQuantity();
      item.setQuantity(++qty);

      return item;
    });
  }
  
  VersionedItem incrementQuantityOptimistic(Long id, int timeout) {
    return runInTransaction(em, () -> {
      VersionedItem item = em.find(VersionedItem.class, 1L, LockModeType.OPTIMISTIC);

      sleep(timeout);

      int qty = item.getQuantity();
      item.setQuantity(++qty);

      return item;
    });
  }

  @Inject
  AsyncProcessor asyncProcessor;

  @Test
  public void testConcurrentIncrementAtomic() throws InterruptedException, ExecutionException {

    Callable<VersionedItem> callable1 = () -> {
      return incrementQuantityOptimistic(1L, 0);
    };

    Callable<VersionedItem> callable2 = () -> {
      return incrementQuantityOptimistic(1L, 0);
    };

    Future<VersionedItem> future1 = asyncProcessor.doit(callable1);
    sleep(1000);
    Future<VersionedItem> future2 = asyncProcessor.doit(callable2);

    VersionedItem item1 = future1.get();
    VersionedItem item2 = future2.get();
    
    System.out.println(item1);
    System.out.println(item2);
    assertEquals("The item returned without delay should have correct quantity", 2,
        item2.getQuantity());
    assertEquals("The item returned by the delayed execution should have correct quantity", 1,
        item1.getQuantity());
    
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testConcurrentIncrement() throws InterruptedException, ExecutionException {

    Callable<Item> callable1 = () -> {
      return incrementQuantity(1L, 500);
    };

    Callable<Item> callable2 = () -> {
      return incrementQuantity(1L, 0);
    };

    // start execution in parallel
    Future<Item> future1 = asyncProcessor.doit(callable1);
    Future<Item> future2 = asyncProcessor.doit(callable2);

    Item item1 = future1.get();
    Item item2 = future2.get();

    System.out.println(item1);
    System.out.println(item2);
    assertEquals("The item returned without delay should have correct quantity", Integer.valueOf(1),
        item2.getQuantity());
    assertEquals("The item returned by the delayed execution should have correct quantity", Integer.valueOf(2),
        item1.getQuantity());
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
