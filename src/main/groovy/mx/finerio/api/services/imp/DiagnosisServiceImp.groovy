package mx.finerio.api.services.imp

import mx.finerio.api.domain.Advice
import mx.finerio.api.domain.Category
import mx.finerio.api.domain.SuggestedExpenses
import mx.finerio.api.domain.repository.AdviceRepository
import mx.finerio.api.domain.repository.SuggestedExpensesRepository
import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.dtos.CategoryDiagnosisDto
import mx.finerio.api.dtos.DiagnosisDto
import mx.finerio.api.dtos.MonthTransactionsDiagnosisDto
import mx.finerio.api.dtos.SubCategoryDiagnosisDto
import org.springframework.stereotype.Service
import java.util.stream.Collectors
import mx.finerio.api.services.CategoryService
import mx.finerio.api.services.DiagnosisService
import mx.finerio.api.services.InsightsService
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat

@Service
class DiagnosisServiceImp extends InsightsService implements DiagnosisService {

    @Autowired
    CategoryService categoryService

    @Autowired
    SuggestedExpensesRepository suggestedExpensesRepository

    @Autowired
    AdviceRepository adviceRepository

    BigDecimal totalAverageIncome

    @Override
    DiagnosisDto getDiagnosisByCustomer(Long customerId, Long averageManualIncome ) throws Exception {
        List<ApiTransactionDto> transactions = getTransactions(customerId)
        totalAverageIncome = averageManualIncome ?: calculateAverageIncome(transactions)
        List<Category> listOfCategories = categoryService.findAll()
        DiagnosisDto diagnosisDto = new DiagnosisDto()
        diagnosisDto.with {
            averageIncome = totalAverageIncome
            data = transactionsGroupByMonth(transactions, listOfCategories)
        }
        return diagnosisDto
    }

    private static BigDecimal calculateAverageIncome(List<ApiTransactionDto> transactions) {
        List<ApiTransactionDto>  incomesTransactions = transactions.findAll { !it.isCharge }
        incomesTransactions*.amount.sum() / incomesTransactions.size()
    }

    private List<MonthTransactionsDiagnosisDto> transactionsGroupByMonth(List<ApiTransactionDto> transactionList, List<Category> listOfCategories){
        List<MonthTransactionsDiagnosisDto> movementsAnalysisDtos = []
        Map<String, List<ApiTransactionDto>> list =  transactionList.stream()
                .collect( Collectors.groupingBy({ ApiTransactionDto transaction ->
                    new SimpleDateFormat("yyyy-MM").format(transaction.date)
                }))
        for ( Map.Entry<String, List<ApiTransactionDto>> entry : list.entrySet() ) {
            movementsAnalysisDtos.add( generateMonthlyDiagnosisDto( entry.key, entry.value, listOfCategories) )
        }
        movementsAnalysisDtos
    }

    private MonthTransactionsDiagnosisDto generateMonthlyDiagnosisDto(String stringDate, List<ApiTransactionDto> transactions, List<Category> listOfCategories){
        MonthTransactionsDiagnosisDto monthTransactionsDiagnosisDto = new MonthTransactionsDiagnosisDto()
        monthTransactionsDiagnosisDto.with {
            date = stringDate
            categories = generateMonthCategoriesDiagnosis(transactions, listOfCategories)
        }
        return monthTransactionsDiagnosisDto
    }

    private List<CategoryDiagnosisDto> generateMonthCategoriesDiagnosis(List<ApiTransactionDto> transactions, List<Category> listOfCategories){
        List<CategoryDiagnosisDto> categoryDiagnosisDtos = []
        Map<String, List<ApiTransactionDto>> list =  transactions.stream()
                .collect( Collectors.groupingBy({ ApiTransactionDto transaction ->
                    getParentCategoryId(transaction, listOfCategories)
                }))

        for ( Map.Entry<String, List<ApiTransactionDto>> entry : list.entrySet() ) {
            categoryDiagnosisDtos.add( generateCategoryDiagnosis( entry.key, entry.value ) )
        }
        categoryDiagnosisDtos
    }

    private  CategoryDiagnosisDto generateCategoryDiagnosis(String categoryId, List<ApiTransactionDto> transactions){
        List<ApiTransactionDto> chargeTransactions = transactions.findAll { it.isCharge }
        BigDecimal totalSpent = chargeTransactions*.amount.sum() as BigDecimal
        SuggestedExpenses suggestedExpenses = generateSuggested(categoryId)

        CategoryDiagnosisDto categoryDiagnosisDtos = new CategoryDiagnosisDto()
        categoryDiagnosisDtos.with {
            categoryDiagnosisDtos.categoryId = categoryId
            spent = totalSpent
            average = totalSpent / chargeTransactions.size()
            others = suggestedExpenses.othersExpenses
            suggested = suggestedExpenses.suggestedPercentage * totalAverageIncome
            subcategories = generateSubCategories(chargeTransactions)
        }

       return categoryDiagnosisDtos
    }

    private SuggestedExpenses generateSuggested(String categoryId) {
         suggestedExpensesRepository.findByCategoryAndIncome(
                 categoryService.findOne(categoryId), totalAverageIncome)
    }

    private List<SubCategoryDiagnosisDto> generateSubCategories(List<ApiTransactionDto> transactions) {
        transactions.stream()
                .collect(Collectors.groupingBy({ ApiTransactionDto transaction ->
                    transaction.categoryId
                })).collect { String subCategoryId, List<ApiTransactionDto> subTransactions ->
            SubCategoryDiagnosisDto subCategoryDiagnosisDto = new SubCategoryDiagnosisDto()
            subCategoryDiagnosisDto.with {
                subCategoryDiagnosisDto.categoryId = subCategoryId
                amount = subTransactions*.amount.sum() as BigDecimal
                advices = adviceRepository.findAllByCategoryAndDateDeletedIsNull(
                        categoryService.findOne(subCategoryId))*.description as List<String>
            }
            subCategoryDiagnosisDto
        }
    }

    private String getParentCategoryId(ApiTransactionDto transaction, List<Category> listOfCategories) {
        getCategoryId(listOfCategories , transaction.categoryId)
    }

}
