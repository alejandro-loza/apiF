package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository

import mx.finerio.api.domain.BankConnection

interface BankConnectionRepository extends JpaRepository<BankConnection, Long> {}
