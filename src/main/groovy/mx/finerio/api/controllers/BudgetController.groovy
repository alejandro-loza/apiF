package mx.finerio.api.controllers


import io.reactivex.Single
import mx.finerio.api.dtos.pfm.BudgetDto
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.services.BudgetService
import mx.finerio.api.validation.BudgetCreateCommand
import mx.finerio.api.validation.BudgetUpdateCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.annotation.Nullable
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/budget")
class BudgetController {

  @Autowired
  BudgetService budgetService

  @PostMapping()
  ResponseEntity create( @RequestBody @Valid BudgetCreateCommand cmd ) {
    if (!cmd) {
      throw new BadRequestException('request.body.invalid')
    }
    new ResponseEntity(budgetService.crateBudgetDtoWithAnalysis(budgetService.create(cmd) ), HttpStatus.OK as HttpStatus)
  }

  @GetMapping("/{id}")
  ResponseEntity show(@NotNull @PathVariable  Long id) {
    new ResponseEntity(budgetService.findById(id), HttpStatus.OK as HttpStatus)
  }

  @GetMapping()
  ResponseEntity showAll(@RequestParam(required = false) Long cursor, @RequestParam Long customerId) {
    def response = cursor
            ? budgetService.findAllByCustomerAndCursor(customerId, cursor)
            : budgetService.findAllByCustomerId(customerId)
    new ResponseEntity(response, HttpStatus.OK as HttpStatus)

  }

  @PutMapping("/{id}")
  ResponseEntity edit( @RequestBody  @Valid BudgetUpdateCommand cmd,  @PathVariable Long id ) {
    new ResponseEntity(budgetService.update(cmd,  budgetService.find(id)), HttpStatus.OK)
  }

  @DeleteMapping("/{id}")
  ResponseEntity delete(@NotNull @PathVariable Long id) {
    budgetService.delete(id)
    new ResponseEntity( HttpStatus.NO_CONTENT )
  }


}
