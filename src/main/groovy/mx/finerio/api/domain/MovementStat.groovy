package mx.finerio.api.domain;

import java.sql.Timestamp

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString
import mx.finerio.api.domain.Movement.Type

import org.hibernate.annotations.GenericGenerator



@Entity
@Table(name = 'movement_stat')
@ToString(includeNames = true, includePackage = false)
class MovementStat {
	

	  @Id @GeneratedValue
	  @Column(name = 'id', updatable = false)
	  Long id

	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = 'user_id', nullable = false)
	  User user

	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = 'category_id', nullable = true)
	  Category category

	  @Enumerated(EnumType.STRING)
	  @Column(name = 'type', nullable = false)
	  Type type

	  @Column(name = 'amount', nullable = false)
	  BigDecimal amount
	  	  
	  @Column(name = 'init_date', nullable = true)
	  Timestamp initDate
	  
	  @Column(name = 'final_date', nullable = true)
	  Timestamp finalDate
}
