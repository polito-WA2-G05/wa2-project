package it.polito.wa2.g05.server.tickets.controllers

import it.polito.wa2.g05.server.tickets.dtos.TicketDTO
import it.polito.wa2.g05.server.tickets.services.TicketService
import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.tickets.dtos.CreateTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.StartTicketFormDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TicketControl(private val ticketService: TicketService) {

    // /api/customer/tickets
    @PostMapping("/customer/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestBody @Valid data: CreateTicketFormDTO, br: BindingResult): TicketDTO {
        if (br.hasErrors()) throw ValidationException(br.fieldErrors, "Validation errors")
        return ticketService.createTicket(data)
    }

    // /api/customer/tickets/{id}/cancel
    @PatchMapping("/customer/tickets/{id}/cancel")
    fun cancelTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.cancelTicket(id)
    }

    // /api/expert/tickets/{id}/close
    @PatchMapping("/expert/tickets/{id}/close")
    fun expertCloseTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.closeTicket(id)
    }

    // /api/manager/tickets/{id}/close
    @PatchMapping("/manager/tickets/{id}/close")
    fun managerCloseTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.closeTicket(id)
    }

    // /api/customer/tickets/{id}/reopen
    @PatchMapping("/customer/tickets/{id}/reopen")
    fun reopenTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.reopenTicket(id)
    }

    // /api/manager/tickets/{id}/start
    @PatchMapping("/manager/tickets/{id}/start")
    fun startTicket(@PathVariable id: Long, @RequestBody @Valid data: StartTicketFormDTO , br:BindingResult) : TicketDTO{
        if (br.hasErrors()) throw ValidationException(br.fieldErrors, "Validation errors")
        return ticketService.startTicket(id,data)
    }

    // /api/expert/tickets/{id}/stop
    @PatchMapping("/expert/tickets/{id}/stop")
    fun stopTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.stopTicket(id)
    }

    // /api/expert/tickets/{id}/resolve
    @PatchMapping("/expert/tickets/{id}/resolve")
    fun expertResolveTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.resolveTicket(id)
    }
    // /api/manager/tickets/{id}/resolve
    @PatchMapping("/manager/tickets/{id}/resolve")
    fun managerResolveTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.resolveTicket(id)
    }

    // /api/authenticated/tickets/{id}
    @GetMapping("/authenticated/tickets/{id}")
    fun getTicket(@PathVariable id: Long): TicketDTO {
        return ticketService.getTicket(id)
    }

    // /api/manager/tickets?product={productId}
    @GetMapping("/manager/tickets")
    fun getAllTicketsByProduct(@RequestParam("product") productId: Long) : List<TicketDTO> {
        return ticketService.getAllTicketsByProductId(productId)
    }
}