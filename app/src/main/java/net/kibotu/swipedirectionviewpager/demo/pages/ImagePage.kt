package net.kibotu.swipedirectionviewpager.demo.pages

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy.NONE
import com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf
import kotlinx.android.synthetic.main.fragment_image_page.*
import net.kibotu.swipedirectionviewpager.demo.R

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

class ImagePage : Page() {

    override val layout: Int = R.layout.fragment_image_page

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerPresenterAdapter
                ?.model(this)
                ?.imageUrl
                ?.let {

                    Glide.with(view.context)
                            .load(it)
                            .apply(diskCacheStrategyOf(NONE))
                            .into(content)
                }
    }
}