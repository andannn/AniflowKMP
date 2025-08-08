package me.andannn.aniflow.data.model

data class Page<T>(
    public val total: Int,
    /**
     * The count on a page
     */
    public val perPage: Int,
    /**
     * The current page
     */
    public val currentPage: Int,
    /**
     * The last page
     */
    public val lastPage: Int,
    /**
     * If there is another page
     */
    public val hasNextPage: Boolean,
    /**
     * The items on the page
     */
    val items: List<T>,
)
