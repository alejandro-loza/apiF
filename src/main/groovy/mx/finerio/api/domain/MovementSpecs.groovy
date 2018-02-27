package mx.finerio.api.domain

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import mx.finerio.api.dtos.MovementListDto

import org.springframework.data.jpa.domain.Specification

class MovementSpecs {

  static Specification findAll( MovementListDto dto ) {

    return new Specification() {
      Predicate toPredicate( Root root, CriteriaQuery query,
          CriteriaBuilder builder ) {

        def predicates = []

        if ( dto.account ) {
          predicates << builder.equal( root.get( 'account' ), dto.account )
        }

        if ( dto.dateCreated ) {
          predicates << builder.greaterThanOrEqualTo(
              root.get( 'customDate' ), dto.dateCreated )
        }

        predicates <<  builder.isNull( root.get( 'dateDeleted' ) )
        query.orderBy( builder.asc( root.get( 'customDate' ) ) )
        builder.and( predicates.toArray( new Predicate[ predicates.size() ] ) )

      }
    }

  }

}
