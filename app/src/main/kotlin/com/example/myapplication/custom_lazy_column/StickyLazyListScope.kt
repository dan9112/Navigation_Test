package com.example.myapplication.custom_lazy_column

import androidx.compose.runtime.Composable

interface StickyLazyListScope {
    fun stickyHeader(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable () -> Unit
    )

    fun item(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable () -> Unit
    )

    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )
}


inline fun <T> StickyLazyListScope.items(
    items: List<T>,
    noinline key: ((index: Int) -> Any)? = null,
    noinline contentType: (index: Int) -> Any? = { null },
    crossinline itemContent: @Composable (item: T) -> Unit
) = items(
    count = items.size,
    key = key,
    contentType = contentType
) { index -> itemContent(items[index]) }
