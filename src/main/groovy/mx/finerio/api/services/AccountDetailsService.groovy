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

    def map = getCreditDetailsMap( accountId )
    addAccountExtraData( map, accountId, 'userData_name', 'name' )
    addAccountExtraData( map, accountId, 'clabe', 'clabe' )
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

}
