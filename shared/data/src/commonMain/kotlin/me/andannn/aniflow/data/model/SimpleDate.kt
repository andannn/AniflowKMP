package me.andannn.aniflow.data.model

public data class SimpleDate(
    /**
     * Numeric Year (2017)
     */
    public val year: Int,
    /**
     * Numeric Month (3)
     */
    public val month: Int,
    /**
     * Numeric Day (24)
     */
    public val day: Int? = null,
)
