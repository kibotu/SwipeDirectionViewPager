package net.kibotu.swipedirectionviewpager.demo

import android.database.DataSetObserver
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.swipedirectionviewpager.Callback
import net.kibotu.swipedirectionviewpager.ViewPagerPresenterAdapter
import net.kibotu.swipedirectionviewpager.demo.models.PageModel
import net.kibotu.swipedirectionviewpager.demo.models.ViewPagerModel
import net.kibotu.swipedirectionviewpager.demo.pages.TextPage


/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

class MainActivity : AppCompatActivity() {

    private lateinit var swipeAdapterLtr: ViewPagerPresenterAdapter<PageModel, ViewPagerModel>
    private lateinit var swipeAdapterRtl: ViewPagerPresenterAdapter<PageModel, ViewPagerModel>
    private lateinit var adapterLtr: TextViewPagerAdapter
    private lateinit var adapterRtl: TextViewPagerAdapter

    var urls: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urls = (0 until 5).map { createRandomImageUrl() }

        initAdapterSwipeLtr()
        initViewPagerSwipeLtr()
        initCircleIndicatorSwipeLtr()

        initAdapterSwipeRtl()
        initViewPagerSwipeRtl()
        initCircleIndicatorSwipeRtl()

        initAdapterLtr()
        initViewPagerLtr()
        initCircleIndicatorLtr()

        initAdapterRtl()
        initViewPagerRtl()
        initCircleIndicatorRtl()

        swipeViewPagerLtr.post {
            addPages(swipeAdapterLtr)
        }

        swipeViewPagerRtl.post {
            addPages(swipeAdapterRtl)
            swipeViewPagerRtl.currentItem = swipeAdapterLtr.count - 1
        }

        updateCircleIndicatorVisibility()

//        swipeAdapter.updateInPlace(filter = { it.text == "Page 3" }, modify = { it.text = "updated model in place by filter" })
//        swipeAdapter.update(PageModel(text = "update replaced model by filter"), { it.text == "Page 2" })
//        swipeAdapter.update(PageModel(text = "updated model at position 0"), 0)

    }

    private fun initAdapterSwipeLtr() {
        swipeAdapterLtr = ViewPagerPresenterAdapter(supportFragmentManager)
        initAdapter(swipeAdapterLtr)
    }

    private fun initViewPagerSwipeLtr() {
        swipeViewPagerLtr.adapter = swipeAdapterLtr
    }

    private fun initCircleIndicatorSwipeLtr() {
        swipeCircleIndicatorLtr.attachToViewPager(swipeViewPagerLtr)
    }


    private fun initAdapterSwipeRtl() {
        swipeAdapterRtl = ViewPagerPresenterAdapter(supportFragmentManager)
        initAdapter(swipeAdapterRtl)

    }

    private fun initViewPagerSwipeRtl() {
        swipeViewPagerRtl.adapter = swipeAdapterRtl
    }

    private fun initCircleIndicatorSwipeRtl() {
        swipeCircleIndicatorRtl.attachToViewPager(swipeViewPagerRtl)
    }


    private fun initAdapterLtr() {
        adapterLtr = TextViewPagerAdapter(5)
    }

    private fun initViewPagerLtr() {
        viewPagerLtr.adapter = adapterLtr
    }

    private fun initCircleIndicatorLtr() {
        circleIndicatorLtr.attachToViewPager(viewPagerLtr)
    }


    private fun initAdapterRtl() {
        adapterRtl = TextViewPagerAdapter(5)
    }

    private fun initViewPagerRtl() {
        viewPagerRtl.adapter = adapterRtl
    }

    private fun initCircleIndicatorRtl() {
        circleIndicatorRtl.attachToViewPager(viewPagerRtl)
    }


    private fun updateCircleIndicatorVisibility() {
        swipeCircleIndicatorLtr.visibility = if (swipeAdapterLtr.count > 1) View.VISIBLE else View.GONE
        swipeCircleIndicatorRtl.visibility = if (swipeAdapterRtl.count > 1) View.VISIBLE else View.GONE
        circleIndicatorLtr.visibility = if (adapterLtr.count > 1) View.VISIBLE else View.GONE
        circleIndicatorRtl.visibility = if (adapterRtl.count > 1) View.VISIBLE else View.GONE
    }

    class TextViewPagerAdapter(private val pages: Int) : PagerAdapter() {

        override fun getCount(): Int {
            return pages
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item = "Page $position"
            val text = TextView(container.context)
            text.gravity = Gravity.CENTER
            text.setBackgroundColor(Color.WHITE)
            text.setTextColor(Color.BLACK)
            text.textSize = 20f
            text.text = item
            container.addView(text, MATCH_PARENT, MATCH_PARENT)
            text.tag = item
            return item
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return position.toString()
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return `object` == view.tag
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(container.findViewWithTag(`object`))
        }
    }

    private fun addPages(adapter: ViewPagerPresenterAdapter<PageModel, ViewPagerModel>) {


        val rtl = adapter.scrollHandler.isRtl()
        Log.v("rtl=", "isRtl = $rtl")

        (0 until 5).forEach {

            if (rtl)
//                adapter.append(PageModel(imageUrl = urls!![it])) { ImagePage() }
                adapter.prepend(PageModel(text = "Page $it")) { TextPage() }
            else
//                adapter.prepend(PageModel(imageUrl = urls!![it])) { ImagePage() }
                adapter.append(PageModel(text = "Page $it")) { TextPage() }

            // adapter.append(PageModel(text = "Page $it")) { TextPage() }
            // adapter.append(PageModel(imageUrl = createRandomImageUrl())) { ImagePage() }
        }
        adapter.notifyDataSetChanged()
    }

    private fun initAdapter(adapter: ViewPagerPresenterAdapter<PageModel, ViewPagerModel>) {
        adapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                updateCircleIndicatorVisibility()
            }
        })

        adapter.onPageChangeListener = object : Callback<Fragment> {
            override fun update(t: Fragment) {
                // page has been updated
            }
        }

        adapter.onPageCompleteListener = object : Callback<Fragment> {
            override fun update(t: Fragment) {
                // page has been completed
            }
        }

        // set shared object between all pages and view page holder
        adapter.viewPagerModel = ViewPagerModel()

        adapter.swipeLeftEdgeListener = Runnable {
            snackbar(this, "has swiped left on first page")
        }

        adapter.swipeRightEdgeListener = Runnable {
            snackbar(this, "has swiped right on last page")
        }
    }
}
