package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = 'user')
@ToString(includePackage = false, includeNames = true, includes = ['username'])
class User implements UserDetails {
  
  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  String id

  @Column(name = 'email', nullable = false, unique = true)
  String username

  @Column(name = 'password', nullable = false)
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

  List getJsonAttributes() {
    [ 'username', 'authorities' ]
  }

}
