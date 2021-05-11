package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.dtos.SummaryBalanceDto
import mx.finerio.api.dtos.SummaryByCategoryDto
import mx.finerio.api.dtos.SummaryByMonthDto
import mx.finerio.api.dtos.SummaryBySubcategoryDto
import mx.finerio.api.dtos.SummaryDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SummaryService extends InsightsService {

  @Autowired
  CategoryService categoryService

  @Autowired
  CustomerService customerService

  @Autowired
  SecurityService securityService

  SummaryDto getSummaryByCustomer( Long customerId ) throws Exception {

    securityService.validateInsightsEnabled()

    customerService.findOne( customerId )
    def transactions = getTransactions( customerId )
    def categories = categoryService.findAll()
    def summaryDto = new SummaryDto()

    for ( transaction in transactions ) {

      def summaryDate = getMainDate( transaction.date )
      def summaryByMonthDto = processSummaryByMonth( summaryDto,
          summaryDate, transaction.isCharge, transaction.amount )
      def categoryId = getCategoryId( categories, transaction.categoryId )
      def summaryByCategoryDto = processSummaryByCategory(
          summaryByMonthDto, categoryId, transaction.amount )
      processSummaryBySubcategory( summaryByCategoryDto,
          transaction.categoryId, transaction )

    }

    processBalance( summaryDto, summaryDto.expenses, 'expenses' )
    processBalance( summaryDto, summaryDto.incomes, 'incomes' )
    return summaryDto

  }

  private SummaryByMonthDto processSummaryByMonth( SummaryDto summaryDto,
      Date summaryDate, Boolean charge, BigDecimal amount )
      throws Exception {

    def summaryByMonthDto = null

    if ( charge ) {
      summaryByMonthDto = summaryDto.expenses.find { it.date == summaryDate }
    } else {
      summaryByMonthDto = summaryDto.incomes.find { it.date == summaryDate }
    }

    if ( summaryByMonthDto == null ) {
      summaryByMonthDto = new SummaryByMonthDto()
      summaryByMonthDto.date = summaryDate
      charge ? summaryDto.expenses << summaryByMonthDto :
          summaryDto.incomes << summaryByMonthDto
    }

    summaryByMonthDto.amount += amount
    return summaryByMonthDto

  }

  private SummaryByCategoryDto processSummaryByCategory(
      SummaryByMonthDto summaryByMonthDto, String categoryId,
      BigDecimal amount ) throws Exception {

    def summaryByCategoryDto = summaryByMonthDto.categories.find {
        it.categoryId == categoryId }

    if ( summaryByCategoryDto == null ) {
      summaryByCategoryDto = new SummaryByCategoryDto()
      summaryByCategoryDto.categoryId = categoryId
      summaryByMonthDto.categories << summaryByCategoryDto
    }

    summaryByCategoryDto.amount += amount
    return summaryByCategoryDto

  }

  private void processSummaryBySubcategory(
      SummaryByCategoryDto summaryByCategoryDto, String subcategoryId,
      ApiTransactionDto transactionDto ) throws Exception {

    def summaryBySubcategoryDto = summaryByCategoryDto.subcategories.find {
        it.categoryId == subcategoryId }

    if ( summaryBySubcategoryDto == null ) {
      summaryBySubcategoryDto = new SummaryBySubcategoryDto()
      summaryBySubcategoryDto.categoryId = subcategoryId
      summaryByCategoryDto.subcategories << summaryBySubcategoryDto
    }

    summaryBySubcategoryDto.amount += transactionDto.amount
    summaryBySubcategoryDto.transactions << transactionDto

  }

  private void processBalance( SummaryDto summaryDto,
      List<SummaryByMonthDto> summaryByMonthDtoList,
      String type ) throws Exception {

    for ( summaryByMonthDto in summaryByMonthDtoList ) {

      def summaryBalanceDto = summaryDto.balances.find {
          it.date == summaryByMonthDto.date }

      if ( summaryBalanceDto == null ) {
        summaryBalanceDto = new SummaryBalanceDto()
        summaryBalanceDto.date = summaryByMonthDto.date
        summaryDto.balances << summaryBalanceDto
      }

      summaryBalanceDto."${type}" += summaryByMonthDto.amount

    }

  }

}

