package com.example.medjournal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/** Inflate a ViewGroup by without attaching layout to its root
 * @param layoutRes layout resource file
 * @param attachToRoot whether to attach layout to its root, false by default
 */
internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}