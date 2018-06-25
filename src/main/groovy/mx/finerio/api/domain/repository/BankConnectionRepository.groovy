package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository

import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.Credential

interface BankConnectionRepository extends JpaRepository<BankConnection, Long> {

  BankConnection findFirstByCredentialAndStatus( Credential credential,
      BankConnection.Status status )

  BankConnection findFirstByCredentialOrderByStartDateDesc(
      Credential credential )

}
