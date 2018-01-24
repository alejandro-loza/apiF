package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class UserService {

  @Autowired
  UserRepository userRepository


    User findById( String id ){
      userRepository.findById( id )
    }


}
