package mx.finerio.api.domain

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import mx.finerio.api.dtos.CustomerListDto

import org.springframework.data.jpa.domain.Specification

class CustomerSpecs {

  static Specification findAll( CustomerListDto dto ) {

    return new Specification() {
      Predicate toPredicate( Root root, CriteriaQuery query,
          CriteriaBuilder builder ) {

        def predicates = []

        if ( dto.client ) {
          predicates << builder.equal( root.get( 'client' ), dto.client )
        }

        if ( dto.cursor ) {
          predicates << builder.ge( root.get( 'id' ), dto.cursor )
        }

        if ( dto.word ) {
          predicates << builder.like( root.get( 'name' ), "%${dto.word}%" )             
        }

        predicates << builder.isNull( root.get( 'dateDeleted' ) )
        query.orderBy( builder.asc( root.get( 'id' ) ) )
        builder.and( predicates.toArray( new Predicate[ predicates.size() ] ) )

      }
    }

  }

}
