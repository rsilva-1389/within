package com.within.app.ui.util

import androidx.annotation.StringRes
import com.within.app.R
import com.within.app.data.model.Category

val Category.labelRes: Int
    @StringRes get() = when (this) {
        Category.SELF_WORTH -> R.string.category_self_worth
        Category.SELF_KINDNESS -> R.string.category_self_kindness
        Category.PRESENCE -> R.string.category_presence
        Category.MOMENTUM -> R.string.category_momentum
        Category.GROWTH -> R.string.category_growth
        Category.REFLECTION -> R.string.category_reflection
    }
