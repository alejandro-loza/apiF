import mx.finerio.api.domain.Client

import javax.persistence.*
import javax.validation.constraints.*
import groovy.transform.ToString

@Entity
@Table(name = 'client_config')
@ToString(includePackage = false, includeNames = true)
class ClientConfig {

    enum Property {
        COUNTRY
    }

    @Id @GeneratedValue
    @Column(name = 'id', updatable = false)
    Long id

    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = 'property')
    Property property

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = 'value')
    String value


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'client_id')
    Client client

    @NotNull
    @Column(name = 'date_created')
    Date dateCreated

    @NotNull
    @Column(name = 'last_updated')
    Date lastUpdate

    @Column(name = 'date_deleted', nullable = true)
    Date dateDeleted

}

