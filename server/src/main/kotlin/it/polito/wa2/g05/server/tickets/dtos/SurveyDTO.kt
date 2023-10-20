package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.tickets.entities.Survey
import it.polito.wa2.g05.server.tickets.utils.Rating

data class SurveyDTO(
    val serviceValuation: Rating,
    val professionality: Rating,
    val comment: String?
)

fun Survey.toDTO(): SurveyDTO {
    return SurveyDTO(serviceValuation, professionality, comment)
}