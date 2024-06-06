package com.carin.domain.enums

enum class UserType(val labelPt: String, val labelEn: String) {
    ALL("Todos", "All"),
    DRIVERS("Condutores", "Drivers"),
    MANAGERS("Gestores", "Managers"),
    ADMINISTRATOR("Administradores", "Administrators");

    companion object {
        fun toRole(userType: UserType) : Role? {
            return when (userType) {
                ALL -> null
                DRIVERS -> Role.Driver
                MANAGERS -> Role.Manager
                ADMINISTRATOR -> Role.Admin
            }
        }
    }
}