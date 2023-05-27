package it.polito.wa2.g05.server.tickets.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.tickets.dtos.TicketDTO
import it.polito.wa2.g05.server.tickets.services.TicketService
import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.tickets.dtos.CreateTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.StartTicketFormDTO
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

    // /api/customer/tickets
    @PostMapping("/customer/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestHeader("Authorization") token: String, @RequestBody @Valid data: CreateTicketFormDTO, br: BindingResult): TicketDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors, "Validation errors")
        log.info("Creating a ticket")
        return ticketService.createTicket(data, token)
    }

    // /api/customer/tickets/{id}/cancel
    @PatchMapping("/customer/tickets/{id}/cancel")
    fun cancelTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Cancelling ticket id:$id")
        return ticketService.cancelTicket(id, token)
    }

    // /api/expert/tickets/{id}/close
    @PatchMapping("/expert/tickets/{id}/close")
    fun expertCloseTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Expert closing ticket id:$id")
        return ticketService.expertCloseTicket(id, token)
    }

    // /api/manager/tickets/{id}/close
    @PatchMapping("/manager/tickets/{id}/close")
    fun managerCloseTicket(@PathVariable id: Long) : TicketDTO {
        log.info("Manager closing ticket id:$id")
        return ticketService.managerCloseTicket(id)
    }

    // /api/customer/tickets/{id}/reopen
    @PatchMapping("/customer/tickets/{id}/reopen")
    fun reopenTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Reopening ticket with id:$id")
        return ticketService.reopenTicket(id, token)
    }

    // /api/manager/tickets/{id}/start
    @PatchMapping("/manager/tickets/{id}/start")
    fun startTicket(@PathVariable id: Long, @RequestBody @Valid data: StartTicketFormDTO , br:BindingResult) : TicketDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors, "Validation errors")
        log.info("Manager started ticket with id:$id")
        return ticketService.startTicket(id,data)
    }

    // /api/expert/tickets/{id}/stop
    @PatchMapping("/expert/tickets/{id}/stop")
    fun stopTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Expert stopped ticket with id:$id")
        return ticketService.stopTicket(id, token)
    }

    // /api/expert/tickets/{id}/resolve
    @PatchMapping("/expert/tickets/{id}/resolve")
    fun expertResolveTicket(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : TicketDTO {
        log.info("Expert resolved ticket with id:$id")
        return ticketService.expertResolveTicket(id, token)
    }
    // /api/manager/tickets/{id}/resolve
    @PatchMapping("/manager/tickets/{id}/resolve")
    fun managerResolveTicket(@PathVariable id: Long) : TicketDTO {
        log.info("Manager resolved ticket with id:$id")
        return ticketService.managerResolveTicket(id)
    }

    // /api/authenticated/tickets/{id}
    @GetMapping("/authenticated/tickets/{id}")
    fun getTicket(@PathVariable id: Long): TicketDTO {
        log.info("Getting ticket with id:$id")
        return ticketService.getTicket(id)
    }

    // /api/manager/tickets?product={productId}
    @GetMapping("/manager/tickets")
    fun getAllTicketsByProduct(@RequestParam("product") productId: Long) : List<TicketDTO> {
        log.info("Manager retrieving all tickets")
        return ticketService.getAllTicketsByProductId(productId)
    }
}