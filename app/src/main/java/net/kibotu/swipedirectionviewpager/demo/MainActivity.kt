package net.kibotu.swipedirectionviewpager.demo

import android.database.DataSetObserver
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.swipedirectionviewpager.Callback
import net.kibotu.swipedirectionviewpager.ViewPagerPresenterAdapter
import net.kibotu.swipedirectionviewpager.demo.models.PageModel
import net.kibotu.swipedirectionviewpager.demo.models.ViewPagerModel
import net.kibotu.swipedirectionviewpager.demo.pages.ImagePage
import net.kibotu.swipedirectionviewpager.demo.pages.TextPage

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ViewPagerPresenterAdapter<PageModel, ViewPagerModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAdapter()
        initViewPager()
        initCircleIndicator()

        addPages()

        adapter.updateInPlace(filter = { it.text == "Page 3" }, modify = { it.text = "updated model in place by filter" })
        adapter.update(PageModel(text = "update replaced model by filter"), { it.text == "Page 2" })
        adapter.update(PageModel(text = "updated model at position 0"), 0)
    }

    private fun addPages() {

        (0 until 5).forEach {
            adapter.append(PageModel(text = "Page $it")) { TextPage() }
            adapter.append(PageModel(imageUrl = createRandomImageUrl())) { ImagePage() }
        }
        adapter.notifyDataSetChanged()
    }

    private fun initAdapter() {
        adapter = ViewPagerPresenterAdapter(supportFragmentManager)

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

    private fun initViewPager() {
        viewPager.adapter = adapter
    }

    private fun initCircleIndicator() {
        circleIndicator.attachToViewPager(viewPager)
        updateCircleIndicatorVisibility()
    }

    private fun updateCircleIndicatorVisibility() {
        circleIndicator.visibility = if (adapter.count > 1) View.VISIBLE else View.GONE
    }
}
