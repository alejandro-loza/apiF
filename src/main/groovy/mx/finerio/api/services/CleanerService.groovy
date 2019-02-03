package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CleanerService {

  @Autowired
  RestTemplateService restTemplateService

  @Value( '${cleaner.url}' )
  String url

  @Value( '${cleaner.auth.username}' )
  String username

  @Value( '${cleaner.auth.password}' )
  String password

  String clean( String text, Boolean income ) throws Exception {

    if ( !text ) {
      throw new BadImplementationException( 'cleanerService.clean.text.null' )
    }
    if (income == null){
      throw new BadImplementationException( 'cleanerService.clean.income.null' )
    }

    def token = "${username}:${password}".bytes.encodeBase64().toString()
    def headers = [ 'Authorization': "Basic ${token}"]
    def params = [ input: text ]
    if(income){ params.income = "true" }
    def out = text
    try{
      out = restTemplateService.get( url, headers, params ).result
      out = out ?: text
    }catch( Exception e){ }
    out

  }

}
