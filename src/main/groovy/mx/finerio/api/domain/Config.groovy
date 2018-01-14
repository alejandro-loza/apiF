package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString

@Entity
@Table(name = 'config')
@ToString(includes = 'id', includeNames = true, includePackage = false)
public class Config{

    enum Item {
        PASSWORD_RECOVERY_BASE_URL,
        NO_REPLY_SENDER,
        SPECIAL_NOTIFICATION_ENABLED,
        BW_SERVICE_ENDPOINT,
        MAILCHIMP_API_KEY,
        MAILCHIMP_API_DOMAIN,
        MAILCHIMP_MAIN_LIST,
        MAILCHIMP_BLOG_LIST,
        SPECTRE_CUSTOMER_CREATE_URL,
        SPECTRE_PROVIDER_LIST_URL,
        SPECTRE_LOGIN_CREATE_URL,
        SPECTRE_LOGIN_RECONNECT_URL,
        SPECTRE_LOGIN_INTERACTIVE_URL,
        SPECTRE_LOGIN_DESTROY_URL,
        SPECTRE_ACCOUNT_LIST_URL,
        SPECTRE_ACCOUNT_EXTRA_LIST_URL,
        SPECTRE_TRANSACTION_LIST_URL,
        CATEGORIZER_SEARCH_URL,
        CRYPT_KEY,
        MIXPANEL_SERVICE_ID,
        SPECTRE_HEADER_NAME,
        SPECTRE_HEADER_VALUE,
        BUDGETS_URL,
        BUDGETS_USERNAME,
        BUDGETS_PASSWORD,
        CLEANER_URL,
        CLEANER_USERNAME,
        CLEANER_PASSWORD,
        EMAIL_SERVICE_URL,
        SURA_EMAIL_TEMPLATE,
        SURA_EMAIL_RECIPIENT
    }

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(strategy=GenerationType.AUTO)
  Long id

  @Column(name = 'version', nullable = false)
  Long version

  @Enumerated(EnumType.STRING)
  @Column(name = 'item', nullable = false, unique=true)
  Item item

  @Column(name = 'description', nullable = false)
  String description

  @Column(name = 'value', nullable = false)
  String value

}
