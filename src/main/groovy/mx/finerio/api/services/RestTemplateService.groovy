package mx.finerio.api.services

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

import okhttp3.*

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RestTemplateService {

  private static final MediaType JSON =
      MediaType.parse( 'application/json; charset=utf-8' )

  def post( String url, Map headers, Map data ) throws Exception {
	
    def client = getClient()
    def body = RequestBody.create( JSON, new JsonBuilder( data ).toString() )
    def builder = new Request.Builder()
        .url( url )
        .post( body )

    headers.each {
       builder.addHeader( it.key.toString(), it.value.toString() )
    }

    def response = client.newCall( builder.build() ).execute()
    def responseBody = response.body().string()
    new JsonSlurper().parseText( responseBody ?: '{}' )

  }

  def get( String url, Map headers, Map params ) throws Exception {

    def urlBuilder = HttpUrl.parse( url ).newBuilder()

    params.each {
      urlBuilder.addQueryParameter( it.key, it.value )
    }

    def client = getClient()
    def builder = new Request.Builder()
        .url( urlBuilder.build().toString() )
        .get()

    headers.each {
       builder.addHeader( it.key.toString(), it.value.toString() )
    }

    def response = client.newCall( builder.build() ).execute()
    def responseBody = response.body().string()
    new JsonSlurper().parseText( responseBody ?: '{}' )

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
