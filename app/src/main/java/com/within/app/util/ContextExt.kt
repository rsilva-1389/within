package com.within.app.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/** Unwrap the [Activity] backing a Compose [Context], or null. Needed to launch the billing flow. */
fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
