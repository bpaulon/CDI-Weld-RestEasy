package bcp.cdi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  Long id;

  @Column(name="first_name")
  String firstName;

  String secondName;

}
