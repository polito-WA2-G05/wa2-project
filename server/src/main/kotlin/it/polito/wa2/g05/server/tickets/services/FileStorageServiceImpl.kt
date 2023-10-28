package it.polito.wa2.g05.server.tickets.services

import it.polito.wa2.g05.server.authentication.security.jwt.JwtDecoder
import it.polito.wa2.g05.server.authentication.utils.Role
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.tickets.ForbiddenActionException
import it.polito.wa2.g05.server.tickets.TicketNotFoundException
import it.polito.wa2.g05.server.tickets.TicketStatusNotValidException
import it.polito.wa2.g05.server.tickets.entities.Attachment
import it.polito.wa2.g05.server.tickets.repositories.AttachmentRepository
import it.polito.wa2.g05.server.tickets.repositories.TicketRepository
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

@Service
class FileStorageServiceImpl(
    private val ticketRepository: TicketRepository,
    private val attachmentRepository: AttachmentRepository,
    private val jwtDecoder: JwtDecoder
) : FileStorageService {

    private val root = Paths.get("uploads")

    init {
        try {
            Files.createDirectories(root)
        } catch (e: IOException) {
            throw RuntimeException("Could not init folder for uploads")
        }
    }

    override fun save(token: String, id: Long, file: MultipartFile) {
        try {
            val user = UserDetails(jwtDecoder.decode(token))

            val ticket = ticketRepository.findById(id).orElseThrow {
                throw TicketNotFoundException(id)
            }

            if (ticket.status != TicketStatus.IN_PROGRESS) {
                throw TicketStatusNotValidException("You cannot upload attachment cause ticket is not IN_PROGRESS")
            }

            if (user.authorities.contains(Role.CUSTOMER.roleName) && ticket.customer.id != user.uuid) {
                throw ForbiddenActionException("You are not allowed to perform this action")
            } else if (user.authorities.contains(Role.EXPERT.roleName) && ticket.expert?.id != user.uuid) {
                throw ForbiddenActionException("You are not allowed to perform this action")
            }

            val attachmentFilenames = attachmentRepository.findAll().map { it.filename.split(".")[0] }

            var filename = file.originalFilename.toString().split(".")[0]
            var extension = file.originalFilename.toString().split(".")[1]


            var currentFilename = filename
            var occurances = 1

            while (attachmentFilenames.contains(currentFilename)) {
                currentFilename = filename + "-$occurances"
                occurances += 1
            }

            filename = currentFilename + "." + extension

            Files.copy(file.inputStream, root.resolve(filename))
            attachmentRepository.save(Attachment(filename, ticket))
        } catch (e: FileAlreadyExistsException) {
            throw RuntimeException("A file of that name already exists")
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    override fun getAttachments(token: String, id: Long): Stream<Path> {
        val user = UserDetails(jwtDecoder.decode(token))

        val ticket = ticketRepository.findById(id).orElseThrow {
            throw TicketNotFoundException(id)
        }

        if (ticket.status != TicketStatus.IN_PROGRESS) {
            throw TicketStatusNotValidException("You cannot retrieve attachments cause ticket is not IN_PROGRESS")
        }

        if (user.authorities.contains(Role.CUSTOMER.roleName) && ticket.customer.id != user.uuid) {
            throw ForbiddenActionException("You are not allowed to perform this action")
        } else if (user.authorities.contains(Role.EXPERT.roleName) && ticket.expert?.id != user.uuid) {
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val attachmentFilenames = attachmentRepository.findAllByTicket(ticket).map { it.filename }

        return Files.walk(root, 1)
            .filter { !it.equals(root) }
            .filter { attachmentFilenames.contains(it.fileName.toString()) }
            .map(root::relativize)

    }

    override fun getAttachment(token: String, id: Long, filename: String): Resource {
        val user = UserDetails(jwtDecoder.decode(token))

        val ticket = ticketRepository.findById(id).orElseThrow {
            throw TicketNotFoundException(id)
        }

        if (ticket.status != TicketStatus.IN_PROGRESS) {
            throw TicketStatusNotValidException("You cannot download attachment cause ticket is not IN_PROGRESS")
        }

        if (user.authorities.contains(Role.CUSTOMER.roleName) && ticket.customer.id != user.uuid) {
            throw ForbiddenActionException("You are not allowed to perform this action")
        } else if (user.authorities.contains(Role.EXPERT.roleName) && ticket.expert?.id != user.uuid) {
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val file = root.resolve(filename)
        val resource = UrlResource(file.toUri())
        if (resource.exists() || resource.isReadable)
            return resource
        else throw RuntimeException("Could not read the file")

    }
}