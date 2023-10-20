package it.polito.wa2.g05.server.tickets.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.tickets.services.TicketService
import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.tickets.dtos.*
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Observed
@RestController
@RequestMapping("/api")
class TicketControl(private val ticketService: TicketService) {

    private val log = LoggerFactory.getLogger("TicketController")

    /* POST /api/customer/tickets */

    @PostMapping("/customer/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestHeader("Authorization") token: String, @RequestBody @Valid data: CreateTicketFormDTO, br: BindingResult): TicketDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors)
        log.info("Creating a ticket")
        return ticketService.createTicket(data, token)
    }

    /* PATCH /api/customer/tickets/{id}/cancel */

    @PatchMapping("/customer/tickets/{id}/cancel")
    fun cancelTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Cancelling ticket id:$id")
        return ticketService.cancelTicket(id, token)
    }

    /* PATCH /api/expert/tickets/{id}/close */

    @PatchMapping("/expert/tickets/{id}/close")
    fun expertCloseTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Expert closing ticket id:$id")
        return ticketService.expertCloseTicket(id, token)
    }

    /* PATCH /api/manager/tickets/{id}/close */

    @PatchMapping("/manager/tickets/{id}/close")
    fun managerCloseTicket(@PathVariable id: Long) : TicketDTO {
        log.info("Manager closing ticket id:$id")
        return ticketService.managerCloseTicket(id)
    }

    /* PATCH /api/customer/tickets/{id}/reopen */

    @PatchMapping("/customer/tickets/{id}/reopen")
    fun reopenTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Reopening ticket with id:$id")
        return ticketService.reopenTicket(id, token)
    }

    /* PATCH /api/manager/tickets/{id}/start */

    @PatchMapping("/manager/tickets/{id}/start")
    fun startTicket(@PathVariable id: Long, @RequestBody @Valid data: StartTicketFormDTO , br:BindingResult) : TicketDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors)
        log.info("Manager started ticket with id:$id")
        return ticketService.startTicket(id,data)
    }

    /* PATCH /api/expert/tickets/{id}/stop */

    @PatchMapping("/expert/tickets/{id}/stop")
    fun stopTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Expert stopped ticket with id:$id")
        return ticketService.stopTicket(id, token)
    }

    /* PATCH /api/expert/tickets/{id}/resolve */

    @PatchMapping("/expert/tickets/{id}/resolve")
    fun expertResolveTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Expert resolved ticket with id:$id")
        return ticketService.expertResolveTicket(id, token)
    }
    /* PATCH /api/manager/tickets/{id}/resolve */

    @PatchMapping("/manager/tickets/{id}/resolve")
    fun managerResolveTicket(@PathVariable id: Long, @RequestBody @Valid data: ManagerResolveTicketDTO, br: BindingResult) : TicketDTO {
        log.info("Manager resolved ticket with id:$id")
        return ticketService.managerResolveTicket(id, data)
    }

    /* GET /api/manager/tickets/{id} */

    @GetMapping("/manager/tickets/{id}")
    fun managerGetTicket(@PathVariable id: Long): TicketDTO {
        log.info("Getting ticket with id:$id")
        return ticketService.managerGetTicket(id)
    }

    /* GET /api/customer/tickets/{id} */

    @GetMapping("/customer/tickets/{id}")
    fun customerGetTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long): TicketDTO {
        log.info("Getting ticket with id:$id")
        return ticketService.customerGetTicket(id, token)
    }

    /* GET /api/expert/tickets/{id} */

    @GetMapping("/expert/tickets/{id}")
    fun expertGetTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long): TicketDTO {
        log.info("Getting ticket with id:$id")
        return ticketService.expertGetTicket(id, token)
    }

    /* GET /api/manager/tickets?product={producEan} */

    @GetMapping("/manager/tickets")
    fun managerGetTickets(@RequestParam("product", required = false) productEan: String?) : List<TicketDTO> {
        log.info("Manager retrieving all tickets")
        return ticketService.managerGetTickets(productEan)
    }

    /* GET /api/customer/tickets?product={productEan} */

    @GetMapping("/customer/tickets")
    fun customerGetTickets(@RequestHeader("Authorization") token: String, @RequestParam("product", required = false) productEan: String?): List<TicketDTO> {
        log.info("Customer retrieving tickets")
        return ticketService.customerGetTickets(token, productEan)
    }

    /* GET /api/expert/tickets?product={productEan} */

    @GetMapping("/expert/tickets")
    fun expertGetTickets(@RequestHeader("Authorization") token: String, @RequestParam("product", required = false) productEan: String?): List<TicketDTO> {
        log.info("Expert retrieving all tickets")
        return ticketService.expertGetTickets(token, productEan)
    }

    /* GET /api/manager/tickets/experts */

    @GetMapping("/manager/tickets/experts")
    fun getExperts(): List<EmployeeDTO> {
        log.info("Manager retrieving all experts")
        return ticketService.getExperts()
    }


    //CHANGES

    /* GET /api/manager/tickets/changes */

    @GetMapping("/manager/tickets/changes")
    fun getChanges(): List<ChangeDTO> {
        log.info("Manager retrieving all changes")
        return ticketService.getChanges()
    }


    //SURVEY

    @PostMapping("/customer/tickets/{id}/survey")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSurvey(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long,
        @RequestBody @Valid data: CreateSurveyDTO,
        br: BindingResult
    ): TicketDTO {
        log.info("Customer creating a survey")
        return ticketService.createSurvey(token, data, id)
    }
}