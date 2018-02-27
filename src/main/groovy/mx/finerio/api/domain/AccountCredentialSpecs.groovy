package mx.finerio.api.domain

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import mx.finerio.api.dtos.AccountListDto

import org.springframework.data.jpa.domain.Specification

class AccountCredentialSpecs {

  static Specification findAll( AccountListDto dto ) {

    return new Specification() {
      Predicate toPredicate( Root root, CriteriaQuery query,
          CriteriaBuilder builder ) {

        def predicates = []

        if ( dto.credential ) {
          predicates << builder.equal( root.get( 'credential' ), dto.customer )
        }

        if ( dto.dateCreated ) {
          predicates << builder.greaterThanOrEqualTo(
              root.get( 'account.dateCreated' ), dto.dateCreated )
        }

        predicates <<  builder.isNull( root.get( 'account.dateDeleted' ) )
        query.orderBy( builder.asc( root.get( 'account.dateCreated' ) ) )
        builder.and( predicates.toArray( new Predicate[ predicates.size() ] ) )

      }
    }

  }

}
