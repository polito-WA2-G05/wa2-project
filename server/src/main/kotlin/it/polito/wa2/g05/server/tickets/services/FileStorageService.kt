package it.polito.wa2.g05.server.tickets.services

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.stream.Stream

interface FileStorageService {
    fun save(token: String, id: Long, file: MultipartFile)
    fun getAttachments(token: String, id: Long): Stream<Path>
    fun getAttachment(token: String, id: Long, filename: String): Resource
}