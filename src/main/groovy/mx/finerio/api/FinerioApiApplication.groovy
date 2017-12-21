package mx.finerio.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer

@SpringBootApplication
@EnableResourceServer
class FinerioApiApplication {

  static void main( String[] args ) {
    SpringApplication.run( FinerioApiApplication, args )
  }

}
