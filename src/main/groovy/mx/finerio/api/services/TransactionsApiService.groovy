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

  final Map percent =[
    BBVA: [ percent: 81, date: 5 ], 
    BNMX: [ percent: 81, date: 5 ],
    AOOL: [ percent: 81, date: 5 ],
    SANTANDER: [ percent: 81, date: 5 ],
    HSBC: [ percent: 81, date: 5 ],
    AMEX: [ percent: 81, date: 5 ],
    INVEX: [ percent: 81, date: 5 ],
    SCOTIA: [ percent: 81, date: 5 ],
    BANORTE: [ percent: 81, date: 5 ],
    INBURSA: [ percent: 81, date: 5 ]
  ]

  List findTransference( Map map ) throws Exception {
    
    if ( !map ) {
      throw new BadRequestException( 'transactionsApi.findTransference.map.null' )
    }
    if ( !map.list ) {
      throw new BadRequestException( 'transactionsApi.findTransference.list.null' )
    }
    if ( !map.type ) {
      throw new BadRequestException( 'transactionsApi.findTransference.type.null' )
    }
    if ( !map.bank ) {
      throw new BadRequestException( 'transactionsApi.findTransference.bank.null' )
    }
    Map params = [:]  
    params.endpoint = "searchTransferences"
    params.params = [ list: map.list.join(","), type: map.type.toString(), bank: map.bank ]
    def restFind = find( params )
    return restFind.results ?: []

  }

  Movement findDuplicated( Movement movement ) throws Exception {

    if ( !movement || !movement.id) {
      throw new BadRequestException( 'transactionsApi.findDuplicated.movement.null' )
    }
    List mov  = movementService.getMovementsToDuplicated( movement )
    List f = prepareList( mov, movement )
    if ( f.size() >= 2 ){  
      Map params = [:]  
      params.endpoint = "searchAll"
      params.params = [ list: f.description.join(",") ]
      def restFind = find( params )
      def reasonResponse = restFind.results.findAll{ 
        ( it.reason.data != "Not found" ) || ( it.similarity.percent >= percent["${movement.account.institution.code}"].percent )
      }
      if( reasonResponse ){
        movementService.updateDuplicated( movement )
      }
    }
    movement

  }

  private List prepareList( List mov, Movement mv ){

    if ( mov.size() == 1 ) { return [] }
    def dateMinus = mv.date.minus( percent["${mv.account.institution.code}"].date )
    List list = mov.findAll{ it.date <= mv.date && it.date >= dateMinus }
    def nl = []
    nl << mv
    list.each{
      if( it.id != mv.id ){ nl << it }
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
