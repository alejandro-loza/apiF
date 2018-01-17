package mx.finerio.api.services

import mx.finerio.api.domain.repository.*
import mx.finerio.api.domain.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class CleanerRestService {

  @Autowired
  ConfigService configService

  @Autowired
  RestTemplateService restTemplateService

//  @Value( '${categorizer.auth.username}' )
//  String username

//  @Value( '${categorizer.auth.password}' )
//  String password

  String clean( String text ) throws Exception {

    def url = configService.findByItem( Config.Item.CLEANER_URL  )
    def username = configService.findByItem( Config.Item.CLEANER_USERNAME )
    def password = configService.findByItem( Config.Item.CLEANER_PASSWORD )
    def token = "${username}:${password}".bytes.encodeBase64().toString()
    def finalUrl = "${url}/clean"
    def headers = [ 'Authorization': "Basic ${token}"]
    def params = [ input: text ]
    restTemplateService.get( finalUrl, headers, params ).result

  }


}
