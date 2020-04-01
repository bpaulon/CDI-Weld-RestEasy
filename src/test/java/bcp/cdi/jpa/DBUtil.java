package bcp.cdi.jpa;

import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBUtil {

  public static <T> T runInTransaction(EntityManager entityManager, Supplier<T> operation) {
    EntityTransaction transaction = entityManager.getTransaction();
    if (transaction.isActive()) {
      return operation.get();
    } else {
      try {
        transaction.begin();
        T result = operation.get();
        transaction.commit();
        return result;
      } catch (RuntimeException e) {
        log.error("Operation failed", e);
        //transaction.rollback();
        throw e;
      }
    }
  }
}
