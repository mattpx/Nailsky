package com.nailsky.app.data

/** Filter parameters used on the Explore screen. */
data class ExploreFilter(
    val query: String = "",
    val category: Category = Category.ALL,
    val shape: NailShape? = null,
    val finish: NailFinish? = null,
    val artist: String? = null,
    val colorMood: ColorMood? = null,
) {
    val isActive: Boolean
        get() = query.isNotBlank() ||
            category != Category.ALL ||
            shape != null ||
            finish != null ||
            artist != null ||
            colorMood != null

    val activeFilterCount: Int
        get() = listOf(
            category != Category.ALL,
            shape != null,
            finish != null,
            artist != null,
            colorMood != null,
        ).count { it }
}

/** Filters [designs] using case-insensitive multi-word matching plus structured attribute filters. */
fun filterDesigns(
    designs: List<NailDesign>,
    filter: ExploreFilter,
): List<NailDesign> {
    val tokens = filter.query
        .trim()
        .removePrefix("#")
        .lowercase()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

    return designs.filter { design ->
        val matchesCategory = filter.category == Category.ALL || design.category == filter.category
        val matchesShape = filter.shape == null || design.shape == filter.shape
        val matchesFinish = filter.finish == null || design.finish == filter.finish
        val matchesArtist = filter.artist == null || design.artist.equals(filter.artist, ignoreCase = true)

        if (!matchesCategory || !matchesShape || !matchesFinish || !matchesArtist) {
            return@filter false
        }

        val searchableText = buildString {
            append(design.title).append(' ')
            append(design.category.label).append(' ')
            append(design.artist).append(' ')
            append(design.shape.label).append(' ')
            append(design.finish.label).append(' ')
            append(design.difficulty.label).append(' ')
            append(design.description).append(' ')
            design.tags.forEach { append(it).append(' ') }
        }.lowercase()

        val matchesMood = filter.colorMood == null ||
            filter.colorMood.keywords.any { kw -> searchableText.contains(kw) }

        if (!matchesMood) return@filter false
        if (tokens.isEmpty()) return@filter true

        tokens.all { token -> searchableText.contains(token) }
    }
}

/** Returns weekly ranked top looks sorted by community likes. */
fun weeklyTopDesigns(
    designs: List<NailDesign>,
    limit: Int = 6,
): List<NailDesign> = designs.sortedByDescending { it.likesCount }.take(limit)

/** Returns up to [limit] related designs ranked by shared category, finish, shape, and tags. */
fun relatedDesigns(
    design: NailDesign,
    allDesigns: List<NailDesign>,
    limit: Int = 6,
): List<NailDesign> {
    val targetTags = design.tags.toSet()
    return allDesigns
        .asSequence()
        .filter { it.id != design.id }
        .sortedByDescending { candidate ->
            var score = 0
            if (candidate.category == design.category) score += 4
            if (candidate.finish == design.finish) score += 2
            if (candidate.shape == design.shape) score += 1
            score += candidate.tags.count { it in targetTags }
            score
        }
        .take(limit)
        .toList()
}
