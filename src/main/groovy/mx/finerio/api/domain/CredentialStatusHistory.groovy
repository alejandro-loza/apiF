package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'credential_status_history')
@ToString(includeNames = true, includePackage = false)
class CredentialStatusHistory {

  enum Status {
      VALIDATE,
      TOKEN,
      ACTIVE,
      INACTIVE,
      INVALID
  }

  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'credential_id')
  Credential credential

  @Enumerated(EnumType.STRING)
  @Column(name = 'status', nullable = false)
  Status status

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted

}
