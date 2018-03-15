package mx.finerio.api.services

import java.security.MessageDigest

import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.stereotype.Service

@Service
class Sha1Service {

  byte[] encrypt( String input ) throws Exception {

    if ( !input ) {
      throw new BadImplementationException(
          'sha1Service.encrypt.input.null' )
    }
 
    def messageDigest = MessageDigest.getInstance( 'SHA-1' )
    messageDigest.update( input.getBytes( 'UTF-8' ),
        0, input.size() )
    messageDigest.digest()

  }

}
