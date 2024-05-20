package com.carin.domain.enums

enum class Role(val roleId: Int, val description: String) {
    Admin(1, "Administrator"),
    Manager(2, "Manager"),
    Driver(3, "Driver");

    companion object {
        private val mapById = entries.associateBy(Role::roleId)
        private val mapByDescription = entries.associateBy(Role::description)

        fun fromId(roleId: Int): Role? = mapById[roleId]
        fun fromDescription(description: String): Role? = mapByDescription[description]
    }
}