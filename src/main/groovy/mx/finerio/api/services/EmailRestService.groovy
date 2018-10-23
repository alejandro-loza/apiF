package mx.finerio.api.services;

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service;
import wslite.rest.RESTClient;
import wslite.http.auth.*

@Service
class EmailRestService {

  @Value('${finerio.email.api.url}')
  String url

  @Value('${finerio.email.api.username}')
  String username

  @Value('${finerio.email.api.password}')
  String password

  String send( String email, String template, Map params ) throws Exception {

    def client = new RESTClient( url )
    client.authorization = new HTTPBasicAuthorization( username, password )
    def data = [ to: [ email ], template:[ name: template, params: params ] ]

    try {

      def response = client.post( path: '/send' ) {
        json data
      }
      return new String( response.data ?: ''.bytes, 'UTF8' )

    } catch ( Exception e ) {

      def message = e.response ?
          "${e.response.statusCode} - ${new String(e.response.data)}" :
          e.message
      throw new IllegalArgumentException( message )

    }

  }

}