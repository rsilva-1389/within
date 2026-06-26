package com.within.app.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider

/**
 * Renders the reaffirmation card and opens the system share sheet with it. The image is the hook;
 * a short caption carries the words for text-only targets.
 */
fun shareReaffirmation(context: Context, reaffirmation: String) {
    val file = ShareCardRenderer.render(context, reaffirmation)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "“$reaffirmation” — Within")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}
