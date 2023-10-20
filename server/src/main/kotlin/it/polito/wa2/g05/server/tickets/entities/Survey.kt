package it.polito.wa2.g05.server.tickets.entities

import it.polito.wa2.g05.server.tickets.utils.Rating
import it.polito.wa2.g05.server.utils.SerialIdEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name="surveys")
class Survey (
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "service_valuation")
    var serviceValuation: Rating,

    @Enumerated(EnumType.ORDINAL)
    var professionality: Rating,
    var comment: String? = null,

    @OneToOne
    @JoinColumn(name="ticket_id", referencedColumnName="id")
    var ticket: Ticket
): SerialIdEntity()