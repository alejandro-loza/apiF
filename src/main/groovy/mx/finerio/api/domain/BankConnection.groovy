package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

@Entity
@Table(name = 'bank_connections')
@ToString(includePackage = false, includeNames = true)
class BankConnection {
  
  enum Status {
    PENDING,
    SUCCESS,
    FAILURE
  }

  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'credential_id')
  Credential credential

  @NotNull
  @Column(name = 'start_date')
  Date startDate

  @Column(name = 'end_date')
  Date endDate

  @Enumerated(EnumType.STRING)
  @Column(name = 'status', nullable = false)
  Status status

}
