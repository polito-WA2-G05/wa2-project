package it.polito.wa2.g05.server.tickets.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.tickets.dtos.AttachmentDTO
import it.polito.wa2.g05.server.tickets.services.FileStorageService
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

@Observed
@RestController
@RequestMapping("/api")
class AttachmentController(private val fileStorageService: FileStorageService) {

    private val log = LoggerFactory.getLogger("AttachmentController")

    @PostMapping("/customer/tickets/{id}/attachments")
    fun customerUploadAttachment(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile
    ) {
        log.info("Customer uploads new attachment for ticket $id")
        fileStorageService.save(token, id, file)
    }

    @PostMapping("/expert/tickets/{id}/attachments")
    fun expertUploadAttachment(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile
    ) {
        log.info("Expert uploads new attachment for ticket $id")
        fileStorageService.save(token, id, file)
    }

    @GetMapping("/customer/tickets/{id}/attachments")
    fun customerGetAttachments(@RequestHeader("Authorization") token: String, @PathVariable id: Long): List<AttachmentDTO> {
        log.info("Customer gets attachments for ticket $id")
        return fileStorageService.getAttachments(token, id).map {
            val filename = it.fileName.toString()
            val url = MvcUriComponentsBuilder.fromMethodName(
                AttachmentController::class.java,
                "customerGetAttachment",
                token,
                id,
                it.fileName.toString()
            ).build().toString()
            AttachmentDTO(filename, url)
        }.toList()
    }

    @GetMapping("/expert/tickets/{id}/attachments")
    fun expertGetAttachments(@RequestHeader("Authorization") token: String, @PathVariable id: Long): List<AttachmentDTO> {
        log.info("Expert gets attachments for ticket $id")
        return fileStorageService.getAttachments(token, id).map {
            val filename = it.fileName.toString()
            val url = MvcUriComponentsBuilder.fromMethodName(
                AttachmentController::class.java,
                "expertGetAttachment",
                token,
                id,
                it.fileName.toString()
            ).build().toString()
            AttachmentDTO(filename, url)
        }.toList()
    }

    @GetMapping("/customer/tickets/{id}/attachments/{filename:.+}")
    fun customerGetAttachment(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {
        log.info("Customer download attachment $filename of ticket $id")

        val file = fileStorageService.getAttachment(token, id, filename)

        val headers = HttpHeaders()

        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        val contentDisposition = ContentDisposition.builder("attachment")
            .filename(file.filename!!)
            .build()

        headers.contentDisposition = contentDisposition
        headers.contentLength = file.contentLength()

        return ResponseEntity.ok()
            .headers(headers)
            .cacheControl(CacheControl.noCache())
            .body(file)
    }

    @GetMapping("/expert/tickets/{id}/attachments/{filename:.+}")
    fun expertGetAttachment(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {
        log.info("Expert download attachment $filename of ticket $id")

        val file = fileStorageService.getAttachment(token, id, filename)

        val headers = HttpHeaders()

        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        val contentDisposition = ContentDisposition.builder("attachment")
            .filename(file.filename!!)
            .build()

        headers.contentDisposition = contentDisposition
        headers.contentLength = file.contentLength()

        return ResponseEntity.ok()
            .headers(headers)
            .cacheControl(CacheControl.noCache())
            .body(file)
    }
}