package it.polito.wa2.g05.server.tickets.dtos

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.PositiveOrZero

data class CreateSurveyDTO(
    @field:PositiveOrZero
    @field:Max(4)
    var serviceValuation : Int,


    @field:PositiveOrZero
    @field:Max(4)
    var professionality : Int,

    var comment: String?
)
