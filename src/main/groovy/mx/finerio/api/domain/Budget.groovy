package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*

@Entity
@Table(name = 'budget_api')
@ToString(includeNames = true, includePackage = false)
class Budget  {

    @Id @GeneratedValue
    @Column(name = 'id', updatable = false)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'customer_id', nullable = false)
    Customer customer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'category_id', nullable = false)
    Category category

    @Column(name = 'name', nullable = true, length = 255)
    String name

    @Column(name = 'amount', nullable = false )
    BigDecimal amount

    @Column(name = 'date_created', nullable = false)
    Date dateCreated

    @Column(name = 'last_updated', nullable = false)
    Date lastUpdated

    @Column(name = 'date_deleted', nullable = false)
    Date dateDeleted

    @Column(name = 'warning_percentage', nullable = false)
    BigDecimal warningPercentage
}
