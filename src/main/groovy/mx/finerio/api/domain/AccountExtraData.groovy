package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*

import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'account_extra_data')
@ToString(includeNames = true, includePackage = false)
class AccountExtraData {

  @Id @GeneratedValue
  @Column(name = 'id', nullable = false, updatable = false)
  String id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'account_id', nullable = false)
  Account account

  @Column(name = 'name', nullable = false, length = 100)
  String name

  @Column(name = 'value', nullable = false, length = 100)
  String value

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

}
