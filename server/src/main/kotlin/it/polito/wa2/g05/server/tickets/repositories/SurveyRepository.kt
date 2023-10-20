package it.polito.wa2.g05.server.tickets.repositories

import it.polito.wa2.g05.server.tickets.entities.Survey
import org.springframework.data.jpa.repository.JpaRepository

public interface SurveyRepository: JpaRepository<Survey,Long>{
}
