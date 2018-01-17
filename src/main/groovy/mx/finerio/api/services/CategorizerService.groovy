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
  RestTemplateService restTemplateService

  @Autowired
  CategoryRepository categoryRepository

//  @Value( '${categorizer.auth.username}' )
  String username

//  @Value( '${categorizer.auth.password}' )
  String password

  Category search( String text ) throws Exception {

    def url = configService.findByItem( Config.Item.CATEGORIZER_SEARCH_URL  )
    def token = "${username}:${password}".getBytes().encodeBase64().toString()
    Map map = [:]
    map.url= [ port: url, service: "" ]
    map.param = [ name: "?input=", value: text ]
    map.auth = [ status: true, type: "Basic", token: token ]
    def result = restTemplateService.get(map)
//    categoryRepository.findById( result.categoryId )
    def cat = new Category() 
    cat.version = 5
    cat.color = '#fb8c00'
    cat.keywords = null
    cat.name = 'Mudanzas'
    cat.textColor = '#ffffff'
    cat.user = null
    cat.parent = null
    cat.activityCodes = null
    cat.orderIndex =  null
    cat
  }


}
