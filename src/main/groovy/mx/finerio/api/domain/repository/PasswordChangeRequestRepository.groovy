package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.PasswordChangeRequest
import mx.finerio.api.domain.User

interface PasswordChangeRequestRepository extends JpaRepository<PasswordChangeRequest, String> {
	List<PasswordChangeRequest> findByUserAndValidTrue( User user )
	PasswordChangeRequest findOneByTokenAndValidTrue( String token )
}
