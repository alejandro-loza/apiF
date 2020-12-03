package mx.finerio.api.domain

import groovy.transform.ToString
import javax.persistence.*

@Entity
@Table(name = 'clients_mtls')
@ToString(includePackage = false, includeNames = true, includes = ['id', 'filename'])
class ClientMtls {
 
  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'client_id', nullable = false)
  Client client

  @Column(name = 'filename', nullable = false, length = 30)
  String filename

  @Column(name = 'secret', nullable = false, length = 255)
  String secret

  @Column(name = 'iv', nullable = false, length = 255)
  String iv

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted

}
