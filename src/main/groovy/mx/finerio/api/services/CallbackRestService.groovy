package mx.finerio.api.services

import com.fasterxml.jackson.databind.ObjectMapper

import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

import mx.finerio.api.dtos.MtlsDto

import org.springframework.stereotype.Service

@Service
class CallbackRestService {

  Integer post( String url, Object body, Map headers = null )
      throws Exception {

    def post = new URL( url ).openConnection()
    post.requestMethod = 'POST'
    post.doOutput = true
    post.setRequestProperty( 'Content-Type', 'application/json' )

    if( headers ) {
      headers.each { entry ->
        post.setRequestProperty( entry.key, entry.value )
      }
    }

    def objectMapper = new ObjectMapper()
    def message = objectMapper.writeValueAsString( body )
    post.outputStream.write( message.getBytes( 'UTF-8' ) )
    return post.responseCode

  }

  Integer post( String url, Object body, Map headers, MtlsDto mtlsDto )
      throws Exception {

    def post = new URL( url ).openConnection()
    post.requestMethod = 'POST'
    post.doOutput = true
    post.setRequestProperty( 'Content-Type', 'application/json' )

    if( headers != null ) {
      headers.each { entry ->
        post.setRequestProperty( entry.key, entry.value )
      }
    }

    post.setSSLSocketFactory(
        getFactory( new File( mtlsDto.filename ), mtlsDto.secret ) )

    def objectMapper = new ObjectMapper()
    def message = objectMapper.writeValueAsString( body )
    post.outputStream.write( message.getBytes( 'UTF-8' ) )
    return post.responseCode

  }
  
  private SSLSocketFactory getFactory( File pKeyFile, String pKeyPassword )
      throws Exception {

    def keyManagerFactory = KeyManagerFactory.getInstance( 'SunX509' )
    def keyStore = KeyStore.getInstance( 'PKCS12' )
    def keyInput = new FileInputStream( pKeyFile )
    keyStore.load( keyInput, pKeyPassword.toCharArray() )
    keyInput.close()
    keyManagerFactory.init( keyStore, pKeyPassword.toCharArray() )
    def context = SSLContext.getInstance( 'TLS' )
    context.init( keyManagerFactory.getKeyManagers(), null,
        new SecureRandom() )
    return context.getSocketFactory()

  }

}
