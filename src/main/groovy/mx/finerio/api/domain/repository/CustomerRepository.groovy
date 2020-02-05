package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Customer

interface CustomerRepository extends JpaRepository<Customer, Long>,
    JpaSpecificationExecutor {
  
  Customer findFirstByClientAndNameAndDateDeletedIsNull( Client client, String name )

}
