package ryanv.talkative.common.util

class RefCountMap<K, V> : HashMap<K, RefCountMap.ReferenceValue<V>>() {
    fun put(key: K, value: V) {
        super.put(key, ReferenceValue(this, value))
    }

    fun registerReference(key: K, reference: Any): (() -> V)? {
        get(key)?.let {
            it.references.add(reference)
            return it::getValue
        }
        return null
    }

    fun unregisterReference(key: K, reference: Any) {
        get(key)?.let {
            it.references.remove(reference)
            if (it.references.isEmpty())
                remove(key)
        }
    }

    class ReferenceValue<V>(private val parent: RefCountMap<*, V>, private val value: V) {
        internal var references: ArrayList<Any> = ArrayList()

        internal fun getValue(): V {
            return value
        }
    }
}