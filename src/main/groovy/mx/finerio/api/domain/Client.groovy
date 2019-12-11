package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = 'clients')
@ToString(includePackage = false, includeNames = true, excludes = ['password'])
class Client implements UserDetails {
  
  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  String id

  @Column(name = 'username', nullable = false, length = 50, unique = true)
  String username

  @Column(name = 'password', nullable = false, length = 255)
  String password

  @Column(name = 'enabled', nullable = false)
  boolean enabled

  @Column(name = 'account_non_expired', nullable = false)
  boolean accountNonExpired

  @Column(name = 'account_non_locked', nullable = false)
  boolean accountNonLocked

  @Column(name = 'credentials_non_expired', nullable = false)
  boolean credentialsNonExpired

  @Column(name = 'categorize_transactions', nullable = false)
  boolean categorizeTransactions

  @Column(name = 'use_transactions_table', nullable = false)
  boolean useTransactionsTable

  @Transient
  List authorities

}
