package mx.finerio.api.services

import com.fasterxml.jackson.databind.ObjectMapper

import org.springframework.stereotype.Service

@Service
class CallbackRestService {

  Integer post( String url, Object body ) throws Exception {

    def post = new URL( url ).openConnection()
    post.requestMethod = 'POST'
    post.doOutput = true
    post.setRequestProperty( 'Content-Type', 'application/json' )

    def objectMapper = new ObjectMapper()
    def message = objectMapper.writeValueAsString( body )
    post.outputStream.write( message.getBytes( 'UTF-8' ) ) 
    return post.responseCode

  }
  
}
