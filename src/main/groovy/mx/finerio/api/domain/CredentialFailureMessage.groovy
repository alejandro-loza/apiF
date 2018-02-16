package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

@Entity
@Table(name = 'credential_failure_messages')
@ToString(includePackage = false, includeNames = true)
class CredentialFailureMessage {
  
  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'institution_id', nullable = false)
  FinancialInstitution institution

  @NotNull
  @Size(min = 1, max = 200)
  @Column(name = 'original_message')
  String originalMessage

  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = 'friendly_message')
  String friendlyMessage

  @NotNull
  @Column(name = 'date_created')
  Date dateCreated

  @NotNull
  @Column(name = 'last_updated')
  Date lastUpdated

}
