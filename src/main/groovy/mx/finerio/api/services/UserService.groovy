package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class UserService {

  @Value('${api.userId}')
  String apiUserId

  @Autowired
  UserRepository userRepository


  User findById( String id ){
    userRepository.findById( id )
  }

  User getApiUser() {
    userRepository.findById( apiUserId )
  }


}
