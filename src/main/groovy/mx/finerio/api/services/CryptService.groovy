package mx.finerio.api.services

import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.Key

import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CryptService {

  def key
  private static final String TRANSFORMATION = 'AES/GCM/NoPadding'
  private static final int TAG_LENGTH = 16

  CryptService( @Value('${crypt.key}') String keyValue ) {
    key = new SecretKeySpec( keyValue.decodeHex(), 'AES' )
  }

  Map encrypt( String value ) throws Exception {

    if ( !value ) {
      throw new BadImplementationException(
          'cryptService.encrypt.value.null' )
    }
 
    def cipher = Cipher.getInstance( TRANSFORMATION )
    cipher.init( Cipher.ENCRYPT_MODE, key )
    [ message: cipher.doFinal( value.getBytes( 'UTF-8' ) )?.encodeHex().toString(),
        iv: cipher.getIV().encodeHex().toString() ]

  }

  String decrypt( String value, String iv ) throws Exception {

    if ( !value ) {
      throw new BadImplementationException(
          'cryptService.decrypt.value.null' )
    }

    if ( !iv ) {
      throw new BadImplementationException(
          'cryptService.decrypt.iv.null' )
    }

    def cipher = Cipher.getInstance( TRANSFORMATION )
    def spec = new GCMParameterSpec( TAG_LENGTH * 8, iv.decodeHex() )
    cipher.init( Cipher.DECRYPT_MODE, key, spec )
    new String( cipher.doFinal( value.decodeHex() ) )

  }

}
