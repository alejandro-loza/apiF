package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

@Entity
@Table(name = 'callbacks')
@ToString(includePackage = false, includeNames = true)
class Callback {
  
  enum Nature {
    NOTIFY,
    SUCCESS,
    FAILURE,
    ACCOUNTS,
    TRANSACTIONS
  }

  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @NotNull
  @Size(min = 1, max = 200)
  @Column(name = 'url')
  String url

  @Enumerated(EnumType.STRING)
  @Column(name = 'nature', nullable = false)
  Nature nature

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'client_id')
  Client client

  @NotNull
  @Column(name = 'date_created')
  Date dateCreated

  @NotNull
  @Column(name = 'last_updated')
  Date lastUpdated

}
