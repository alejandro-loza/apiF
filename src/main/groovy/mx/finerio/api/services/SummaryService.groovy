package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.dtos.SummaryBalanceDto
import mx.finerio.api.dtos.SummaryByCategoryDto
import mx.finerio.api.dtos.SummaryByMonthDto
import mx.finerio.api.dtos.SummaryBySubcategoryDto
import mx.finerio.api.dtos.SummaryDto
import mx.finerio.api.domain.repository.TransactionRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SummaryService {

  @Autowired
  CategoryService categoryService

  @Autowired
  CustomerService customerService

  @Autowired
  TransactionRepository transactionRepository

  SummaryDto getSummaryByCustomer( Long customerId ) throws Exception {

    customerService.findOne( customerId )
    def transactions = getTransactions( customerId )
    def categories = categoryService.findAll()
    def summaryDto = new SummaryDto()

    for ( transaction in transactions ) {

      def summaryDate = getSummaryDate( transaction.date )
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

  private List<ApiTransactionDto> getTransactions( Long customerId )
    throws Exception {

    def rawTransactions = transactionRepository.findAllByCustomerId(
      customerId )
    def transactions = []

    for ( transaction in rawTransactions ) {

      transactions << new ApiTransactionDto(
        id: transaction[ 0 ] as Long,
        description: transaction[ 1 ] as String,
        cleanedDescription: transaction[ 2 ] as String,
        amount: transaction[ 3 ] as BigDecimal,
        isCharge: transaction[ 4 ] as Boolean,
        date: transaction[ 5 ] as Date,
        categoryId: transaction[ 6 ] as String,
        duplicated: transaction[ 7 ] as Boolean,
        balance: transaction[ 8 ] as BigDecimal
      )

    }

    return transactions

  }

  private Date getSummaryDate( Date date ) throws Exception {

    def cal = Calendar.getInstance()
    cal.time = date
    def year = cal.get( Calendar.YEAR )
    def month = cal.get( Calendar.MONTH ) + 1
    return Date.parse( 'yyyy-MM',
        "${year}-${month.toString().padLeft( 2, '0' )}" )

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

  private String getCategoryId( List<Category> categories,
      String subcategoryId ) throws Exception {

    def category = categories.find { it.id == subcategoryId }

    if ( category != null ) {
      return category.parent.id
    }

    return null

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

