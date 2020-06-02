package mx.finerio.api.services

import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

import javax.crypto.Cipher

import mx.finerio.api.exceptions.BadImplementationException

import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service
class RsaCryptService {

  @Autowired
  AmazonS3Service amazonS3Service

  String encrypt( String text ) throws Exception {

    if ( !text ) {
      throw new BadImplementationException(
          'rsaCryptService.encrypt.text.null' )
    }

    def publicKey = getPublicKey()
    def cipher = Cipher.getInstance( 'RSA' )
    cipher.init( Cipher.ENCRYPT_MODE, publicKey )
    cipher.doFinal( text.getBytes( 'UTF-8' ) ).encodeBase64().toString()

  }

  String decrypt( String cryptedText ) throws Exception {

    if ( !cryptedText ) {
      throw new BadImplementationException(
          'rsaCryptService.decrypt.cryptedText.null' )
    }

    def privateKey = getPrivateKey()
    def cipher = Cipher.getInstance( 'RSA' )
    String finalResp 
    try{
      cipher.init( Cipher.DECRYPT_MODE, privateKey )
      finalResp = new String( cipher.doFinal( cryptedText.decodeBase64() ), 'UTF-8' )
    }catch(Exception e){
      def pathFile = amazonS3Service.getFile()
      privateKey = getPrivateKey( pathFile )
      cipher.init( Cipher.DECRYPT_MODE, privateKey )
      finalResp = new String( cipher.doFinal( cryptedText.decodeBase64() ), 'UTF-8' )
    }
    finalResp
  
  }

  private PublicKey getPublicKey() throws Exception {
  
    def publicKeyBytes = new File( 'pem/public.pem' ).bytes
    def pemParser = new PEMParser( new BufferedReader( new InputStreamReader(
        new ByteArrayInputStream( publicKeyBytes ) ) ) )
    new JcaPEMKeyConverter().getPublicKey( pemParser.readObject() )
 
  }

  private PrivateKey getPrivateKey() throws Exception {

    def privateKeyBytes = new File( 'pem/private.pem' ).bytes
    def pemParser = new PEMParser( new BufferedReader( new InputStreamReader(
        new ByteArrayInputStream( privateKeyBytes ) ) ) )
    def pemKeyPair = (pemParser.readObject()) as PEMKeyPair
    new JcaPEMKeyConverter().getPrivateKey( pemKeyPair.privateKeyInfo )

  }
  
  private PrivateKey getPrivateKey( String filePath ) throws Exception {

   def privateKeyBytes = new File( filePath ).bytes
   def pemParser = new PEMParser( new BufferedReader( new InputStreamReader(
       new ByteArrayInputStream( privateKeyBytes ) ) ) )
   def privateKI = pemParser.readObject()
   new JcaPEMKeyConverter().getPrivateKey( privateKI )

 }


  
}
