package mx.finerio.api.services

import mx.finerio.api.dtos.AnalysisByCategoryDto
import mx.finerio.api.dtos.AnalysisByMonthDto
import mx.finerio.api.dtos.AnalysisBySubcategoryDto
import mx.finerio.api.dtos.AnalysisByTransactionDto
import mx.finerio.api.dtos.AnalysisDto
import mx.finerio.api.dtos.ApiTransactionDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AnalysisService extends InsightsService {

  @Autowired
  CategoryService categoryService

  @Autowired
  CustomerService customerService

  @Autowired
  SecurityService securityService

  AnalysisDto getAnalysisByCustomer( Long customerId ) throws Exception {

    securityService.validateInsightsEnabled()

    customerService.findOne( customerId )
    def transactions = getTransactions( customerId )
    def categories = categoryService.findAll()
    def analysisDto = new AnalysisDto()

    for ( transaction in transactions ) {

      if ( !transaction.isCharge ) { continue }
      if ( transaction.categoryId == null ) { continue }
      def analysisDate = getMainDate( transaction.date )
      def analysisByMonthDto = processAnalysisByMonth( analysisDto,
          analysisDate, transaction.amount )
      def categoryId = getCategoryId( categories, transaction.categoryId )
      def analysisByCategoryDto = processAnalysisByCategory(
          analysisByMonthDto, categoryId, transaction.amount )
      def analysisBySubcategoryDto = processAnalysisBySubcategory(
          analysisByCategoryDto, transaction.categoryId, transaction )
      processAnalysisByTransaction( analysisBySubcategoryDto, transaction )

    }

    return analysisDto

  }

  private AnalysisByMonthDto processAnalysisByMonth(
      AnalysisDto analysisDto, Date date, BigDecimal amount )
      throws Exception {

    def analysisByMonthDto = analysisDto.data.find { it.date == date }

    if ( analysisByMonthDto == null ) {

      analysisByMonthDto = new AnalysisByMonthDto()
      analysisByMonthDto.date = date
      analysisDto.data << analysisByMonthDto

    }

    return analysisByMonthDto

  }

  private AnalysisByCategoryDto processAnalysisByCategory(
      AnalysisByMonthDto analysisByMonthDto, String categoryId,
      BigDecimal amount ) throws Exception {


    def analysisByCategoryDto = analysisByMonthDto.categories.find {
        it.categoryId == categoryId }

    if ( analysisByCategoryDto == null ) {
      analysisByCategoryDto = new AnalysisByCategoryDto()
      analysisByCategoryDto.categoryId = categoryId
      analysisByMonthDto.categories << analysisByCategoryDto
    }

    analysisByCategoryDto.amount += amount
    return analysisByCategoryDto

  }

  private AnalysisBySubcategoryDto processAnalysisBySubcategory(
      AnalysisByCategoryDto analysisByCategoryDto, String subcategoryId,
      ApiTransactionDto apiTransactionDto ) throws Exception {

    def analysisBySubcategoryDto =
        analysisByCategoryDto.subcategories.find {
        it.categoryId == subcategoryId }

    if ( analysisBySubcategoryDto == null ) {
      analysisBySubcategoryDto = new AnalysisBySubcategoryDto()
      analysisBySubcategoryDto.categoryId = subcategoryId
      analysisByCategoryDto.subcategories << analysisBySubcategoryDto
    }

    analysisBySubcategoryDto.quantity += 1
    analysisBySubcategoryDto.amount += apiTransactionDto.amount
    analysisBySubcategoryDto.average = getRounded(
      analysisBySubcategoryDto.amount / analysisBySubcategoryDto.quantity )
    return analysisBySubcategoryDto

  }

  private void processAnalysisByTransaction(
      AnalysisBySubcategoryDto analysisBySubcategoryDto,
      ApiTransactionDto apiTransactionDto ) throws Exception {

    def analysisByTransactionDto =
        analysisBySubcategoryDto.transactions.find {
            it.description ==
                apiTransactionDto.cleanedDescription.toUpperCase() }

    if ( analysisByTransactionDto == null ) {
      analysisByTransactionDto = new AnalysisByTransactionDto()
      analysisByTransactionDto.description =
          apiTransactionDto.cleanedDescription.toUpperCase()
      analysisBySubcategoryDto.transactions << analysisByTransactionDto
    }

    analysisByTransactionDto.quantity += 1
    analysisByTransactionDto.amount += apiTransactionDto.amount
    analysisByTransactionDto.average = getRounded(
      analysisByTransactionDto.amount / analysisByTransactionDto.quantity )

  }

  private BigDecimal getRounded( BigDecimal input ) throws Exception {
    return input.setScale( 2, BigDecimal.ROUND_HALF_UP )
  }

}

