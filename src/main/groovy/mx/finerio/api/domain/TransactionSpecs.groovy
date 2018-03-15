package mx.finerio.api.domain

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import mx.finerio.api.dtos.TransactionListDto

import org.springframework.data.jpa.domain.Specification

class TransactionSpecs {

  static Specification findAll( TransactionListDto dto ) {

    return new Specification() {
      Predicate toPredicate( Root root, CriteriaQuery query,
          CriteriaBuilder builder ) {

        def predicates = []

        if ( dto.account ) {
          predicates << builder.equal( root.get( 'account' ), dto.account )
        }

        if ( dto.id ) {
          predicates << builder.lessThanOrEqualTo( root.get( 'id' ), dto.id )
        }

        predicates <<  builder.isNull( root.get( 'dateDeleted' ) )
        query.orderBy( builder.desc( root.get( 'id' ) ) )
        builder.and( predicates.toArray( new Predicate[ predicates.size() ] ) )

      }
    }

  }

}
