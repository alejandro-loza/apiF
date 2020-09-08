package mx.finerio.api.services

import mx.finerio.api.domain.BankField
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.BankFieldRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class BankFieldService {

  @Autowired
  FinancialInstitutionService financialInstitutionService

  @Autowired
  BankFieldRepository bankFieldRepository

  List<BankField> findAllByFinancialInstitution(
      Long financialInstitutionId ) throws Exception {

    def financialInstitution = financialInstitutionService.findOne(
        financialInstitutionId )
    return bankFieldRepository.
        findAllByProviderIdAndFinancialInstitutionAndInteractiveIsFalse(
            3, financialInstitution )

  }

  Map getFields( BankField bankField ) throws Exception {

    if ( !bankField ) {
      throw new BadImplementationException(
          'bankFieldService.getFields.bankField.null' )
    }

    [ name: bankField.name, friendlyName: bankField.friendlyName,
        position: bankField.position, type: bankField.type,
        required: bankField.required ]

  }

}

