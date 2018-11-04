package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface CreditDetailsRepository extends JpaRepository<CreditDetails, Long>, JpaSpecificationExecutor {

  CreditDetails findByAccount( Account account )

}
