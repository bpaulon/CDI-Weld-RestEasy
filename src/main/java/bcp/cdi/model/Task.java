package bcp.cdi.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import lombok.Data;

@NamedQuery(name="Task.findTaskByTitle" , 
      query=" SELECT t " 
          + " FROM task t"
          + " WHERE t.title LIKE :title")
@Data
@Entity(name = "task")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String title;

  @OneToMany(fetch=FetchType.LAZY)
  @JoinColumn(name = "task_id")
  List<TaskFile> files;
  
}


