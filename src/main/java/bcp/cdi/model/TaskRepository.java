package bcp.cdi.model;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class TaskRepository {

  @Inject
  EntityManager em;
  
  public List<Task> findTasksByTitle(String searchTitle) {
    TypedQuery<Task> q = em.createQuery("SELECT t FROM task t WHERE t.title like :title", Task.class);
    q.setParameter("title", "%" + searchTitle + "%");
    return q.getResultList();
  }
  
  public List<Task> findTaskByTitleNamed(String searchTitle) {
    TypedQuery<Task> q = em.createNamedQuery("Task.findTaskByTitle", Task.class);
    q.setParameter("title", "%" + searchTitle + "%");
    return q.getResultList();
  }
  
}
