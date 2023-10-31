package it.polito.wa2.g05.server.tickets.entities

import it.polito.wa2.g05.server.products.entities.Product
import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.specializations.entities.Specialization
import it.polito.wa2.g05.server.tickets.utils.PriorityLevel
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import it.polito.wa2.g05.server.utils.SerialIdEntity
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "tickets")
class Ticket(
    @Enumerated(EnumType.STRING)
    var status: TicketStatus,

    var title: String,
    var description: String,

    @ManyToOne
    @JoinColumn(name = "customer_id" , referencedColumnName = "id")
    var customer: Profile,

    @ManyToOne
    @JoinColumn(name="expert_id", referencedColumnName = "id")
    var expert: Employee? = null,

    @Column(name="priority_level")
    @Enumerated(EnumType.ORDINAL)
    var priorityLevel: PriorityLevel? = null,

    @ManyToOne
    @JoinColumn(name="product_id" , referencedColumnName = "id")
    var product: Product,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    var createdDate: Date,

    @Column(name="closed_date")
    var closedDate: Date? = null,

    @ManyToOne
    @JoinColumn(name="specialization_id" , referencedColumnName = "id")
    var specialization: Specialization,

    @Column(name="resolved_description")
    var resolvedDescription: String? = null,

    @OneToOne
    @JoinColumn(name="survey_id", referencedColumnName = "id")
    var survey: Survey? = null
): SerialIdEntity()