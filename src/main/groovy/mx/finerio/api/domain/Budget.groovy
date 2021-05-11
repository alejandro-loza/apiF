package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*

import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'budget')
@ToString(includeNames = true, includePackage = false)
class Budget  {

    @Id
    @Column(name = 'id', nullable = false, updatable = false)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    String id

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
