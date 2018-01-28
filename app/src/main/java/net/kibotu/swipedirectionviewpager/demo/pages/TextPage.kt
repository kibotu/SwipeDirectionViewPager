package net.kibotu.swipedirectionviewpager.demo.pages

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_text_page.*
import net.kibotu.swipedirectionviewpager.demo.R

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

class TextPage : Page() {

    override val layout: Int = R.layout.fragment_text_page

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        content.text = viewPagerPresenterAdapter?.model(this)?.text
    }
}