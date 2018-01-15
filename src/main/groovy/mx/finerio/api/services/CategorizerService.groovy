package mx.finerio.api.services

import mx.finerio.api.domain.repository.*
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
  Okhttp3Service okhttp3Service

  @Autowired
  CategoryRepository categoryRepository

  @Value( '${categorizer.auth.username}' )
  String username

  @Value( '${categorizer.auth.password}' )
  String password

  Category search( String text ) throws Exception {

    def url = configService.findByItem( Config.Item.CATEGORIZER_SEARCH_URL  )
    def token = "${username}:${password}".getBytes().encodeBase64().toString()
    Map map = [:]
    map.url= [ port: url, service: "" ]
    map.param = [ name: "?input=", value: text ]
    map.auth = [ status: true, type: "Basic", token: token ]
    def result = okhttp3Service.get(map)
    result	
    //categoryRepository.findById( result.categoryId )

  }


}
