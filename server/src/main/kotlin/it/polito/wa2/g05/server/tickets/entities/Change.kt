package it.polito.wa2.g05.server.tickets.entities

import it.polito.wa2.g05.server.EntityBase
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name="changes")
class Change(

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    var fromStatus: TicketStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status")
    var toStatus: TicketStatus? = null,

    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: Date? = null,

    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    var ticket: Ticket? = null,

    @ManyToOne
    @JoinColumn(name = "expert_id", referencedColumnName = "id")
    var expert: Employee? = null
): EntityBase<Long>()