package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'category')
@ToString(includes = 'id, name', includeNames = true, includePackage = false)
public class Category{

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id

  @Column(name = 'name', nullable = false )
  String name

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'user_id', nullable = true)
  User user

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'parent_id', nullable = true)
  Category parent


}
