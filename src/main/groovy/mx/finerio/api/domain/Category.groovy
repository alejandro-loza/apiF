package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

@Entity
@Table(name = 'category')
@ToString(includes = 'id', includeNames = true, includePackage = false)
public class Category{

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  String id

  @Column(name = 'name', nullable = false )
  String name

  @Column(name = 'keywords', nullable = true )
  String keywords

  @Column(name = 'text_color', nullable = false )
  String textColor

  @Column(name = 'color', nullable = false )
  String color

  @Column(name = 'activity_codes', nullable = false )
  String activityCodes

  @Column(name = 'version', nullable = false)
  Long version

  @Column(name = 'order_index', nullable = false)
  Integer orderIndex
/*
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'user_id', nullable = true)
  User user
*/
//parent_id

}
