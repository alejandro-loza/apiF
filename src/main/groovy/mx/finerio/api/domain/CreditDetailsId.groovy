package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.GenericGenerator
import java.io.Serializable;


@Embeddable
class CreditDetailsId implements Serializable {

  @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'account_id', nullable = false)
  Account account

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CreditDetailsId)) return false;
    CreditDetailsId that = (CreditDetailsId) o;
    return Objects.equals( id, that.id ) &&
            Objects.equals( account, that.account );
  }

  @Override
  public int hashCode() {
      return Objects.hash( id, account );
  }

}
