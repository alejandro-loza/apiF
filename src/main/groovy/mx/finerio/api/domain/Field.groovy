package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*

@Entity
@Table(name = 'fields')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class Field {

	@Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(strategy=GenerationType.AUTO)
  Long id
  
	@ManyToOne
  @JoinColumn(name = "credential_id")
  Credential credential

  @Column(name = 'name', nullable = false)
  String name

  @Column(name = 'value', nullable = false)
  String value

  @Column(name = 'date_created', nullable = true)
  Date dateCreated

  @Column(name = 'last_updated', nullable = true)
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted

}
