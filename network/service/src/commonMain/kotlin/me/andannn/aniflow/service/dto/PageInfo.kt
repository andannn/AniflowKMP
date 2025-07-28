package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class PageInfo(
    /**
     * The total number of items. Note: This value is not guaranteed to be accurate, do not rely on
     * this for logic
     */
    public val total: Int? = null,
    /**
     * The count on a page
     */
    public val perPage: Int? = null,
    /**
     * The current page
     */
    public val currentPage: Int? = null,
    /**
     * The last page
     */
    public val lastPage: Int? = null,
    /**
     * If there is another page
     */
    public val hasNextPage: Boolean? = null,
)
