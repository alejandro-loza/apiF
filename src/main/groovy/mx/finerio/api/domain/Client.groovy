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

  @Column(name = 'enabled', nullable = true)
  boolean enabled

  @Column(name = 'account_non_expired', nullable = true)
  boolean accountNonExpired

  @Column(name = 'account_non_locked', nullable = true)
  boolean accountNonLocked

  @Column(name = 'credentials_non_expired', nullable = true)
  boolean credentialsNonExpired

  @Transient
  List authorities

}
