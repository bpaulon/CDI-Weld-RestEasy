package bcp.cdi.jpa;

import static bcp.cdi.jpa.DBUtil.runInTransaction;
import static org.junit.Assert.assertEquals;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bcp.cdi.model.User;

@EnableAutoWeld
@AddPackages({ ApplicationTestResources.class })
@ActivateScopes({ RequestScoped.class })
public class MyResourceTest {

  private static final String NEW_FIRST_NAME = "new firstName";
  private static final String NEW_SECOND_NAME = "new secondName";

  @Inject
  EntityManager em;

  @BeforeEach
  public void setup() throws Exception {
    runInTransaction(em, () -> {
      User user001 = new User();
      user001.setFirstName("first001");
      user001.setSecondName("se");
      em.persist(user001);
      return user001;
    });
  }

  void updateUserFirstName(String firstName, Long userID) {
    runInTransaction(em, () -> {
      Query query = em.createQuery("UPDATE user SET firstName = :firstName "
          + " WHERE id = :userID");

      query.setParameter("userID", userID);
      query.setParameter("firstName", firstName);

      return query.executeUpdate();
    });
  }

  void updateUserSecondName(String secondName, Long userID) {
    runInTransaction(em, () -> {
      User user001 = em.find(User.class, 1L);

      user001.setSecondName(secondName);
      return user001;
    });
  }

  @Test
  public void testUpdateUsingSQLDoesNotUpdateEntity() {
    updateUserSecondName(NEW_SECOND_NAME, 1L);
    updateUserFirstName(NEW_FIRST_NAME, 1L);

    User user01 = em.find(User.class, 1L);

    assertEquals(user01.getSecondName(), NEW_SECOND_NAME);
    // FAILS expected:<[first001]> but was:<[new firstName]>
    assertEquals(user01.getFirstName(), NEW_FIRST_NAME);
  }

}
