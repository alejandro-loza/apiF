package mx.finerio.api.services

import javax.validation.Valid
import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.dtos.*
import mx.finerio.api.exceptions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Service
class BcryptService {

  String encode( String pass ){
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
    return encoder.encode( pass )
  }

}
