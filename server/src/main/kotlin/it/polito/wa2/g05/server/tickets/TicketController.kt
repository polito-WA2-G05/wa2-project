package it.polito.wa2.g05.server.tickets

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
@RequestMapping("/api/tickets")
class TicketControl(private val ticketService: TicketService) {

    // /api/tickets
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(@RequestBody @Valid data: CreateTicketFormDTO, br: BindingResult): TicketDTO {
        if (br.hasErrors()) throw ValidationException(br.fieldErrors, "Validation errors")
        return ticketService.createTicket(data)
    }

    // /api/tickets/{id}/cancel
    @PatchMapping("/{id}/cancel")
    fun cancelTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.cancelTicket(id)
    }

    // /api/tickets/{id}/close
    @PatchMapping("/{id}/close")
    fun closeTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.closeTicket(id)
    }

    // /api/tickets/{id}/reopen
    @PatchMapping("/{id}/reopen")
    fun reopenTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.reopenTicket(id)
    }

    // /api/tickets/{id}/start
    @PatchMapping("/{id}/start")
    fun startTicket(@PathVariable id: Long, @RequestBody @Valid data: StartTicketFormDTO , br:BindingResult) : TicketDTO{
        if (br.hasErrors()) throw ValidationException(br.fieldErrors, "Validation errors")
        return ticketService.startTicket(id,data)
    }

    // /api/tickets/{id}/stop
    @PatchMapping("/{id}/stop")
    fun stopTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.stopTicket(id)
    }

    // /api/tickets/{id}/resolve
    @PatchMapping("/{id}/resolve")
    fun resolveTicket(@PathVariable id: Long) : TicketDTO{
        return ticketService.resolveTicket(id)
    }
    
    @GetMapping("/{id}")
    fun getTicket(@PathVariable id: Long): TicketDTO {
        return ticketService.getTicket(id)
    }

    // /api/tickets?product={productId}
    @GetMapping
    fun getAllTicketsByProduct(@RequestParam("product") productId: Long) : List<TicketDTO> {
        return ticketService.getAllTicketsByProductId(productId)
    }
}