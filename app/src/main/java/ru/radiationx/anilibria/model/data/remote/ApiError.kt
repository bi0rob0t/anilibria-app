package ru.radiationx.anilibria.model.data.remote

data class ApiError constructor(
        val code: Int?,
        override val message: String?,
        val description: String?
) : RuntimeException()
