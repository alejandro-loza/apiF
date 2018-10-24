package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'movement')
@ToString(includes = 'id, description, amount, type, date',
    includeNames = true, includePackage = false)
public class Movement{

  enum Type {
    DEPOSIT, CHARGE
  }

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id

  @Column(name = 'version', nullable = false)
  Long version = 0

  @Column(name = 'date', nullable = false)
  Date date

  @Column(name = 'custom_date', nullable = false)
  Date customDate

  @Column(name = 'description', nullable = false)
  String description

  @Column(name = 'custom_description', nullable = false)
  String customDescription

  @Column(name = 'amount', nullable = false)
  BigDecimal amount

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'account_id', nullable = false)
  Account account
  
  @Enumerated(EnumType.STRING)
  @Column(name = 'type', nullable = false)
  Type type

  @Column(name = 'balance', nullable = false)
  BigDecimal balance

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'duplicated', nullable = false)
  Boolean duplicated = false

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'category_id', nullable = true)
  Category category
  
  @Column(name = 'has_concepts', nullable = true)
  Boolean hasConcepts
}
