package bcp.cdi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;

@Data
@Entity(name = "bookToc")
public class BookToc {

  @Id
  @Column(name = "book_id")
  Long id;

  String content;
  
  @ToString.Exclude
  @OneToOne
  @JoinColumn(name = "book_id")
  Book book;
  
}


