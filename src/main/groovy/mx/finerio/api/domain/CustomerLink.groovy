package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = 'customer_link')
@ToString(includePackage = false, includeNames = true)
class CustomerLink {

    @Id @GeneratedValue
    @Column(name = 'id', updatable = false)
    Long id

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'customer_id')
    Customer customer

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = 'link_id')
    String linkId
    @NotNull
    @Column(name = 'date_created')
    Date dateCreated

    @NotNull
    @Column(name = 'last_updated')
    Date lastUpdated

    @Column(name = 'date_deleted', nullable = true)
    Date dateDeleted

}
