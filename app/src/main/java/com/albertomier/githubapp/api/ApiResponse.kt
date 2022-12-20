package com.albertomier.githubapp.api

import java.util.regex.Pattern

sealed class ApiResponse<T> {
}

class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>() {
    constructor(body: T, linksHeaders: String?) : this(
        body = body,
        links = linksHeaders?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1) as String)
                } catch (e: java.lang.NumberFormatException) {
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()

                if (count == 2) {
                    links[matcher.group(2) as String] = matcher.group(1) as String
                }
            }

            return links
        }
    }
}

data class ApiErrorResponse<T>(
    val errorMessage: String
) : ApiResponse<T>()