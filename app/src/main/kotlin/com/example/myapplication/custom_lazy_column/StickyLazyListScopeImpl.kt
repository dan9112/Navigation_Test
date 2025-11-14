package com.example.myapplication.custom_lazy_column

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

class StickyLazyListScopeImpl : StickyLazyListScope {
    val items = mutableListOf<StickyItem>()
    private var currentHeader: (@Composable () -> Unit)? = null

    override fun item(key: Any?, contentType: Any?, content: @Composable () -> Unit) {
        items.add(StickyItem(
            key = key ?: content.hashCode(),
            contentType = contentType,
            content = content,
            headerContent = currentHeader
        ))
    }

    override fun stickyHeader(key: Any?, contentType: Any?, content: @Composable () -> Unit) {
        // Добавляем прозрачную версию заголовка в основной список
        items.add(StickyItem(
            key = "header_${key ?: content.hashCode()}",
            contentType = "sticky_header",
            content = {
                Box(Modifier.fillMaxWidth().alpha(0f)) {
                    content()
                }
            },
            headerContent = content
        ))
        currentHeader = content
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        repeat(count) { index ->
            item(
                key = key?.invoke(index),
                contentType = contentType(index),
                content = { itemContent(index) }
            )
        }
    }
}

data class StickyItem(
    val key: Any,
    val contentType: Any?,
    val content: @Composable () -> Unit,
    val headerContent: (@Composable () -> Unit)?
)