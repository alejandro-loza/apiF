package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'credential')
@ToString(excludes = 'password, securityCode, iv', includeNames = true, includePackage = false)
class Credential {

  enum Status {
      VALIDATE,
      TOKEN,
      ACTIVE,
      INACTIVE,
      INVALID
  }

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id

  @Enumerated(EnumType.STRING)
  @Column(name = 'status', nullable = false)
  Status status 

  @Column(name = 'username', nullable = false, length = 255)
  String username

  @Column(name = 'password', nullable = false, length = 255)
  String password

  @Column(name = 'security_code', nullable = true, length = 255)
  String securityCode

  @Column(name = 'iv', nullable = true, length = 255)
  String iv

  @Column(name = 'version', nullable = false)
  Long version

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'user_id', nullable = false)
  User user

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'customer_id', nullable = true)
  Customer customer

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'institution_id', nullable = false)
  FinancialInstitution institution

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'provider_id', nullable = true)
  Long providerId

  @Column(name = 'error_code', nullable = true, length = 255)
  String errorCode

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted

  @Column(name = 'status_code', nullable = true, length = 5)
  String statusCode

}
