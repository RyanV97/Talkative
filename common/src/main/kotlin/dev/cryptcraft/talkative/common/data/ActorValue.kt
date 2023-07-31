package dev.cryptcraft.talkative.common.data

open class ActorValue<T : Comparable<T>>(private var value: T) {
    private var defaultValue: T = value
    private var range: ClosedRange<T>? = null

    fun withDefaultValue(value: T): ActorValue<T> {
        this.defaultValue = value
        return this
    }

    fun isValueDefault(): Boolean {
        return this.value == this.defaultValue
    }

    open fun setValue(newValue: T): Boolean {
        if (!isInRange(newValue))
            return false
        this.value = newValue
        return true
    }

    fun getValue(): T {
        return this.value
    }

    fun getDefault(): T {
        return this.defaultValue
    }

    fun resetToDefault() {
        this.value = this.defaultValue
    }

    fun withRange(range: ClosedRange<T>): ActorValue<T> {
        this.range = range
        return this
    }

    fun withRange(min: T, max: T): ActorValue<T> {
        this.range = min..max
        return this
    }

    fun isInRange(value: T): Boolean {
        return range?.contains(value) ?: true
    }
}