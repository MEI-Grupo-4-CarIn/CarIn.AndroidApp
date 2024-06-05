package com.carin.domain.enums
enum class RouteType(val labelPt: String, val labelEn: String) {
    ALL("Todas", "All"),
    PENDING( "Pendentes", "Pending"),
    IN_PROGRESS("A decorrer", "In progress"),
    COMPLETED("Concluídas", "Completed"),
    CANCELLED("Canceladas", "Canceled");

    companion object {
        fun toRouteStatus(routeType: RouteType) : RouteStatus? {
            return when (routeType) {
                ALL -> null
                PENDING -> RouteStatus.Pending
                IN_PROGRESS -> RouteStatus.InProgress
                COMPLETED -> RouteStatus.Completed
                CANCELLED -> RouteStatus.Cancelled
            }
        }
    }
}