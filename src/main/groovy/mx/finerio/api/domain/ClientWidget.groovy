package mx.finerio.api.domain

import groovy.transform.ToString
import javax.persistence.*
import javax.validation.constraints.*


@Entity
@Table(name = 'clients_widget')
@ToString( includeNames = true, includePackage = false)
class ClientWidget {
 
  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'client_id', nullable = false)
  Client client

  @Column(name = 'widget_id', nullable = false, length = 255)
  Long widgetId

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted

  @Column(name = 'version', nullable = false)
  Long version = 0


}
