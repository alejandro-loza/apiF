package mx.finerio.api.services

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

import mx.finerio.api.domain.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.*

import groovy.json.JsonSlurper

@Service
class CredentialService {

	@Autowired
	CryptService cryptService

	def createCredential() throws Exception{
		String loginBearerToken= this.loginCredential()
		if ( !loginBearerToken ) {
      throw new IllegalArgumentException( 'credential.create.loginBearerToken.blank' )
    }
		String data = this.initData()
		def credential = this.postCredential(loginBearerToken,data)
	}
	
	def loginCredential() {
		OkHttpClient client = new OkHttpClient().newBuilder()
		  .hostnameVerifier( new HostnameVerifier() {
		    boolean verify( String hostname, SSLSession session ) {
		      true
		    }
		  }).build()

		Request request = new Request.Builder()
		  .url("https://finerio-dev.southcentralus.cloudapp.azure.com/api/login")
		  .get()
		  .addHeader("authorization", "Basic c2NyYXBlcjpzY3JhcGVyXzIzNDUk")
		  .build();

		Response response = client.newCall(request).execute()
		ResponseBody responseBody= response.body()
  	Headers responseHeaders = response.headers()
    def x = responseBody.string()
    def json = new JsonSlurper().parseText(x)
  	json.authorizationToken
	}
	
	def postCredential(String token, String data) {
			OkHttpClient client = new OkHttpClient().newBuilder()
	  .hostnameVerifier( new HostnameVerifier() {
	    boolean verify( String hostname, SSLSession session ) {
	      true
	    }
	  }).build()

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, data);
		Request request = new Request.Builder()
		  .url("https://finerio-dev.southcentralus.cloudapp.azure.com/api/services/credentials")
		  .post(body)
		  .addHeader("content-type", "application/json")
		  .addHeader("authorization", "Bearer ${token}")
		  .build();

		Response response = client.newCall(request).execute();
		response.body().string()
	}
	
	String initData(){
		def credential = new Credential()
		credential.finantialInstitutionId = 7
		credential.status = "VALIDATE"

		def field1 = new Field()
		field1.name = 'username'
		field1.value = 'erick'
		field1.credential = credential

		def field2 = new Field()
		field2.name = 'password'
		field2.value = '1234567890'
		field2.credential = credential

		cryptService.updateKey("57D50663C76CA7CDC3B30ABCA20572A46690FD23F93247BAFFC1F402A001C6E4")
		def encryption = cryptService.encrypt( field2.value )
		def dataString="""
			{\"data\":[
				{\"id\":\"${credential.id}\",
				\"username\":\"${field1.value}\",
				\"password\":\"${encryption.message}\",
				\"iv\":\"${encryption.iv}\",
				\"user\":{\"id\":\"52987e73-4437-4634-998e-0df230c09da2\"},
				\"institution\":{\"id\":\"${credential.finantialInstitutionId}\"},
				\"securityCode\":\"\"}]}
		"""
		dataString
	}


}
