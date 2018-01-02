package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*


@Entity
@Table(name = 'credentials')
@ToString(includes = 'id,status', includeNames = true, includePackage = false)
class Credential {

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(strategy=GenerationType.AUTO)  
  Long id

  @Column(name = 'finantial_institution_id', nullable = false)
  Long finantialInstitutionId

  @Column(name = 'status', nullable = false)
  String status

  @Column(name = 'date_created', nullable = true)
  Date dateCreated

  @Column(name = 'last_updated', nullable = true)
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted
  
}
