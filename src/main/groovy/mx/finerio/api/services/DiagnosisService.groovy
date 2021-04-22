package mx.finerio.api.services

import mx.finerio.api.dtos.DiagnosisDto

interface DiagnosisService {
    DiagnosisDto getDiagnosisByCustomer(Long customerId ) throws Exception
}