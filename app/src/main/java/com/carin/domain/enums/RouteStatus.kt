package com.carin.domain.enums

enum class RouteStatus(val statusId: Int, val description: String) {
    Pending(0, "Pending"),
    InProgress(1, "InProgress"),
    Completed(2, "Completed"),
    Cancelled(3, "Cancelled");

    companion object {
        private val mapById = entries.associateBy(RouteStatus::statusId)
        private val mapByDescription = entries.associateBy { it.description.lowercase() }

        fun fromId(statusId: Int): RouteStatus? = mapById[statusId]
        fun fromDescription(description: String): RouteStatus? = mapByDescription[description.lowercase()]
    }
}
