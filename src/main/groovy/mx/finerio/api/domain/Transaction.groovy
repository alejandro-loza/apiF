package mx.finerio.api.domain

import java.sql.Timestamp

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'transactions')
@ToString(includeNames = true, includePackage = false)
public class Transaction {

  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'account_id', nullable = false)
  Account account
  
  @Column(name = 'bank_date', nullable = false)
  Timestamp bankDate

  @Column(name = 'description', nullable = false, length = 255)
  String description

  @Column(name = 'cleaned_description', nullable = true, length = 255)
  String cleanedDescription

  @Column(name = 'amount', nullable = false)
  BigDecimal amount

  @Column(name = 'charge', nullable = false)
  Boolean charge

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'category_id', nullable = true)
  Category category
  
  @Column(name = 'scraper_id', nullable = true, length = 255)
  String scraperId

  @Column(name = 'duplicated', nullable = false)
  Boolean duplicated

  @Column(name = 'date_created', nullable = false)
  Timestamp dateCreated

  @Column(name = 'last_updated', nullable = false)
  Timestamp lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Timestamp dateDeleted

}
