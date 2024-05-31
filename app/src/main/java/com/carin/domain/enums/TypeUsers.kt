package com.carin.domain.enums
enum class TypeUsers(val labelPt: String, val labelEn: String) {
    ALL("Todos", "All"),
    DRIVERS("Condutores", "Drivers"),
    MANAGERS("Gestores", "Managers"),
    ADMINISTRATOR("Administradores", "Administrators");

    companion object {
        fun toRole(typeUsers: TypeUsers) : Role? {
            return when (typeUsers) {
                ALL -> null
                DRIVERS -> Role.Driver
                MANAGERS -> Role.Manager
                ADMINISTRATOR -> Role.Admin
            }
        }
    }
}