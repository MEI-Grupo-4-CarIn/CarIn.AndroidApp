package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DetailedResponse<T>(
    @SerializedName("data") val data: T,
    @SerializedName("meta") val meta: PaginationMeta
)

data class PaginationMeta(
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("itemsPerPage") val itemsPerPage: Int
)
