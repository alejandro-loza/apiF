package mx.finerio.api.domain

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import org.springframework.data.jpa.domain.Specification

import mx.finerio.api.dtos.MovementStatListDto

class MovementStatSpecs {
	
	static Specification findAll( MovementStatListDto dto ) {
		
			return new Specification() {
			  Predicate toPredicate( Root root, CriteriaQuery query,
				  CriteriaBuilder builder ) {
		
				def predicates = []
		
				if ( dto.category ) {
				  predicates << builder.equal( root.get( 'category' ), dto.category )
				}
				
				if ( dto.type ) {
				  predicates << builder.equal( root.get( 'type' ), dto.type )
			    }
				  
				if ( dto.greaterAmount ) {
				  predicates << builder.greaterThanOrEqualTo(root.get( 'amount' ), dto.greaterAmount )
				}
				
				if ( dto.lessAmount ){
					predicates << builder.lessThanOrEqualTo(root.get( 'amount' ), dto.lessAmount )
				  }
				  
				if ( dto.fromDate ){
					  predicates << builder.greaterThanOrEqualTo(root.get( 'initDate' ), dto.fromDate )
					}
					
				if ( dto.toDate ){
					 predicates << builder.lessThanOrEqualTo(root.get( 'finalDate' ), dto.toDate )
					  }
					 
			     predicates <<  builder.equal( root.get( 'user' ), dto.user  )
				 predicates << builder.greaterThan(root.get( 'amount' ), 0)
				 query.orderBy( builder.asc( root.get( 'initDate' ) ) )
				 builder.and( predicates.toArray( new Predicate[ predicates.size() ] ) )
		
			  }
			}
		
		  }

}
