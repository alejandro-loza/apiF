package mx.finerio.api.services



import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import java.security.Security
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.exceptions.BadRequestException
import org.springframework.stereotype.Service
import org.springframework.beans.factory.InitializingBean

@Service
class RsaCryptScraperV2Service implements InitializingBean {

  @Autowired
  ScraperV2ClientService scraperV2ClientService

  byte[] keyBytes
  PrivateKey privateKey
  
  String encrypt( String text ) throws Exception {
          
      if ( !text ) {
        throw new BadRequestException(
            'rsaCryptServiceScraperV2.encrypt.text.null' )
      }
  
      def publicKey = getPublicKey()  
      def cipher = Cipher.getInstance( 'RSA/NONE/OAEPWithSHA256AndMGF1Padding','BC' )
      cipher.init( Cipher.ENCRYPT_MODE, publicKey )
      cipher.doFinal( text.getBytes( 'UTF-8' ) ).encodeBase64().toString()
  
  }

  private PublicKey getPublicKey() throws Exception {
  
    if( this.keyBytes == null) {
      def data = scraperV2ClientService.getPublicKey()
      String keyString = data.public_key
      this.keyBytes = keyString.decodeBase64()        
    }  
           
    X509EncodedKeySpec spec = new X509EncodedKeySpec( this.keyBytes ) 
    KeyFactory kf = KeyFactory.getInstance("RSA") 
    kf.generatePublic(spec)
   
  }

  @Override
  public void afterPropertiesSet() throws Exception {
      Security.addProvider(new BouncyCastleProvider())
  }

}
