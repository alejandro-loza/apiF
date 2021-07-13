package mx.finerio.api.domain

import javax.persistence.*
import groovy.transform.ToString
import javax.validation.constraints.NotNull

@Entity
@Table(name = 'clientConfig')
@ToString(includes = 'id, name', includeNames = true, includePackage = false)
public class ClientConfig {

    enum Property {
        COUNTRY_CODE, INSTITUTION_TYPE, MAGIC_LINK_EMAIL_TEMPLATE, SATWS_APIKEY,INSTITUTIONS_GRANTED
    }

    @Id @GeneratedValue
    @Column(name = 'id', updatable = false)
    Long id

    @Column(name = 'property', nullable = false )
    String property

    @Column(name = 'value', nullable = false )
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
    Date lastUpdated

    @Column(name = 'date_deleted', nullable = true)
    Date dateDeleted
}

