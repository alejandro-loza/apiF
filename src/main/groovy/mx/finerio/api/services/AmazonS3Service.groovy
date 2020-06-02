package mx.finerio.api.services

import mx.finerio.api.exceptions.*
import java.io.*
import java.nio.*
import java.nio.file.*
import java.util.Random;
import com.amazonaws.*
import com.amazonaws.auth.*
import com.amazonaws.regions.*
import com.amazonaws.services.s3.*
import com.amazonaws.services.s3.model.*
import com.amazonaws.services.s3.paginators.*
import java.util.ListIterator
import java.security.Key
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Autowired

@Service
class AmazonS3Service {

  final Regions clientRegion = Regions.DEFAULT_REGION

	@Value('${finerio.amazon.bucket}')
	String bucketName

	@Value('${finerio.amazon.key-id}')
	String keyId

	@Value('${finerio.amazon.key-secret}')
	String keySecret
  
  @Value('${admin.config.isProduction}') 
  final Boolean isProduction
	
  public static final String NAME_FOLDER = "pemsClient"

  @Autowired
  SecurityService securityService

	String getFile(){
    def client = securityService.getCurrent()
    def clientId = client?.id
    String path = "${bucketName}/${NAME_FOLDER}/${isProduction?'production':'sandbox'}/client-${clientId}/"
    String filename = "private.pem" 
    String directory = path.replaceAll( "${bucketName}/", "" )
    File folder = new File( directory )
    if( !folder.exists() ) { folder.mkdirs() }
    def tempFile = new File( "${directory}${filename}")
    if( tempFile.exists() ){ return "${directory}${filename}" }
    def s3 = getAmazonS3Client()
    try { 
      S3Object o = s3.getObject( bucketName, "${directory}${filename}");
      S3ObjectInputStream s3is = o.getObjectContent();
      FileOutputStream fos = new FileOutputStream( tempFile );
      byte[] read_buf = new byte[1024];
      int read_len = 0;
      while ((read_len = s3is.read(read_buf)) > 0) {
        fos.write(read_buf, 0, read_len);
      }
      s3is.close();
      fos.close();
    } catch (AmazonServiceException e) {
      throw new BadRequestException(
		    "amazonS3Service.getFile.AmazonServiceException.error: ${e.getMessage()}")
    } catch (FileNotFoundException e) {
			throw new BadRequestException(
		    "amazonS3Service.getFile.FileNotFoundException.error: ${e.getMessage()}")
    } catch (IOException e) {
		  throw new BadRequestException(
		    "amazonS3Service.getFile.IOException.error: ${e.getMessage()}")
    }
    "${directory}${filename}"
  }

  private AmazonS3 getAmazonS3Client(){

    AWSCredentials awsCredentials = new BasicAWSCredentials(
      keyId, keySecret );
    return AmazonS3ClientBuilder
      .standard()
      .withCredentials( new AWSStaticCredentialsProvider(awsCredentials) )
      .withRegion( Regions.US_EAST_2 )
      .build()

  }




}
