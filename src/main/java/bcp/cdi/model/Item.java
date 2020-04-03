package bcp.cdi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQuery;

import lombok.Data;

@NamedQuery(name="lockItem",
query="SELECT i FROM item i WHERE i.name LIKE :itemName",
lockMode = LockModeType.PESSIMISTIC_WRITE)

@Data
@Entity(name = "item")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String name;

  int quantity;

}
