package net.kibotu.swipedirectionviewpager.demo.pages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import net.kibotu.swipedirectionviewpager.SwipeDirection
import net.kibotu.swipedirectionviewpager.ViewPagerPresenterAdapter
import net.kibotu.swipedirectionviewpager.demo.models.PageModel
import net.kibotu.swipedirectionviewpager.demo.models.ViewPagerModel
import net.kibotu.swipedirectionviewpager.demo.snackbar


/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

abstract class Page : Fragment(), ViewPagerPresenterAdapter.ViewPagerPresenter<PageModel, ViewPagerModel> {

    @get:LayoutRes
    protected abstract val layout: Int

    override var viewPagerPresenterAdapter: ViewPagerPresenterAdapter<PageModel, ViewPagerModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onSwipeIntercepted(direction: SwipeDirection) {
        Log.v(javaClass.simpleName, "[onSwipeIntercepted] $direction")
        snackbar(activity, "onSwipeIntercepted $direction")
    }

    override fun allowSwipeDirection(): SwipeDirection {
        return SwipeDirection.ALL
    }
}