package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CategorizerService {

  @Autowired
  RestTemplateService restTemplateService

  @Value( '${categorizer.url}' )
  String url

  @Value( '${categorizer.auth.username}' )
  String username

  @Value( '${categorizer.auth.password}' )
  String password

  def search( String text, Boolean income ) throws Exception {

    if ( !text ) {
      throw new BadImplementationException( 'categorizerService.search.text.null' )
    }
    if (income == null){
      throw new BadImplementationException( 'categorizerService.search.income.null' )
    }

    def token = "${username}:${password}".bytes.encodeBase64().toString()
    def headers = [ 'Authorization': "Basic ${token}"]
    def params = [ input: text, clean: 'false' ]
    if(income){ params.income = "true" }
    def out = [:] 
    try{
      out = restTemplateService.get( url, headers, params )
    }catch( Exception e){ }
    out

  }

}
