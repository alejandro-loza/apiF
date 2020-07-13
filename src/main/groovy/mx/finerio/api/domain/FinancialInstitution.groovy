package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*

@Entity
@Table(name = 'financial_institution')
@ToString(includeNames = true, includePackage = false)
class FinancialInstitution {

  enum Status {
    ACTIVE,
    INACTIVE,
    DELETED
  }

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  Long id

    @Column(name = 'version', nullable = false)
    Long version

  @Column(name = 'code', nullable = false, length = 255)
  String code

  @Column(name = 'description', nullable = true, length = 255)
  String description

  @Column(name = 'name', nullable = false, length = 255)
  String name

  @Enumerated(EnumType.STRING)
  @Column(name = 'status', nullable = false, length = 255)
  Status status

}
