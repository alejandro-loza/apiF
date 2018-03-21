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

  def search( String text ) throws Exception {

    if ( !text ) {
      throw new BadImplementationException( 'categorizerService.search.text.null' )
    }

    def token = "${username}:${password}".bytes.encodeBase64().toString()
    def headers = [ 'Authorization': "Basic ${token}"]
    def params = [ input: text ]
    restTemplateService.get( url, headers, params )

  }

}
