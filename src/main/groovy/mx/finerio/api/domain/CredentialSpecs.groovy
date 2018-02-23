package mx.finerio.api.domain

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import mx.finerio.api.dtos.CredentialListDto

import org.springframework.data.jpa.domain.Specification

class CredentialSpecs {

  static Specification findAll( CredentialListDto dto ) {

    return new Specification() {
      Predicate toPredicate( Root root, CriteriaQuery query,
          CriteriaBuilder builder ) {

        def predicates = []

        if ( dto.customer ) {
          predicates << builder.equal( root.get( 'customer' ), dto.customer )
        }

        if ( dto.dateCreated ) {
          predicates << builder.greaterThanOrEqualTo(
              root.get( 'dateCreated' ), dto.dateCreated )
        }

        predicates <<  builder.isNull( root.get( 'dateDeleted' ) )
        query.orderBy( builder.asc( root.get( 'dateCreated' ) ) )
        builder.and( predicates.toArray( new Predicate[ predicates.size() ] ) )

      }
    }

  }

}
