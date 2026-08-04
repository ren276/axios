package dev.sandesh.kharcha.feature.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

/**
 * Glance App Widget displaying monthly summary.
 */
class KharchaWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In a real app, you would fetch data from repository using a Worker or EntryPoint
        val spent = 45200.0
        val remaining = 15000.0

        provideContent {
            WidgetContent(spent, remaining)
        }
    }

    @Composable
    private fun WidgetContent(spent: Double, remaining: Double) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E)) // Dark theme fallback
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Kharcha - This Month",
                style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.Gray))
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(
                "Spent: ₹${String.format("%,.0f", spent)}",
                style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontWeight = FontWeight.Bold)
            )
            Text(
                "Remaining: ₹${String.format("%,.0f", remaining)}",
                style = TextStyle(color = androidx.glance.unit.ColorProvider(Color(0xFF4CAF50)), fontWeight = FontWeight.Medium)
            )
        }
    }
}
