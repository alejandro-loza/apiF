package mx.finerio.api.services

import mx.finerio.api.domain.CreditDetails

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountDetailsService {

  @Autowired
  AccountExtraDataService accountExtraDataService

  @Autowired
  CreditDetailsService creditDetailsService

  Map findAllByAccount( String accountId ) throws Exception {

    def creditData = getCreditDetailsMap( accountId )
    def keys = [ 'creditLimit', 'cardNumber' ]
    def map = creditData.subMap( keys )
    addAccountExtraData( map, accountId, 'userData_name', 'name' )
    addAccountExtraData( map, accountId, 'clabe', 'clabe' )
    map.user = getUserData( accountId )
    map.debit = getDebitData( accountId )
    map.credit = creditData
    return map

  }

  private Map getCreditDetailsMap( String accountId ) throws Exception {

    def instance = creditDetailsService.findByAccountId( accountId )

    if ( !instance ) {
      instance = new CreditDetails()
    }

    return creditDetailsService.getFields( instance )

  }

  private void addAccountExtraData( Map map, String accountId,
      String extraDataKey, String key ) throws Exception {

    def dto = accountExtraDataService.findByAccountAndName(
        accountId, extraDataKey )
    map."${key}" = dto?.value

  }

  private Map getUserData( String accountId ) throws Exception {

    def userData = [:]
    addAccountExtraData( userData, accountId, 'userData_name', 'name' )
    return userData

  }

  private Map getDebitData( String accountId ) throws Exception {

    def debitData = [:]
    addAccountExtraData( debitData, accountId, 'clabe', 'clabe' )
    return debitData

  }

}
