package com.example.mytodo.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.mytodo.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */

fun View.showSnackBar(snackBarText: String, timeLength: Int) {
    Snackbar.make(this, snackBarText, timeLength).run {
        addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
            }

            override fun onShown(sb: Snackbar?) {
                super.onShown(sb)
            }
        })
            .show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */

fun View.setupSnackBar(
    lifecycleOwner: LifecycleOwner,
    snackBarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    snackBarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackBar(context.getString(it), timeLength)
        }
    })
}

fun FloatingActionButton.setUpRotateAnimation(rotate: Boolean): Boolean {
    this.animate().apply {
        duration = 500
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
            }
        })
        rotation(if (rotate) 135f else 0f)
    }
    return rotate
}


/**
 * Be aware that this utility method ONLY works when called from an Activity!
 * The below method calls getCurrentFocus of the target Activity to fetch the proper window token.
 * https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
 */
fun hideKeyboard(activity: Activity) {
    val im: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    im.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * But suppose you want to hide the keyboard from an EditText hosted in a DialogFragment?
 * You can't use the method above for that:
 * This won't work because you'll be passing a reference to the Fragment's host Activity,
 * which will have no focused control while the Fragment is shown!
 * Wow!
 * So, for hiding the keyboard from fragments, I resort to the lower-level, more common, and uglier:
 * https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
 */

fun hideKeyboardFrom(context: Context?, view: View) {
    val im: InputMethodManager =
        context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}

