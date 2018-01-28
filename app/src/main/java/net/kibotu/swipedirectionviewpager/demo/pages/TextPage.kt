package net.kibotu.swipedirectionviewpager.demo.pages

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_text_page.*
import net.kibotu.swipedirectionviewpager.Updatable
import net.kibotu.swipedirectionviewpager.demo.R
import net.kibotu.swipedirectionviewpager.demo.models.PageModel

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

class TextPage : Page(), Updatable<PageModel> {

    override val layout: Int = R.layout.fragment_text_page

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        content.text = viewPagerPresenterAdapter?.model(this)?.text
    }

    override fun onUpdate(t: PageModel) {
        content?.text = t.text
    }
}