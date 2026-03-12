package com.aliimran.financialtracker.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.aliimran.financialtracker.util.IconMapper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.domain.model.Category
import com.aliimran.financialtracker.util.Extensions.toComposeColor

/**
 * A fixed-height scrollable grid of category chips used in the
 * Add Transaction screen.
 *
 * Uses [LazyVerticalGrid] with a fixed 4-column layout.
 * The grid height is fixed so it co-exists with the surrounding
 * [LazyColumn] / scrollable [Column] without causing nested scroll
 * conflicts — the user scrolls inside the grid independently.
 *
 * @param categories       Full list of categories to display.
 * @param selectedCategory Currently selected category (null = none).
 * @param onCategoryClick  Called when the user taps a chip.
 * @param isLoading        Shows placeholder shimmer chips while loading.
 * @param gridHeight       Fixed height for the grid container.
 * @param modifier         External modifier.
 */
@Composable
fun CategoryGrid(
    categories       : List<Category>,
    selectedCategory : Category?,
    onCategoryClick  : (Category) -> Unit,
    isLoading        : Boolean = false,
    gridHeight       : Dp     = 220.dp,
    modifier         : Modifier = Modifier,
) {
    // Show 8 shimmer placeholders while loading
    val displayItems: List<Category?> = if (isLoading) List(8) { null } else categories

    LazyVerticalGrid(
        columns            = GridCells.Fixed(4),
        modifier           = modifier
            .fillMaxWidth()
            .height(gridHeight),
        contentPadding     = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement   = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled  = true,
    ) {
        items(
            items = displayItems,
            key   = { it?.id ?: System.nanoTime() },
        ) { category ->
            if (category == null) {
                CategoryChipShimmer()
            } else {
                CategoryChip(
                    category   = category,
                    isSelected = category.id == selectedCategory?.id,
                    onClick    = { onCategoryClick(category) },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Individual Category Chip
// ─────────────────────────────────────────────────────────────

@Composable
fun CategoryChip(
    category   : Category,
    isSelected : Boolean,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    val chipColor = category.color.toComposeColor()

    // Animate background fill on selection
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) chipColor.copy(alpha = 0.20f)
                      else            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label         = "chip_bg",
    )

    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = chipColor,
                    shape = RoundedCornerShape(12.dp),
                ) else Modifier
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Icon circle ───────────────────────────────────────
        Box(
            modifier         = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(chipColor.copy(alpha = if (isSelected) 0.25f else 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = IconMapper[category.iconResName],
                contentDescription = category.name,
                tint               = chipColor,
                modifier           = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Category name ─────────────────────────────────────
        Text(
            text       = category.name,
            fontSize   = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color      = if (isSelected) chipColor
                         else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign  = TextAlign.Center,
            maxLines   = 2,
            overflow   = TextOverflow.Ellipsis,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Shimmer Placeholder
// ─────────────────────────────────────────────────────────────

@Composable
private fun CategoryChipShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        )
    }
}
