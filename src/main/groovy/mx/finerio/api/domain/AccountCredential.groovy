package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

@Entity
@Table(name = 'account_credential')
@ToString( includeNames = true, includePackage = false)
public class AccountCredential{

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(strategy=GenerationType.AUTO)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'account_id', nullable = false)
  Account account

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'credential_id', nullable = false)
  Credential credential

    @Column(name = 'version', nullable = false)
    Long version

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated
}
