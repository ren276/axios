package dev.sandesh.kharcha.feature.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import dev.sandesh.kharcha.core.model.Transaction
import dev.sandesh.kharcha.core.designsystem.R as DesignSystemR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class DataExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun exportToCsv(transactions: List<Transaction>): File? = withContext(Dispatchers.IO) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "Kharcha_Export_${System.currentTimeMillis()}.csv")
            val writer = file.bufferedWriter()
            
            // Header
            writer.write("ID,Amount,Merchant,Category,Date,Type,Confidence,NeedsReview\n")
            
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            transactions.forEach { t ->
                val dateStr = sdf.format(Date(t.timestamp))
                val merchantStr = t.merchantNormalized.replace(",", "")
                val categoryStr = t.categoryId.toString()
                writer.write("${t.id},${t.amount},$merchantStr,$categoryStr,$dateStr,${t.type.name},${t.parseConfidence},${t.status == dev.sandesh.kharcha.core.model.TransactionStatus.NEEDS_REVIEW}\n")
            }
            
            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun exportToPdf(transactions: List<Transaction>): File? = withContext(Dispatchers.IO) {
        try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            // Load and draw circular logo
            val originalBitmap = BitmapFactory.decodeResource(context.resources, DesignSystemR.drawable.logo)
            if (originalBitmap != null) {
                val size = 60
                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, size, size, false)
                val circleBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val circleCanvas = Canvas(circleBitmap)
                val shaderPaint = Paint().apply {
                    isAntiAlias = true
                    shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
                circleCanvas.drawCircle(size / 2f, size / 2f, size / 2f, shaderPaint)
                canvas.drawBitmap(circleBitmap, 50f, 30f, null)
                originalBitmap.recycle()
                scaledBitmap.recycle()
                circleBitmap.recycle()
            }

            var yPosition = 110f
            canvas.drawText("Kharcha - Transaction Report", 50f, yPosition, titlePaint)
            yPosition += 40f
            
            // Table Header
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Date", 50f, yPosition, paint)
            canvas.drawText("Merchant", 200f, yPosition, paint)
            canvas.drawText("Category", 350f, yPosition, paint)
            canvas.drawText("Amount", 450f, yPosition, paint)
            yPosition += 20f
            
            canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
            yPosition += 20f

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            
            for (t in transactions) {
                if (yPosition > 800f) {
                    document.finishPage(page)
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = 50f
                }
                
                val dateStr = sdf.format(Date(t.timestamp))
                val merchantStr = (t.merchantNormalized.ifEmpty { "Unknown" }).take(20)
                val categoryStr = t.categoryId.toString()
                val amountStr = String.format("₹%,.2f", t.amount)
                
                if (t.type == dev.sandesh.kharcha.core.model.TransactionType.CREDIT) {
                    paint.color = Color.parseColor("#388E3C") // Green
                } else {
                    paint.color = Color.BLACK
                }

                canvas.drawText(dateStr, 50f, yPosition, paint)
                canvas.drawText(merchantStr, 200f, yPosition, paint)
                canvas.drawText(categoryStr, 350f, yPosition, paint)
                canvas.drawText(amountStr, 450f, yPosition, paint)
                yPosition += 20f
            }

            document.finishPage(page)
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "Kharcha_Report_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
