package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'country')
@ToString(includes = 'id, name', includeNames = true, includePackage = false)
public class Country{

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id

  @Column(name = 'code', nullable = false )
  String code

  @Column(name = 'name', nullable = false )
  String name

  @Column(name = 'image_url', nullable = false )
  String imageUrl

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted
}
