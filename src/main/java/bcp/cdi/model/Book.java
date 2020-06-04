package bcp.cdi.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity(name = "book")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  
  String title;
  
  // remove the relation to fix 1+N problem or use optional=false if
  // the relation always exists
  @OneToOne(fetch = FetchType.LAZY, mappedBy = "book"/*, optional=false*/)
  BookToc toc;
}
