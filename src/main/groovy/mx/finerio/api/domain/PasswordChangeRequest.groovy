package mx.finerio.api.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

import groovy.transform.ToString

@Entity
@Table(name = 'password_change_request')
@ToString(includeNames = true, includePackage = false)
class PasswordChangeRequest {
	
	@Id @GeneratedValue
	@Column(name = 'id', updatable = false)
	Long id
	
	@Column(name = 'request_date', nullable = false)
	Date requestDate
	
	@Column(name = 'token', nullable = false)
	String token
	
	@Column(name = 'valid', nullable = false)
	Boolean valid
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = 'user_id', nullable = false)
	User user
}
