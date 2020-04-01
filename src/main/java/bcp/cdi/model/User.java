package bcp.cdi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.Data;

@Data
@Entity(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
  @SequenceGenerator(name = "SEQ_GEN", sequenceName = "USER_SEQ", allocationSize = 1)
  Long id;

  String firstName;

  String secondName;
  
  @Version
  private int version;

}
