package net.kibotu.swipedirectionviewpager

import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.kibotu.logger.Logger.logv
import java.util.*


/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

open class ViewPagerPresenterAdapter<T, VM>(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    /**
     * ViewPager Page.
     */
    class Page<out T, out S>(
        val model: T, val presenter: S, val factory: () -> S,
        /** Indicates if presenter needs to be re-created. **/
        var dirty: Boolean = false, var toBeRemoved: Boolean = false
    ) where S : Fragment

    /**
     * Interface for injecting the adapter into pages.
     */
    interface ViewPagerPresenter<T, VM> : SwipeDirectionListener {
        var viewPagerPresenterAdapter: ViewPagerPresenterAdapter<T, VM>?
    }

    private val uuid = UUID.randomUUID().toString()

    /**
     * Actual data containing [T] and it's [Page] type.
     */
    private val data: MutableList<Page<T, Fragment>> = mutableListOf()

    /**
     * Shared object between view page holder and pages.
     */
    var viewPagerModel: VM? = null

    /**
     * Enables access of view pager scroll mechanics in pages.
     */
    lateinit var scrollHandler: ScrollHandler

    /**
     * Meant for pages to notify view page holder about updates.
     */
    var onPageChangeListener: Callback<Fragment>? = null

    /**
     * Meant for pages to notify view page holder that the page has completed its purpose.
     */
    var onPageCompleteListener: Callback<Fragment>? = null

    /**
     * Callback for left scroll events on first page.
     */
    var swipeLeftEdgeListener: Runnable? = null

    /**
     * Callback for right scroll events on last page.
     */
    var swipeRightEdgeListener: Runnable? = null

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

        if (enableLogging) logv("[getItemPosition] position=${adapterPositionToString(position)} item=$item")

        // POSITION_NONE means something like: this fragment is no longer valid
        // triggering the ViewPager to re-build the instance of this fragment.
        return POSITION_NONE // todo not re-creating fragments each #notifyDataSetChanged, since we have different layouts in the view pager removal seems to be tricky
    }

    /**
     * Destroys page.
     */
    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        if (enableLogging) logv("[destroyItem] container=$container position=$position item=$item")
        super.destroyItem(container, position, item)
    }

    /**
     * Adds a new page at the end.
     */
    fun <R> append(t: T, factory: () -> R) where R : Fragment, R : ViewPagerPresenterAdapter.ViewPagerPresenter<T, VM> {
        if (enableLogging) logv("[getItem] size=$count at=0 item=$t")

        data.add(Page(t, factory().apply { viewPagerPresenterAdapter = this@ViewPagerPresenterAdapter }, factory))
        data.subList(0, data.size - 1).forEach { it.dirty = true }
    }

    /**
     * Adds a new page at the beginning.
     */
    fun <R> prepend(
        t: T,
        factory: () -> R
    ) where R : Fragment, R : ViewPagerPresenterAdapter.ViewPagerPresenter<T, VM> {
        if (enableLogging) logv("[getItem] size=$count at=0 item=$t")

        data.add(0, Page(t, factory().apply { viewPagerPresenterAdapter = this@ViewPagerPresenterAdapter }, factory))
        data.drop(1).forEach { it.dirty = true }
    }

    /**
     * Note: Use [getFragment] instead. Internally used by adapter and it clears [Page.dirty] flag during page creation.
     * @return Fragment at adapter position.
     */
    override fun getItem(position: Int): Fragment {
        val page = data[position]
        if (enableLogging) logv("[getItem] position=$position ${page.dirty}")

        if (page.dirty)
            page.dirty = false

        return data[position].presenter
    }

    /**
     * @return Fragment at adapter position.
     */
    fun getFragment(position: Int): Fragment {
        return data[clamp(position, 0, data.size)].presenter
    }

    /**
     * @return Model by a given fragment.
     */
    fun model(fragment: Fragment): T? {
        return data.find { it.presenter == fragment }?.model
    }

    /**
     * @return Model by a given adapter position.
     */
    fun model(position: Int): T? {
        return data[position].model
    }

    /**
     * @return Adapter position of fragment.
     */
    fun indexOf(fragment: Fragment): Int {
        return data.indexOfFirst { it.presenter == fragment }
    }

    /**
     * @return Adapter position of fragment.
     */
    fun indexOf(t: T): Int {
        return data.indexOfFirst { it.model == t }
    }

    /**
     * Removes first found page based on a given filter.
     */
    fun removeIf(filter: (T) -> Boolean) {
        data.find { filter(it.model) }?.let {
            if (enableLogging) logv("[removeIf] $it")

            it.toBeRemoved = true
            data.remove(it)
        }
    }

    /**
     * @return true if a model is in adapter by using a filter.
     */
    fun contains(filter: (T) -> Boolean): Boolean {
        return data.find { filter(it.model) } != null
    }

    /**
     * Clears adapter.
     */
    fun clear() {
        data.clear()
    }

    /**
     * @return true if adapter is empty.
     */
    fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    /**
     * @return Current amount of elements added to adapter.
     */
    override fun getCount(): Int {
        return data.size
    }

    /**
     * Updates a page based on a filter. Requires [ViewPagerPresenter] to inherit from [Updatable] as well.
     */
    fun update(t: T, filter: (T) -> Boolean) {
        data.indexOfFirst { filter(it.model) }.let {
            if (enableLogging) logv("[update] $it $t")

            data[it] = Page(t, data[it].presenter, data[it].factory)
            @Suppress("UNCHECKED_CAST")
            (data[it].presenter as? Updatable<T>)?.onUpdate(t)
        }
    }

    /**
     * Updates a page at a given position.
     */
    fun update(t: T, position: Int) {
        if (enableLogging) logv("[update] $t at $position")
        data[position] = Page(t, data[position].presenter, data[position].factory)
        @Suppress("UNCHECKED_CAST")
        (data[position].presenter as? Updatable<T>)?.onUpdate(t)
    }

    /**
     * Updates a page model based on a filter. Requires [ViewPagerPresenter] to inherit from [Updatable] as well.
     */
    fun updateInPlace(filter: (T) -> Boolean, modify: (T) -> Unit) {
        data.indexOfFirst { filter(it.model) }.let {
            if (enableLogging) logv("[updateInPlace] $it")

            val page = data[it]
            modify(page.model)
            @Suppress("UNCHECKED_CAST")
            (data[it].presenter as? Updatable<T>)?.onUpdate(page.model)
        }
    }

    private fun adapterPositionToString(position: Int): String = when (position) {
        POSITION_NONE -> "POSITION_NONE"
        POSITION_UNCHANGED -> "POSITION_UNCHANGED"
        else -> "UNKNOWN: $position"
    }
}