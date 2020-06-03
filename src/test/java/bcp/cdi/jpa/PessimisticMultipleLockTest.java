package bcp.cdi.jpa;

import static bcp.cdi.jpa.DBUtil.runInTransaction;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

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
public class PessimisticMultipleLockTest {

  static final Operation DELETE_ALL = deleteAllFrom("item");
  static final Operation INSERT_ITEMS = insertInto("item")
      .columns("id", "name", "quantity", "version")
      .values(1L, "Dell monitor", 0, 1)
      .values(2L, "pc", 0, 1)
      .build();

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

  List<Item> incrementQuantity(Long id, int timeout) {
    return runInTransaction(em, () -> {
      Query query = em.createQuery("from item");
      query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
      
      List<Item> items = query.getResultList();
      
      sleep(timeout);
      items.stream().forEach(i -> {
        int qty = i.getQuantity();
        i.setQuantity(++qty);
      });

      return items;
    });
  }
  
  
  List<Item> incrementQuantity2(Long id, int timeout) {
    return runInTransaction(em, () -> {
      Query query = em.createQuery("from item where id = 2");
      query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
      
      List<Item> items = query.getResultList();
      
      sleep(timeout);
      items.stream().forEach(i -> {
        int qty = i.getQuantity();
        i.setQuantity(++qty);
      });

      return items;
    });
  }
  

  @Test
  public void testShouldUpdateCorrectlyWhenItemLocked() throws InterruptedException, ExecutionException {

    Callable call1 = () -> incrementQuantity(1L, 2000);
    Callable call2 = () -> incrementQuantity2(1L, 0);

    // start execution in parallel
    Future<List<Item>> future1 = asyncProcessor.doit(call1);
    // make sure the transactions are started in this sequence
    sleep(200);
    Future<List<Item>> future2 = asyncProcessor.doit(call2);

    // wait and get results
    List<Item> update1 = future1.get();
    List<Item> update2 = future2.get();

    System.out.println("first update: " + update1);
    System.out.println("second update:" + update2);
    
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
