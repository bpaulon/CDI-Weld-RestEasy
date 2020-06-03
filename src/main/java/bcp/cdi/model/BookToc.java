package bcp.cdi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;

@Data
@Entity(name = "bookToc")
public class BookToc {

  @Id
  Long id;

  String content;
  
  @ToString.Exclude
  @OneToOne
  @MapsId
  @JoinColumn(name = "book_id")
  Book book;
  
}


