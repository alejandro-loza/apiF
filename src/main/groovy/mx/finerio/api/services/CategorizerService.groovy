package mx.finerio.api.services

import mx.finerio.api.domain.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class CategorizerService {

  @Autowired
  ConfigService configService

  @Autowired
  RestTemplateService restTemplateService

  @Value( '${categorizer.auth.username}' )
  String username

  @Value( '${categorizer.auth.password}' )
  String password

  Map search( String text ) throws Exception {

    def url = configService.findByItem( Config.Item.CATEGORIZER_SEARCH_URL )
    def token = "${username}:${password}".bytes.encodeBase64().toString()
    def headers = [ 'Authorization': "Basic ${token}"]
    def params = [ input: text ]
    restTemplateService.get( url, headers, params )

  }

}
