package bcp.cdi.jpa;

import static bcp.cdi.jpa.DBUtil.runInTransaction;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import bcp.cdi.model.User;

@EnableAutoWeld
@AddBeanClasses({ ApplicationTestResources.class})

@ActivateScopes({ RequestScoped.class })
public class FindAndUpdateTest {

  private static final String NEW_FIRST_NAME = "new firstName";
  private static final String NEW_SECOND_NAME = "new secondName";

  static final Operation DELETE_ALL = deleteAllFrom("user");
  static final Operation INSERT_USERS = insertInto("user")
      .columns("id", "first_name", "secondName")
      .values(1L, "first name", "second name").build();

  @Inject
  EntityManager em;
      
  @BeforeEach
  public void setup() throws Exception {
    Operation operation = sequenceOf(DELETE_ALL, INSERT_USERS);
    
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(ApplicationTestResources.ds), operation);
    dbSetup.launch();
  }
  
  void updateUserFirstName(Long id, String newFirstName) {
    runInTransaction(em, () -> {
      Query query = em.createQuery("UPDATE user SET firstName = :firstName "
          + " WHERE id = :id");
      query.setParameter("id", id);
      query.setParameter("firstName", newFirstName);

      return query.executeUpdate();
    });
  }

  User updateUserSecondName(Long id, String secondName) {
    return runInTransaction(em, () -> {
      User user = em.find(User.class, 1L);

      user.setSecondName(secondName);
      return user;
    });
  }

  @Test
  public void testUpdateUsingSQLDoesNotUpdateEntity() {
    updateUserSecondName(1L, NEW_SECOND_NAME); // JPA update
    updateUserFirstName(1L, NEW_FIRST_NAME); // SQL update
    
    // Need to refresh the user entity
    // em.refresh(user);
    // or
    // em.clear();
    
    User user = em.find(User.class, 1L);
    
    assertEquals(NEW_SECOND_NAME, user.getSecondName());
    // FAILS the user's first name was not updated in the persistence context
    assertEquals(NEW_FIRST_NAME, user.getFirstName());
  }
  
  @Test
  public void testMergeForExistingIDUpdatesData() {
    User user = User.builder().id(1L).firstName(NEW_FIRST_NAME).secondName(NEW_SECOND_NAME).build();
    user = updateUser(user);
   
    assertEquals(NEW_FIRST_NAME, user.getFirstName());
  }

  User updateUser(User user) {
    return runInTransaction(em, () -> {
      if (em.find(User.class, user.getId()) == null) {
        throw new IllegalArgumentException("Unknown user id");
      }
      return em.merge(user);
    });
  }
  
  @Test
  public void testMergeForNotExistingIDInsertsData() {
    User user = User.builder().id(2L).firstName(NEW_FIRST_NAME).secondName(NEW_SECOND_NAME).build();
    user = updateUserUnknownID(user);
   
    assertEquals(NEW_FIRST_NAME, user.getFirstName());
    System.out.println(em.createQuery("from user").getResultList());
  }
  
  User updateUserUnknownID(User user) {
    return runInTransaction(em, () -> {
      return em.merge(user);
    });
  }
}
