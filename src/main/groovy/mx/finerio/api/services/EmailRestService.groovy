package mx.finerio.api.services;

import mx.finerio.api.dtos.email.EmailSendDto

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

  String send( EmailSendDto dto ) throws Exception {

    def fromData = dto.from ?
        [ email: dto.from.email, name: dto.from.name ] : null
    def client = new RESTClient( url )
    client.authorization = new HTTPBasicAuthorization( username, password )
    def data = [
      from: fromData,
      to: dto.to,
      template: [
        name: dto.template.name,
        params: dto.template.params
      ]
    ]

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
