package dev.sandesh.kharcha.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sandesh.kharcha.core.designsystem.theme.ColorAlertAmber
import dev.sandesh.kharcha.core.designsystem.theme.ColorAlertGreen
import dev.sandesh.kharcha.core.designsystem.theme.ColorAlertRed
import dev.sandesh.kharcha.core.designsystem.theme.ColorAlertYellow

// ─── KharchaCard ─────────────────────────────────────────────────────────────

@Composable
fun KharchaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = { Column(modifier = Modifier.padding(16.dp), content = content) }
        )
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = { Column(modifier = Modifier.padding(16.dp), content = content) }
        )
    }
}

// ─── KharchaTopBar ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KharchaTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        modifier = modifier,
        navigationIcon = { navigationIcon?.invoke() },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// ─── EmptyState ───────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    message: String,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon?.invoke()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(24.dp))
            action()
        }
    }
}

// ─── SkeletonLoader ───────────────────────────────────────────────────────────

@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    var shimmerAlpha by remember { mutableStateOf(0.3f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (shimmerAlpha == 0.3f) 0.7f else 0.3f,
        animationSpec = tween(durationMillis = 800),
        label = "shimmer"
    )
    LaunchedEffect(animatedAlpha) { shimmerAlpha = animatedAlpha }

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = animatedAlpha))
    )
}

// ─── BudgetProgressBar ───────────────────────────────────────────────────────

@Composable
fun BudgetProgressBar(
    percent: Float,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 8.dp
) {
    val color = when {
        percent >= 1.0f -> ColorAlertRed
        percent >= 0.8f -> ColorAlertAmber
        percent >= 0.5f -> ColorAlertYellow
        else -> ColorAlertGreen
    }
    val animatedProgress by animateFloatAsState(
        targetValue = percent.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "budget_progress"
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50)),
        color = color,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

// ─── AmountText ───────────────────────────────────────────────────────────────

@Composable
fun AmountText(
    amount: Double,
    isCredit: Boolean,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    val color = if (isCredit) ColorAlertGreen else MaterialTheme.colorScheme.error
    val prefix = if (isCredit) "+" else "−"
    Text(
        text = "$prefix₹${String.format("%.2f", amount)}",
        color = color,
        style = style,
        modifier = modifier
    )
}
