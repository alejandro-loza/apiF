package mx.finerio.api.services

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile('prod')
class ProdScraperService extends ScraperApiService {
}
