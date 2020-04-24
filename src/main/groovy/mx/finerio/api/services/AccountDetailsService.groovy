package mx.finerio.api.services

import mx.finerio.api.domain.CreditDetails

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountDetailsService {

  @Autowired
  CreditDetailsService creditDetailsService

  Map findAllByAccount( String accountId ) throws Exception {

    def instance = creditDetailsService.findByAccountId( accountId )

    if ( !instance ) {
      instance = new CreditDetails()
    }

    def map = creditDetailsService.getFields( instance )
    return map

  }

}
