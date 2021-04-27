package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = 'incomeFrom')
@ToString(includeNames = true, includePackage = false)
class SuggestedExpenses {

  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'category_id', nullable = true)
  Category category

  @Column(name = 'income_from', nullable = false)
  BigDecimal  incomeFrom

  @Column(name = 'income_to', nullable = false)
  BigDecimal  incomeTo

  @Column(name = 'suggested_percentage', nullable = false)
  BigDecimal  suggestedPercentage

  @Column(name = 'others_expenses', nullable = true)
  BigDecimal  othersExpenses

  @Column(name = 'date_created', nullable = false)
  Timestamp dateCreated

  @Column(name = 'last_updated', nullable = false)
  Timestamp lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Timestamp dateDeleted

}
