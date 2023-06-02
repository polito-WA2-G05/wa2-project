package it.polito.wa2.g05.server.utils

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.util.ProxyUtils
import java.io.Serializable

@MappedSuperclass
abstract class EntityBase<T: Serializable> {
    companion object{
        private const val serialVersionUID = -43869754L
    }

    abstract var id: T?

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