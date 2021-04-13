package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.beans.factory.InitializingBean
import mx.finerio.api.exceptions.BadImplementationException
import org.springframework.beans.factory.annotation.Autowired

@Service
class SatwsService {

  final static String DEFAULT_TYPE = "ciec"

  @Autowired
  SatwsClientService satwsClientService

  String createCredential( Map data ) throws Exception {
        
    data.type = DEFAULT_TYPE      
    satwsClientService.createCredential( data )

  }
}
