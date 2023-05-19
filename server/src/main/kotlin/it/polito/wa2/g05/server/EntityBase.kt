package it.polito.wa2.g05.server

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import java.util.UUID

@MappedSuperclass
abstract class EntityBase<T: Serializable> {
    companion object{
        private const val serialVersionUID = -43869754L
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: T? = null

    fun getId(): T? = id

    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(other === this) return true
        if(javaClass != ProxyUtils.getUserClass(other))
            return false
        other as EntityBase<*>
        return if (null == id) false
        else this.id == other.id
    }

    override fun hashCode(): Int {
        return 5
    }
}

@MappedSuperclass
abstract class EntityBaseUUID {
    companion object{
        private const val serialVersionUID = -43869754L
    }

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private var id: UUID? = null

    fun getId(): UUID? = id

    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(other === this) return true
        if(javaClass != ProxyUtils.getUserClass(other))
            return false
        other as EntityBaseUUID
        return if (null == id) false
        else this.id == other.id
    }

    override fun hashCode(): Int {
        return 5
    }
}

