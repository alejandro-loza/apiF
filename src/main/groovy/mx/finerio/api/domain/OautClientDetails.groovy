package mx.finerio.api .domain

import javax.persistence.*
import javax.validation.constraints.*
import groovy.transform.ToString

@Entity
@Table(name = "oauth_client_details")
@ToString(includePackage = false, includeNames = true, excludes = ['clientSecret'])
class OauthClientDetails {

  @Id
  @Column(name = "client_id", nullable = false)
  String clientId

  @Column(name = "client_secret", nullable = false)
  String clientSecret

  @Column(name = "access_token_validity", nullable = false)
  Long accessTokenValidity

  @Column(name = "refresh_token_validity", nullable = false)
  Long refreshTokenValidity

  @Column(name = "scope", nullable = false)
  String scope

  @Column(name = "authorized_grant_types", nullable = false)
  String grantTypes

  @Column(name = "resource_ids", nullable = false)
  String resourceIds

  @Column(name = "web_server_redirect_uri", nullable = true)
  String webServerRedirectUri

  @Column(name = "authorities", nullable = true)
  String authorities

  @Column(name = "additional_information", nullable = true)
  String additionalInfo

  @Column(name = "autoapprove", nullable = true)
  String autoApprove

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'date_deleted', nullable = true)
  Date dateDeleted

}
