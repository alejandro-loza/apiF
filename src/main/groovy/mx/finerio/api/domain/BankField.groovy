package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*

@Entity
@Table(name = 'field')
@ToString(includeNames = true, includePackage = false)
class BankField {

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(strategy=GenerationType.AUTO)
  Long id
  
  @ManyToOne
  @JoinColumn(name = "institution_id")
  FinancialInstitution financialInstitution

  @Column(name = 'provider_id', nullable = false)
  Long providerId

  @Column(name = 'name', nullable = false)
  String name

  @Column(name = 'friendly_name', nullable = false)
  String friendlyName

  @Column(name = 'position', nullable = false)
  Byte position

  @Column(name = 'type', nullable = false)
  String type

  @Column(name = 'interactive', nullable = false)
  Boolean interactive

  @Column(name = 'required', nullable = false)
  Boolean required

}
