package com.carin.domain.enums

enum class RouteStatus(
    val statusId: Int,
    val description: String,
    val stringKey: String,
    val externalKey: String
) {
    Pending(0, "Pending", "pending", "pending"),
    InProgress(1, "InProgress", "in_progress", "inProgress"),
    Completed(2, "Completed", "completed", "completed"),
    Cancelled(3, "Cancelled", "cancelled", "cancelled");

    companion object {
        private val mapById = entries.associateBy(RouteStatus::statusId)
        private val mapByDescription = entries.associateBy { it.description.lowercase() }

        fun fromId(statusId: Int): RouteStatus? = mapById[statusId]
        fun fromDescription(description: String): RouteStatus? = mapByDescription[description.lowercase()]
    }
}
