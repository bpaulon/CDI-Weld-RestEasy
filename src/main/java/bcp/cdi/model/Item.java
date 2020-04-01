package bcp.cdi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity(name="item")
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
  @SequenceGenerator(name = "SEQ_GEN", sequenceName = "ITEM_SEQ", allocationSize = 1)
  Long id;
  
  String name;

  Integer quantity;
  
  
}
