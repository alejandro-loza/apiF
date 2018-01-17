package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'category')
@ToString(includes = 'id', includeNames = true, includePackage = false)
public class Category{

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id

  @Column(name = 'name', nullable = false )
  String name

  @Column(name = 'keywords', nullable = true )
  String keywords

  @Column(name = 'text_color', nullable = false )
  String textColor

  @Column(name = 'color', nullable = false )
  String color

  @Column(name = 'activity_codes', nullable = true )
  String activityCodes

  @Column(name = 'version', nullable = false)
  Long version

  @Column(name = 'order_index', nullable = true)
  Integer orderIndex

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'user_id', nullable = true)
  User user

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'parent_id', nullable = true)
  Category parent


}
