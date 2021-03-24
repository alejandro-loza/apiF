package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*
import org.springframework.data.jpa.repository.Query

interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor {
  
  Client findOneByUsername( String username )

  @Query(value = "select * from clients where enabled = 1 and date_deleted is null and email is not null", nativeQuery = true)
  Client notificationEmail()

}
