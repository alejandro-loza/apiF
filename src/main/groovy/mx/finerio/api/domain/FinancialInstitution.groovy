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

   enum InstitutionType {
    PERSONAL,
    BUSINESS
  }

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  Long id

    @Column(name = 'version', nullable = false)
    Long version

  @Column(name = 'code', nullable = false, length = 255)
  String code

  @Column(name = 'internal_code', nullable = false, length = 255)
  String internalCode

  @Column(name = 'description', nullable = true, length = 255)
  String description

  @Column(name = 'name', nullable = false, length = 255)
  String name

  @Enumerated(EnumType.STRING)
  @Column(name = 'status', nullable = false, length = 255)
  Status status

  @Enumerated(EnumType.STRING)
  @Column(name = 'institution_type', nullable = false, length = 255)
  InstitutionType institutionType

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'country_id', nullable = true)
  Country country

}
