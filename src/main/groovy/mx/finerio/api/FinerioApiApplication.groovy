package mx.finerio.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.core.context.SecurityContextHolder

@SpringBootApplication
@EnableAsync
class FinerioApiApplication {

  static void main( String[] args ) {

  SecurityContextHolder.strategyName =
        SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
    SpringApplication.run( FinerioApiApplication, args )

  }

}
