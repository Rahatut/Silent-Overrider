package com.sors.config

data class TriggerConfig(
    val keyword: String,
    val ringDurationSeconds: Long = 30,
) {
    init {
        require(keyword.isNotBlank()) { "Trigger keyword must not be blank" }
        require(ringDurationSeconds > 0) { "Ring duration must be positive" }
    }
}
