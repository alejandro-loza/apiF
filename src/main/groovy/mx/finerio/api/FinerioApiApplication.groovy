package mx.finerio.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class FinerioApiApplication {

  static void main( String[] args ) {
    SpringApplication.run( FinerioApiApplication, args )
  }

}
