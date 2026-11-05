package com.slay.workshopnative.data.preferences

import com.slay.workshopnative.data.model.WorkshopBrowseQuery
import com.slay.workshopnative.data.model.WorkshopDateRangeFilter
import kotlinx.serialization.Serializable

/**
 * 工坊浏览页上次使用的筛选条件。
 *
 * 只保存与「筛选」相关的字段：排序、时间范围、标签、兼容性开关和发布日期范围；
 * 搜索词和页码不保存，避免下次进入时意外带着上次的搜索词或翻页位置。
 * [appId] 用于判断这份条件是否属于同一个游戏的工坊：标签是游戏相关的，
 * 换游戏后恢复标签可能直接得到空列表，所以只在同一个 appId 下恢复。
 */
@Serializable
data class SavedWorkshopFilter(
    val appId: Int = 0,
    val sortKey: String = WorkshopBrowseQuery.SORT_TREND,
    val periodDays: Int = 7,
    val requiredTags: List<String> = emptyList(),
    val excludedTags: List<String> = emptyList(),
    val showIncompatible: Boolean = false,
    val createdStartEpochSeconds: Long = 0L,
    val createdEndEpochSeconds: Long = 0L,
    val updatedStartEpochSeconds: Long = 0L,
    val updatedEndEpochSeconds: Long = 0L,
    val savedAtMs: Long = 0L,
) {
    val hasAnyFilter: Boolean
        get() = requiredTags.isNotEmpty() ||
            excludedTags.isNotEmpty() ||
            showIncompatible ||
            createdStartEpochSeconds > 0L ||
            createdEndEpochSeconds > 0L ||
            updatedStartEpochSeconds > 0L ||
            updatedEndEpochSeconds > 0L

    fun toQuery(base: WorkshopBrowseQuery): WorkshopBrowseQuery {
        return base.copy(
            sortKey = sortKey.takeIf(String::isNotBlank) ?: WorkshopBrowseQuery.SORT_TREND,
            periodDays = periodDays,
            requiredTags = requiredTags.toSet(),
            excludedTags = excludedTags.toSet(),
            showIncompatible = showIncompatible,
            createdDateRange = WorkshopDateRangeFilter(
                startEpochSeconds = createdStartEpochSeconds,
                endEpochSeconds = createdEndEpochSeconds,
            ).normalized(),
            updatedDateRange = WorkshopDateRangeFilter(
                startEpochSeconds = updatedStartEpochSeconds,
                endEpochSeconds = updatedEndEpochSeconds,
            ).normalized(),
            page = 1,
            searchText = "",
        )
    }

    companion object {
        fun from(
            query: WorkshopBrowseQuery,
            appId: Int,
            savedAtMs: Long,
        ): SavedWorkshopFilter {
            return SavedWorkshopFilter(
                appId = appId,
                sortKey = query.sortKey,
                periodDays = query.periodDays,
                requiredTags = query.requiredTags.sorted(),
                excludedTags = query.excludedTags.sorted(),
                showIncompatible = query.showIncompatible,
                createdStartEpochSeconds = query.createdDateRange.startEpochSeconds,
                createdEndEpochSeconds = query.createdDateRange.endEpochSeconds,
                updatedStartEpochSeconds = query.updatedDateRange.startEpochSeconds,
                updatedEndEpochSeconds = query.updatedDateRange.endEpochSeconds,
                savedAtMs = savedAtMs,
            )
        }
    }
}
