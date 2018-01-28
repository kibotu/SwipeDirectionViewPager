package net.kibotu.swipedirectionviewpager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.math.MathUtils.clamp
import android.view.ViewGroup
import java.util.*


/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

open class ViewPagerPresenterAdapter<T, VM>(fm: FragmentManager) : FragmentStatePagerAdapter(fm), LogTag {

    class Page<out T, out S>(val model: T, val presenter: S, val factory: () -> S, var dirty: Boolean = false, var toBeRemoved: Boolean = false) where S : Fragment

    interface ViewPagerPresenter<T, VM> {
        var viewPagerPresenterAdapter: ViewPagerPresenterAdapter<T, VM>?
    }

    private val uuid = UUID.randomUUID().toString()

    /**
     * Actual data containing [T] and it's [Presenter] type.
     */
    val data: MutableList<Page<T, Fragment>> = mutableListOf()

    /**
     * ViewPager Model, shared object between view page holder and pages.
     */
    var viewPagerModel: VM? = null

    var scrollListener: ScrollListener? = null

    var onPageChangeListener: Callback<Fragment>? = null

    var onPageCompleteListener: Callback<Fragment>? = null

    var onSwipeDirectionListener: Callback<SwipeDirection>? = null

    /**
     * Force a refresh of the page when a different fragment is displayed
     * this method will be called for every fragment in the ViewPager
     * see also: https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter#replacing-fragments-inside-viewpager
     */
    override fun getItemPosition(item: Any): Int {

        val position = if (data.any { it.dirty })
            POSITION_NONE
        else
            POSITION_UNCHANGED // don't force a reload

        log("[getItemPosition] position=${adapterPositionToString(position)} item=$item")

        // POSITION_NONE means something like: this fragment is no longer valid
        // triggering the ViewPager to re-build the instance of this fragment.
        return POSITION_NONE // todo not re-creating fragments each #notifyDataSetChanged, since we have different layouts in the view pager removal seems to be tricky
    }


    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        log("[destroyItem] container=$container position=$position item=$item")
        super.destroyItem(container, position, item)
    }

    fun getFragment(position: Int): Fragment {
        return data[clamp(position, 0, data.size)].presenter
    }

    override fun getItem(position: Int): Fragment {
        val page = data[position]
        log("[getItem] position=$position ${page.dirty}")

        if (page.dirty)
            page.dirty = false

        return data[position].presenter
    }

    fun <R> append(t: T, factory: () -> R) where R : Fragment, R : ViewPagerPresenterAdapter.ViewPagerPresenter<T, VM> {
        log("[getItem] size=$count at=0 item=$t")

        data.add(Page(t, factory().apply { viewPagerPresenterAdapter = this@ViewPagerPresenterAdapter }, factory))
        data.subList(0, data.size - 1).forEach { it.dirty = true }
    }

    fun <R> prepend(t: T, factory: () -> R) where R : Fragment, R : ViewPagerPresenterAdapter.ViewPagerPresenter<T, VM> {
        log("[getItem] size=$count at=0 item=$t")

        data.add(0, Page(t, factory().apply { viewPagerPresenterAdapter = this@ViewPagerPresenterAdapter }, factory))
        data.drop(1).forEach { it.dirty = true }
    }

    fun update(t: T, filter: (T) -> Boolean) {
        data.indexOfFirst { filter(it.model) }.let {
            log("[update] $it $t")

            data[it] = Page(t, data[it].presenter, data[it].factory)
            @Suppress("UNCHECKED_CAST")
            (data[it].presenter as? Updatable<T>)?.onUpdate(t)
        }
    }

    fun model(fragment: Fragment): T? {
        return data.find { it.presenter == fragment }?.model
    }

    fun model(position: Int): T? {
        return data[position].model
    }

    fun indexOf(fragment: Fragment): Int {
        return data.indexOfFirst { it.presenter == fragment }
    }

    fun removeIf(filter: (T) -> Boolean) {
        data.find { filter(it.model) }?.let {
            log("[removeIf] $it")

            it.toBeRemoved = true
            data.remove(it)
        }
    }

    fun contains(filter: (T) -> Boolean): Boolean {
        return data.find { filter(it.model) } != null
    }

    fun clear() {
        data.clear()
    }

    fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun getCount(): Int {
        return data.size
    }

    fun updateWithFilter(filter: (T) -> Boolean, modify: (T) -> Unit) {
        data.indexOfFirst { filter(it.model) }.let {
            log("[updateWithFilter] $it")

            val page = data[it]
            modify(page.model)
            @Suppress("UNCHECKED_CAST")
            (data[it].presenter as? Updatable<T>)?.onUpdate(page.model)
        }
    }

    override fun tag(): String {
        return javaClass.simpleName + "[" + uuid.substring(0, 8) + "]"
    }

    private fun adapterPositionToString(position: Int): String = when (position) {
        POSITION_NONE -> "POSITION_NONE"
        POSITION_UNCHANGED -> "POSITION_UNCHANGED"
        else -> "UNKNOWN: $position"
    }
}