package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.AccountExtraData

interface AccountExtraDataRepository
    extends JpaRepository<AccountExtraData, String>,
    JpaSpecificationExecutor {
  
  AccountExtraData findFirstByAccountAndName( Account account, String name )

  List<AccountExtraData> findAllByAccount( Account account )

}
