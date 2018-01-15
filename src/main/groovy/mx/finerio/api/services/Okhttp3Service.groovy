package mx.finerio.api.services

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

import okhttp3.*

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class Okhttp3Service {

  private static final MediaType JSON =
      MediaType.parse( 'application/json; charset=utf-8' )

  def post( Map params ) throws Exception {
	
    def finalUrl =  "${params.url.port}/${params.url.service}"
    def client = getClient()
    def json = new JsonBuilder( params.param.body ).toString()
    def body = RequestBody.create( JSON, json )
    def request 
    if(params.auth.status){
        request = new Request.Builder()
          .url(finalUrl)
          .addHeader("authorization", "${params.auth.type} ${params.auth.token}")
          .post( body )
          .build();
    }else{
        request = new Request.Builder()
          .url(finalUrl)
          .post( body )
          .build();
    }
    def response = client.newCall( request ).execute()
    def responseBody = response.body().string()
    def result = new JsonSlurper().parseText( responseBody ?: '{}' )
    result	

  }

  def get( Map params ) throws Exception {
    try {
      def finalUrl = "${params.url.port}${params.url.service}${params.param.name}${params.param.value}"
      def client = getClient()
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
      def result = new JsonSlurper().parseText( responseBody ?: '{}' )
      result	
    } catch ( Exception e ) {
      def message = e.response ?
          "${e.response.statusCode} - ${new String(e.response.data)}" :
          e.message
    }
  }

  private OkHttpClient getClient() throws Exception {

    new OkHttpClient().newBuilder()
      .hostnameVerifier( new HostnameVerifier() {
        boolean verify( String hostname, SSLSession session ) {
          true
        }
      }).build()

  }

}
