package mx.finerio.api.services

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

import okhttp3.*

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class Okhttp3Service {

  def get( Map params ) throws Exception {

    try {
      def finalUrl = "${params.url.port}${params.url.service}?${params.param.name}=${params.param.value}"
      def client = new OkHttpClient().newBuilder()
        .hostnameVerifier( new HostnameVerifier() {
          boolean verify( String hostname, SSLSession session ) {
            true
          }
        }).build()
      Request request 
      if(params.auth.status){
        request = new Request.Builder()
	  .url(finalUrl)
	  .get()
	  .addHeader("authorization", "${params.auth.type} ${params.auth.token}")
  	  .build();
      }else{
        request = new Request.Builder()
	  .url(finalUrl)
	  .get()
  	  .build();
      }
      def response = client.newCall(request).execute();
      def responseBody = response.body().string()
      def clean = new JsonSlurper().parseText( responseBody ?: '{}' )
      clean	
    } catch ( Exception e ) {
      def message = e.response ?
          "${e.response.statusCode} - ${new String(e.response.data)}" :
          e.message
    }
  }


}
