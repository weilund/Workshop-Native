package com.slay.workshopnative.data.preferences

import com.slay.workshopnative.data.model.WorkshopBrowseQuery
import com.slay.workshopnative.data.model.WorkshopDateRangeFilter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedWorkshopFilterTest {

    @Test
    fun from_and_toQuery_round_trips_filter_fields() {
        val query = WorkshopBrowseQuery(
            sectionKey = WorkshopBrowseQuery.SECTION_ITEMS,
            sortKey = WorkshopBrowseQuery.SORT_LAST_UPDATED,
            periodDays = 30,
            searchText = "cat",
            requiredTags = setOf("Scene", "Anime"),
            excludedTags = setOf("Mature"),
            showIncompatible = true,
            createdDateRange = WorkshopDateRangeFilter(
                startEpochSeconds = 100L,
                endEpochSeconds = 200L,
            ),
            updatedDateRange = WorkshopDateRangeFilter(
                startEpochSeconds = 300L,
                endEpochSeconds = 400L,
            ),
            page = 3,
            pageSize = 18,
        )

        val saved = SavedWorkshopFilter.from(
            query = query,
            appId = 431960,
            savedAtMs = 1L,
        )
        val restored = saved.toQuery(WorkshopBrowseQuery(pageSize = 18))

        assertEquals(WorkshopBrowseQuery.SORT_LAST_UPDATED, restored.sortKey)
        assertEquals(30, restored.periodDays)
        assertEquals(setOf("Anime", "Scene"), restored.requiredTags)
        assertEquals(setOf("Mature"), restored.excludedTags)
        assertTrue(restored.showIncompatible)
        assertEquals(100L, restored.createdDateRange.startEpochSeconds)
        assertEquals(200L, restored.createdDateRange.endEpochSeconds)
        assertEquals(300L, restored.updatedDateRange.startEpochSeconds)
        assertEquals(400L, restored.updatedDateRange.endEpochSeconds)
        assertEquals(1, restored.page)
        assertEquals("", restored.searchText)
        assertEquals(431960, saved.appId)
    }

    @Test
    fun hasAnyFilter_detects_empty_and_non_empty_filters() {
        val empty = SavedWorkshopFilter.from(
            query = WorkshopBrowseQuery(),
            appId = 1,
            savedAtMs = 0L,
        )
        assertFalse(empty.hasAnyFilter)

        assertTrue(empty.copy(requiredTags = listOf("Scene")).hasAnyFilter)
        assertTrue(empty.copy(excludedTags = listOf("Mature")).hasAnyFilter)
        assertTrue(empty.copy(showIncompatible = true).hasAnyFilter)
        assertTrue(empty.copy(createdStartEpochSeconds = 1L).hasAnyFilter)
        assertTrue(empty.copy(updatedEndEpochSeconds = 1L).hasAnyFilter)
    }

    @Test
    fun toQuery_normalizes_reversed_date_range() {
        val saved = SavedWorkshopFilter(
            createdStartEpochSeconds = 500L,
            createdEndEpochSeconds = 100L,
        )
        val restored = saved.toQuery(WorkshopBrowseQuery())
        assertEquals(100L, restored.createdDateRange.startEpochSeconds)
        assertEquals(500L, restored.createdDateRange.endEpochSeconds)
    }

    @Test
    fun json_round_trip() {
        val filter = SavedWorkshopFilter(
            appId = 431960,
            sortKey = WorkshopBrowseQuery.SORT_TREND,
            periodDays = 7,
            requiredTags = listOf("Scene"),
            excludedTags = listOf("Mature"),
            showIncompatible = true,
        )
        val json = Json
        val raw = json.encodeToString(filter)
        val decoded = json.decodeFromString<SavedWorkshopFilter>(raw)
        assertEquals(filter, decoded)
    }
}
