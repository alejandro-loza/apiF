@Grab( 'org.bouncycastle:bcpkix-jdk15on:1.57' )


import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import javax.crypto.Cipher
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import static java.nio.charset.StandardCharsets.UTF_8

def jsonString="{\"credentialId\":\"a701fd11-5617-41f5-bbd3-d409489c6809\",\"accountId\":\"aeaf7cf0-85d6-4feb-8949-b3f16bea5963\",\"transactions\":[]}"
def plainText = jsonString.getBytes( 'UTF-8' ).encodeBase64().toString()  
def signature = "ab5ziYd1t/BVX8uWe/+AK91am2eODdDUz+jOx4t8cCDIrGzovMAuPnmL0or0tXbbRbD6WNRYZPHoGn7/zBjABV7aDoyjXdR6u7dR649TIUgGYT91LI7iPGc84aTNFcISPEuiIbWEulhltgbWefPEpW6/dEFQ2zAGpIU2pizBWI9DWckPlEEJCIRVOtjyU/5W9JK+FBIa5NCyLxui5oLVri4C/Xl2OR19oN7m8F48FsYmnvXDssYSBfG+0/uklhGTp6sLscXlZvcOTf0iN9DOZ8mo7TKE3a2ciLcUGUQyJuK/66RPigtjddkcMlDTWh404euSJRPNHm/oPTq7jDdJhQ=="
def publicKey = getPublicKey()

println verify( plainText, signature, publicKey )


 static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
    Signature publicSignature = Signature.getInstance("SHA256withRSA");
    publicSignature.initVerify(publicKey);
    publicSignature.update(plainText.getBytes(UTF_8));

    byte[] signatureBytes = Base64.getDecoder().decode(signature);

    return publicSignature.verify(signatureBytes);
}

static PublicKey getPublicKey() throws Exception {
  
    def publicKeyBytes = new File( 'public.pem' ).bytes
    def pemParser = new PEMParser( new BufferedReader( new InputStreamReader(
        new ByteArrayInputStream( publicKeyBytes ) ) ) )
    new JcaPEMKeyConverter().getPublicKey( pemParser.readObject() )
 
  }