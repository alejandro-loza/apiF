package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.exceptions.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TransactionsApiService {

  @Autowired
  RestTemplateService restTemplateService

  @Autowired
  MovementService movementService

  @Value( '${transactions-api.url}' )
  String url

  @Value( '${transactions-api.auth.username}' )
  String username

  @Value( '${transactions-api.auth.password}' )
  String password

  void findDuplicated( Movement movement ) throws Exception {

    if ( !movement || !movement.id) {
      throw new BadRequestException( 'transactionsApi.findDuplicated.movement.null' )
    }
    List mov  = movementService.getMovementsToDuplicated( movement.id )
    List f = prepareList( mov, movement )
    println "Movimiento entrante: \n${movement.date} \t ${movement.description}\n"
    println "Numero de posibles duplicados: " + ( f.size()-1 )  
    f.each{ 
      if( it != movement ){  println "${it.date} \t ${it.description}" }
    } 
    if ( f.size() >= 2 ){  
      Map params = [:]  
      params.endpoint = "searchAll"
      params.params = [ list: f.description.join(",") ]
      def restFind = find( params )

      def reasonResponse = restFind.results.findAll{ 
        ( it.reason.data != "Not found" ) || ( it.similarity.percent >= 80 )
      }
      if( reasonResponse ){
        println "\nreason" 
        reasonResponse.each{ println "${it.similarity} \t ${it.reason.data} \t ${it.description}" } 
      }
    }

  }

  private List prepareList( List mov, Movement mv ){

    def dateMinus = mv.date.minus(5)
    List list = mov.findAll{ it.date <= mv.date && it.date >= dateMinus }
    def nl = []
    nl << mv
    list.each{
      if( it != mv ){ nl << it }
    }
    nl

  }

  private Map find( Map map ) throws Exception {

    if ( !map ) {
      throw new BadImplementationException( 'transactionsApiService.find.map.null' )
    }

    def token = "${username}:${password}".bytes.encodeBase64().toString()
    def headers = [ 'Authorization': "Basic ${token}"]
    def params = map.params
    restTemplateService.get( url + map.endpoint, headers, params ).result

  }

}
