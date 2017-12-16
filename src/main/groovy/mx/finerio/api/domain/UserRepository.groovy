package mx.finerio.api.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface UserRepository extends JpaRepository<User, Long>,
    JpaSpecificationExecutor {
  User findOneByUsername( String username )
}
