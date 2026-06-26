package com.within.app.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.File
import java.io.FileOutputStream

/**
 * Draws a branded, square share image for a reaffirmation — the acquisition loop (a user shares
 * their daily reaffirmation, the card carries the brand). Pure Android Canvas so it renders the
 * same on every Compose version. Returns a PNG in the cache dir, ready for a FileProvider uri.
 */
object ShareCardRenderer {

    private const val SIZE = 1080
    private const val PADDING = 96f
    private const val WARM = 0xFFFE856C.toInt()
    private const val SUPPORTIVE = 0xFFFEB97E.toInt()
    private const val WHITE = 0xFFFFFFFF.toInt()

    fun render(context: Context, reaffirmation: String): File {
        val bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Warm coral → peach diagonal gradient, matching ReaffirmationCard.
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, SIZE.toFloat(), SIZE.toFloat(),
                WARM, SUPPORTIVE, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, SIZE.toFloat(), SIZE.toFloat(), bgPaint)

        // Wordmark, top-left.
        val wordmarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = WHITE
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 46f
            letterSpacing = 0.32f
            alpha = 235
        }
        canvas.drawText("WITHIN", PADDING, PADDING + 40f, wordmarkPaint)

        // Reaffirmation, wrapped and vertically centered.
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = WHITE
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            textSize = 70f
        }
        val textWidth = (SIZE - PADDING * 2).toInt()
        val layout = StaticLayout.Builder
            .obtain("“$reaffirmation”", 0, "“$reaffirmation”".length, textPaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(14f, 1f)
            .build()
        canvas.save()
        val textY = (SIZE - layout.height) / 2f
        canvas.translate(PADDING, textY)
        layout.draw(canvas)
        canvas.restore()

        // Ori, bottom-right.
        runCatching {
            context.assets.open("ori/expressions/ori_gratitude.webp").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()?.let { ori ->
            val oriSize = 200
            val dst = Rect(
                SIZE - PADDING.toInt() - oriSize,
                SIZE - PADDING.toInt() - oriSize,
                SIZE - PADDING.toInt(),
                SIZE - PADDING.toInt()
            )
            canvas.drawBitmap(ori, null, dst, Paint(Paint.FILTER_BITMAP_FLAG))
            ori.recycle()
        }

        // Footer tagline, bottom-left.
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = WHITE
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textSize = 34f
            alpha = 210
        }
        canvas.drawText("A 30-day journey home to yourself", PADDING, SIZE - PADDING, footerPaint)

        val dir = File(context.cacheDir, "share").apply { mkdirs() }
        val file = File(dir, "reaffirmation.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        return file
    }
}
