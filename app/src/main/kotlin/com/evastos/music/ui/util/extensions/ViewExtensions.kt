package com.evastos.music.ui.util.extensions

import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.evastos.music.R
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private const val DEBOUNCE_MILLIS = 300L

fun View.debounceClicks(): Observable<Unit> =
        clicks().debounce(DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS, Schedulers.computation())

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun TextView.showText(text: String?) {
    this.text = text
}

fun SwipeRefreshLayout.setRefreshing() {
    isRefreshing = true
}

fun SwipeRefreshLayout.setNotRefreshing() {
    isRefreshing = false
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun showSnackbarForView(
    view: View,
    snackbarMessage: String,
    actionMessage: String?,
    action: (() -> Unit)? = null
): Snackbar {
    return Snackbar.make(view, snackbarMessage, Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction(actionMessage) {
                    action?.invoke()
                }
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
                show()
            }
}

fun Snackbar?.hideIfShown() {
    this?.let {
        if (it.isShown) {
            it.dismiss()
        }
    }
}
