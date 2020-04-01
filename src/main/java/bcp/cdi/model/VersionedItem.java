package bcp.cdi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VersionedItem {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
  @SequenceGenerator(name = "SEQ_GEN", sequenceName = "VERSIONED_ITEM_SEQ", allocationSize = 1)
  Long id;
  
  String name;

  int quantity;
  
  @Version
  private int version;
}
