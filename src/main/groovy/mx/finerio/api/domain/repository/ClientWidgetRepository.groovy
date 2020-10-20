package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface ClientWidgetRepository extends JpaRepository<ClientWidget, Long>, JpaSpecificationExecutor { 

ClientWidget findByWidgetId( Long widgetId )  
}
