package com.nailsky.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignFilterTest {

    @Test
    fun sampleDesignsHaveUniqueIdsAndRichMetadata() {
        val ids = sampleDesigns.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        sampleDesigns.forEach { design ->
            assertTrue("Missing imageUrl for ${design.title}", design.imageUrl.startsWith("https://"))
            assertTrue("Missing colors for ${design.title}", design.colors.size >= 2)
            assertTrue("Missing steps for ${design.title}", design.steps.isNotEmpty())
            assertTrue("Missing tags for ${design.title}", design.tags.isNotEmpty())
        }
    }

    @Test
    fun emptyFilterReturnsAllDesigns() {
        val result = filterDesigns(sampleDesigns, ExploreFilter())
        assertEquals(sampleDesigns.size, result.size)
        assertFalse(ExploreFilter().isActive)
    }

    @Test
    fun searchMatchesTitleTagAndArtistCaseInsensitively() {
        val frenchResults = filterDesigns(sampleDesigns, ExploreFilter(query = "FRENCH"))
        assertTrue(frenchResults.isNotEmpty())
        assertTrue(frenchResults.any { it.title.contains("French", ignoreCase = true) })

        val hashtagResults = filterDesigns(sampleDesigns, ExploreFilter(query = "#sakura"))
        assertEquals(1, hashtagResults.size)
        assertEquals("Cherry Blossom", hashtagResults.first().title)

        val artistResults = filterDesigns(sampleDesigns, ExploreFilter(artist = "@luna.nails"))
        assertTrue(artistResults.isNotEmpty())
        assertTrue(artistResults.all { it.artist == "@luna.nails" })
    }

    @Test
    fun structuredFiltersCombineWithQuery() {
        val filter = ExploreFilter(
            shape = NailShape.ALMOND,
            finish = NailFinish.CHROME,
        )
        val result = filterDesigns(sampleDesigns, filter)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.shape == NailShape.ALMOND && it.finish == NailFinish.CHROME })
        assertEquals(2, filter.activeFilterCount)
    }

    @Test
    fun relatedDesignsExcludesSelfAndPrioritizesSameCategory() {
        val target = sampleDesigns.first() // Classic French Tip (FRENCH)
        val related = relatedDesigns(target, sampleDesigns, limit = 5)
        assertEquals(5, related.size)
        assertTrue(related.none { it.id == target.id })
        assertEquals(Category.FRENCH, related.first().category)
    }
}
