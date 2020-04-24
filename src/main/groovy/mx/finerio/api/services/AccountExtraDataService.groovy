package mx.finerio.api.services

import mx.finerio.api.domain.AccountExtraData
import mx.finerio.api.domain.repository.AccountExtraDataRepository
import mx.finerio.api.dtos.CreateAllAccountExtraDataDto
import mx.finerio.api.dtos.AccountExtraDataDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountExtraDataService {

  @Autowired
  AccountService accountService

  @Autowired
  AccountExtraDataRepository accountExtraDataRepository

  void createAll( CreateAllAccountExtraDataDto dto ) throws Exception {

    dto.extraData?.each {

      if ( it.value instanceof Map ) { 
        def newDto = new CreateAllAccountExtraDataDto()
        newDto.accountId = dto.accountId
        newDto.extraData = it.value
        newDto.prefix = "${dto.prefix}${it.key}_"
        createAll( newDto )
      } else if ( it.value != null ) {
        def accountExtraDataDto = new AccountExtraDataDto()
        accountExtraDataDto.accountId = dto.accountId
        accountExtraDataDto.name = "${dto.prefix}${it.key}"
        accountExtraDataDto.value = it.value as String
        create( accountExtraDataDto )
      }

    }

  }

  AccountExtraDataDto findByAccountAndName( String accountId, String name )
      throws Exception {

    def account = accountService.findById( accountId )
    def instance = accountExtraDataRepository.findFirstByAccountAndName(
        account, name )
    if ( !instance ) { return null }
    def dto = new AccountExtraDataDto()
    dto.accountId = accountId
    dto.name = instance.name
    dto.value = instance.value
    return dto

  }

  private void create( AccountExtraDataDto dto ) throws Exception {

    def account = accountService.findById( dto.accountId )
    def instance = accountExtraDataRepository.findFirstByAccountAndName(
        account, dto.name )

    if ( instance == null ) {
      instance = new AccountExtraData()
      instance.account = account
      instance.name = dto.name.take( 100 )
      instance.dateCreated = new Date()
    }

    instance.value = dto.value.take( 100 )
    instance.lastUpdated = new Date()
    accountExtraDataRepository.save( instance )

  }

}
