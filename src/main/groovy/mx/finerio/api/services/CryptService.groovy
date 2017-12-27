package mx.finerio.api.services

import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.Key
import org.springframework.stereotype.Service

@Service
class CryptService {

  private Key key 
  private final String transformation = 'AES/GCM/NoPadding'
  private final int tagLength = 16

    def encrypt(String value) {
        def cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return [message: cipher.doFinal(value.getBytes('UTF-8'))?.encodeHex().toString(), iv: cipher.getIV().encodeHex().toString()]
    }

  String decrypt( String value, String iv ) throws Exception {

    if ( !value ) {
      throw new IllegalArgumentException( 'crypt.decrypt.value.blank' )
    }

    if ( !iv ) {
      throw new IllegalArgumentException( 'crypt.decrypt.iv.blank' )
    }

    def cipher = Cipher.getInstance( transformation )
    def spec = new GCMParameterSpec( tagLength * 8, iv.decodeHex() )
    cipher.init( Cipher.DECRYPT_MODE, key, spec )
    new String( cipher.doFinal( value.decodeHex() ) )

  }

    boolean updateKey(String key) {
        try {
            def newKey = new SecretKeySpec(key.decodeHex(), 'AES')
            this.key = newKey
        } catch (e) {
            log.error("Error updating key", e)
            return false
        }
    }

}
