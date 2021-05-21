package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = 'advice')
@ToString(includeNames = true, includePackage = false)
class Advice {

  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @Column(name = 'description', nullable = false, length = 255)
  String description

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'category_id', nullable = false)
  Category category

  @Column(name = 'date_created', nullable = false)
  Timestamp dateCreated

  @Column(name = 'last_updated', nullable = false)
  Timestamp lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Timestamp dateDeleted

}
