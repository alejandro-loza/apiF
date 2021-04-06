package mx.finerio.api.domain

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import mx.finerio.api.dtos.FinancialInstitutionListDto
import org.springframework.data.jpa.domain.Specification
import mx.finerio.api.domain.FinancialInstitution

class FinancialIntitutionSpecs {


  static Specification findAll( FinancialInstitutionListDto dto ) {

    return new Specification() {
      Predicate toPredicate( Root root, CriteriaQuery query,
          CriteriaBuilder builder ) {

        def predicates = []

        if ( dto.country ) {
          predicates << builder.equal( root.get( 'country' ), dto.country )
        }

        if ( dto.type ) {
          predicates << builder.equal( root.get( 'institutionType' ), dto.type )
        }
           
        predicates << builder.notEqual( root.get( 'status' ), FinancialInstitution.Status.DELETED )
        predicates << builder.notEqual( root.get( 'code' ), 'DINERIO' )
        query.orderBy( builder.asc( root.get( 'id' ) ) )
        builder.and( predicates.toArray( new Predicate[ predicates.size() ] ) )

      }
    }

  }

}
