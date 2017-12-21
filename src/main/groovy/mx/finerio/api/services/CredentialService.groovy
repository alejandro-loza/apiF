package mx.finerio.api.services

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

import mx.finerio.api.domain.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.*

@Service
class CredentialService {

	
	
	def loginCredential() {
	/*		
				OkHttpClient client = new OkHttpClient()
				Request request = new Request.Builder()
			  .url("https://finerio-dev.southcentralus.cloudapp.azure.com/api/login")
			  .get()
			  ​def s = 'scraper:scraper_2345$' 
				String encoded = s.bytes.encodeBase64().toString()
println "\n"
println encoded
			  .addHeader("authorization", "Basic ${encoded}")
			  //.addHeader("authorization", "Bearer ${token}")
			  .build();
	  		Response response = client.newCall(request).execute()
*/
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

Response response = client.newCall(request).execute();
	}
	/*
	def postCredential(String token) throws IOException{
		OkHttpClient client = new OkHttpClient()
		MediaType mediaType = MediaType.parse("application/json");
		String object = " {\"data\":[{\"id\":\"52987e73-4437-4634-998e-0df230c09da2\",\"username\":\"\",\"password\":\"\",\"iv\":\"\",\"user\":{\"id\":\"52987e73-4437-4634-998e-0df230c09da2\"},\"credential\":{\"id\":\"07\"},\"securityCode\":\"\"}]}"
		RequestBody body = RequestBody.create(mediaType, object);
		Request request = new Request.Builder()
		  .url("https://finerio-dev.southcentralus.cloudapp.azure.com/api/services/credentials")
		  .post(body)
		  .addHeader("content-type", "application/json")
		  .addHeader("authorization", "Bearer ${token}")
		  .build();

		Response response = client.newCall(request).execute();
		response.body().string()
	}
	*/
	/*
	def initData(){
		def credential = new Credential()
		credential.finantialInstitution = 7
		credential.status = VALIDATE

		def field1 = new Field()
		field1.name = 'username'
		field1.value = 'erick'
		field1.credential = credential

		def field2 = new Field()
		field2.name = 'password'
		field2.value = '1234567890'
		field2.credential = credential

		def encryption = encrypt( field2.value )
		def data = [:]
		data.id = credential.id
		data."${field1.name}" = field1.value
		data."${field2.name}" = encryption.message
		data.iv ? encryption.iv

		data.user = [ id: user.id ]
		data.institution = [ id: credential.finantialInstitution ]
		data.securityCode = ''	
	}
	*/

}
/*

  	ResponseBody responseBody= response.body()
  	Headers responseHeaders = response.headers()
    //responseHeaders.each{println "${it}}"}
    def x = responseBody.string()
  	def slurper = new JsonSlurper()
 		def result = slurper.parseText(x)
 		
 		["responseBody":result,"response":response]

*/
