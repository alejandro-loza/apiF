package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = 'users')
@ToString(includePackage = false, includeNames = true, includes = ['username'])
class User implements UserDetails {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = 'id', nullable = false, updatable = false)
  Long id

  @NotNull(message = 'user.username.null')
  @Size(min = 1, max = 100, message = 'user.username.size')
  @Column(name = 'username', nullable = false, unique = true, length = 100)
  String username

  @NotNull(message = 'user.password.null')
  @Size(min = 1, max = 255, message = 'user.password.size')
  @Column(name = 'password', nullable = false)
  String password

  @Column(name = 'enabled', nullable = false)
  boolean enabled

  @Column(name = 'account_non_expired', nullable = false)
  boolean accountNonExpired

  @Column(name = 'account_non_locked', nullable = false)
  boolean accountNonLocked

  @Column(name = 'credentials_non_expired', nullable = false)
  boolean credentialsNonExpired

  @Transient
  List authorities

  @NotNull(message = 'user.dateCreated.null')
  @Column(name = 'date_created')
  Date dateCreated

  @NotNull(message = 'user.lastUpdated.null')
  @Column(name = 'last_updated')
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted

  List getJsonAttributes() {
    [ 'username', 'authorities' ]
  }

}
