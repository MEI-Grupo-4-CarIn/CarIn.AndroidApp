package com.carin.domain.enums

enum class Role(val roleId: Int, val description: String, val stringKey: String) {
    Admin(1, "Administrator", "admin_role"),
    Manager(2, "Manager", "manager_role"),
    Driver(3, "Driver", "driver_role");

    companion object {
        private val mapById = entries.associateBy(Role::roleId)
        private val mapByDescription = entries.associateBy(Role::description)

        fun fromId(roleId: Int): Role? = mapById[roleId]
        fun fromDescription(description: String): Role? = mapByDescription[description]
    }
}