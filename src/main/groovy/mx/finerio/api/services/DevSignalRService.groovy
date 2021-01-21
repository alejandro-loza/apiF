package mx.finerio.api.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PreDestroy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service
@Profile('dev')
class DevSignalRService implements SignalRService{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.DevSignalRService' )
    
  @Autowired
  CredentialService credentialService

  @Autowired
  ScraperService scraperService

  @Autowired
  public TransactionsReceiver(){
    log.info("Dummy Connection to signalr set.")   
  }
  
  @Override
  void sendTokenToScrapper( String token, String credentialId )  {

    def credential = credentialService.findAndValidate( credentialId )
    def data = [
      id: credentialId,
      username: credential.username,
      password: credential.password,
      iv: credential.iv,
      user: [ id: credential.user.id ],
      institution: [ id: 1 ],
      securityCode: credential.securityCode
    ]

    scraperService.requestData( data )

  }

}
    

