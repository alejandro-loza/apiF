package mx.finerio.api.services.imp

import mx.finerio.api.domain.Advice
import mx.finerio.api.domain.Category
import mx.finerio.api.domain.SuggestedExpenses
import mx.finerio.api.domain.repository.AdviceRepository
import mx.finerio.api.domain.repository.SuggestedExpensesRepository
import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.dtos.CategoryDiagnosisDto
import mx.finerio.api.dtos.DiagnosisAdviceDto
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
    DiagnosisDto getDiagnosisByCustomer(Long customerId, Optional<BigDecimal> averageManualIncome ) throws Exception {
        List<ApiTransactionDto> transactions = getTransactions(customerId)
        totalAverageIncome = averageManualIncome.isPresent() ? averageManualIncome.get() : calculateAverageIncome(transactions)
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
        incomesTransactions ? incomesTransactions*.amount.sum() / incomesTransactions.size() : 0
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
            categories = transactions ? generateMonthCategoriesDiagnosis(transactions, listOfCategories): []
        }
        return monthTransactionsDiagnosisDto
    }

    private List<CategoryDiagnosisDto> generateMonthCategoriesDiagnosis(List<ApiTransactionDto> transactions, List<Category> listOfCategories){
        List<CategoryDiagnosisDto> categoryDiagnosisDtos = []
        transactions = transactions.findAll {getParentCategoryId(it, listOfCategories)}

        if(transactions){
            Map<String, List<ApiTransactionDto>> list =  transactions.stream()
                    .collect( Collectors.groupingBy({ ApiTransactionDto transaction ->
                        getParentCategoryId(transaction, listOfCategories)
                    }))

            for ( Map.Entry<String, List<ApiTransactionDto>> entry : list.entrySet() ) {
                CategoryDiagnosisDto diagnosisDto = generateCategoryDiagnosis(entry.key, entry.value)
                if(diagnosisDto) categoryDiagnosisDtos.add(diagnosisDto)
            }
        }

        categoryDiagnosisDtos
    }

    private  CategoryDiagnosisDto generateCategoryDiagnosis(String categoryId, List<ApiTransactionDto> transactions){
        List<ApiTransactionDto> chargeTransactions = transactions.findAll { it.isCharge }
        if(chargeTransactions){
            CategoryDiagnosisDto categoryDiagnosisDtos = new CategoryDiagnosisDto()
            BigDecimal totalSpent = chargeTransactions*.amount.sum() as BigDecimal
            SuggestedExpenses suggestedExpenses = generateSuggested(categoryId)
            categoryDiagnosisDtos.with {
                categoryDiagnosisDtos.categoryId = categoryId
                spent = totalSpent
                average = totalSpent / chargeTransactions.size()
                others = suggestedExpenses ? suggestedExpenses?.othersExpenses : null
                suggested = suggestedExpenses ? suggestedExpenses?.suggestedPercentage * totalAverageIncome : 0
                subcategories = generateSubCategories(chargeTransactions)
            }
            return categoryDiagnosisDtos
        }

       return null
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
                def advicesList = adviceRepository.findAllByCategoryAndDateDeletedIsNull(
                        categoryService.findOne(subCategoryId))
                advices = advicesList.collect { new DiagnosisAdviceDto(
                  description: it.description
                )}
            }
            subCategoryDiagnosisDto
        }
    }

    private String getParentCategoryId(ApiTransactionDto transaction, List<Category> listOfCategories) {
        getCategoryId(listOfCategories , transaction.categoryId)
    }

}
