[![Donation](https://img.shields.io/badge/buy%20me%20a%20coffee-brightgreen.svg)](https://www.paypal.me/janrabe/5) [![About Jan Rabe](https://img.shields.io/badge/about-me-green.svg)](https://about.me/janrabe)
# SwipeDirectionViewPager [![](https://jitpack.io/v/kibotu/SwipeDirectionViewPager.svg)](https://jitpack.io/#kibotu/SwipeDirectionViewPager) [![](https://jitpack.io/v/kibotu/SwipeDirectionViewPager/month.svg)](https://jitpack.io/#kibotu/SwipeDirectionViewPager) [![Javadoc](https://img.shields.io/badge/javadoc-SNAPSHOT-green.svg)](https://jitpack.io/com/github/kibotu/SwipeDirectionViewPager/master-SNAPSHOT/javadoc/index.html) [![Build Status](https://travis-ci.org/kibotu/SwipeDirectionViewPager.svg?branch=master)](https://travis-ci.org/kibotu/SwipeDirectionViewPager) [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)  [![Gradle Version](https://img.shields.io/badge/gradle-5.3.1-green.svg)](https://docs.gradle.org/current/release-notes) [![Kotlin](https://img.shields.io/badge/kotlin-1.3.30-green.svg)](https://kotlinlang.org/) [![Licence](https://img.shields.io/badge/licence-Apache%202-blue.svg)](https://raw.githubusercontent.com/kibotu/SwipeDirectionViewPager/master/LICENSE) [![androidx](https://img.shields.io/badge/androidx-brightgreen.svg)](https://developer.android.com/topic/libraries/support-library/refactor)

## Introduction

Custom ViewPager that allows to block swiping right or left where the ViewPager child fragments set the scroll direction and handle blocked swipe events. The library also comes with an adapter that can handle different view pages views and can control the view pager scrolling from within view pager pages.

![Screenshot](https://raw.githubusercontent.com/kibotu/SwipeDirectionViewPager/master/screenshot.jpeg) ![Screenshot](https://raw.githubusercontent.com/kibotu/SwipeDirectionViewPager/master/screenshot2.png)

## How to install

    repositories {
        maven {
            url "https://jitpack.io"
        }
    }

    dependencies {
        implementation 'com.github.kibotu:SwipeDirectionViewPager:-SNAPSHOT'
    }

## How to use

### Basic Setup

1 Create adapter

    // use Fragment#childFragmentManager in nested fragments
    var adapter: ViewPagerPresenterAdapter<PageModel, ViewPagerModel> = ViewPagerPresenterAdapter(supportFragmentManager)

2 add to view pager

    viewPager.adapter = adapter

3 add listeners

    adapter.swipeLeftEdgeListener = Runnable {
        snackbar(this, "has swiped left on first page")
    }

    adapter.swipeRightEdgeListener = Runnable {
        snackbar(this, "has swiped right on last page")
    }

4 add view pager fragments

    (0 until 5).forEach {
        adapter.append(PageModel(text = "Page $it")) { TextPage() }
        adapter.append(PageModel(imageUrl = createRandomImageUrl())) { ImagePage() }
    }

    // don't forget to notify updates
    adapter.notifyDataSetChanged()

5 implement ViewPagerPresenterAdapter.ViewPagerPresenter<PageModel, ViewPagerModel> interface in your view pager fragments, e.g.:

    class Page : Fragment(), ViewPagerPresenterAdapter.ViewPagerPresenter<PageModel, ViewPagerModel> {

        /**
         * into pages injected adapter
         */
        override var viewPagerPresenterAdapter: ViewPagerPresenterAdapter<PageModel, ViewPagerModel>? = null

        /**
         * gets called when allowSwipeDirection is set and user scrolls to the previous or next page.
         * basically, you set 'only swipe left' and when user swipes right, you can give the user feedback
         */
        override fun onSwipeIntercepted(direction: SwipeDirection){
        }

        /**
         * set page specific swipe allowed direction
         */
        override fun allowSwipeDirection(): SwipeDirection {
            return SwipeDirection.ALL
        }
    }

### Inserting fragments

Adding a new page to the end

    adapter.append(PageModel(text = "Page $it")) { TextPage() }

Adding a new page to the beginning

    adapter.prepend(PageModel(text = "Page $it")) { TextPage() }

### Getting fragments

    var fragment = viewPagerPresenterAdapter?.getFragment(adapterPosition); // don't use #getItemPosition, it interferes with fragment updates

### Getting models

using adapter position

    var model = viewPagerPresenterAdapter?.model(adapterPosition);

using a fragment

    var model = viewPagerPresenterAdapter?.model(fragment);

### Getting adapter position

Note: neither models nor fragments are unique, it's returning simply the first one found

using a fragment

    var position = viewPagerPresenterAdapter?.indexOf(fragment);

using a model

    var position = viewPagerPresenterAdapter?.indexOf(model);

### Update fragments

ViewPager fragments need to inherit form [Updatable]. the #update(t: T) gets called in order to update fragments without the need to recreate them. (which would be the case using #notifyDataSetChanged)

replace model at given position

    viewPagerPresenterAdapter?.update(PageModel(text = "updated model at position 0"), 0)

replacing a model at given criteria

    viewPagerPresenterAdapter?.update(PageModel(text = "update replaced model by filter"), { it.text == "Page 2" })

modifying model data in place

    viewPagerPresenterAdapter?.updateInPlace(filter = { it.text == "Page 3" }, modify = { it.text = "updated model in place by filter" })

### Misc

PageModel - can be any data model, which can be bound to view pager page and can be accessed inside pages.

    var model : PageModel = viewPagerPresenterAdapter?.model(fragment)

    // or

    var model : PageModel = viewPagerPresenterAdapter?.model(adapterPosition)

ViewPagerModel - meant to be a shared object between all pages and its parent view pager holder

    var viewPagerModel : ViewPagerModel = viewPagerPresenterAdapter?.viewPagerModel

### ScrollHandler

Allows view pager pages to control view pager scrolls.


    viewPagerPresenterAdapter.scrollHandler

    /**
     * Skips a given amount of pages.
     */
    fun skip(amount: Int, smooth: Boolean = true)

    /**
     * Scrolls to a given position.
     */
    fun scrollTo(position: Int, smooth: Boolean = true)

    /**
     * Scrolls to next page.
     */
    fun scrollToNextPage()

    /**
     * Scrolls to previous page.
     */
    fun scrollToPreviousPage()

    /**
     * @return true if current page is first.
     */
    fun isFirstPage(): Boolean

    /**
     * @return true if current page is last.
     */
    fun isLastPage(): Boolean

## RTL Support by using

net.kibotu.swipedirectionviewpager.SwipeDirectionViewPagerRtl, which uses duolingo's RtlViewPager

    implementation "com.android.support:support-core-ui:$support_version"
    implementation 'com.duolingo.open:rtl-viewpager:1.0.3'

## How to build

    graldew clean build

### CI

    gradlew clean assembleRelease test javadoc

#### Build Requirements

- JDK8
- Android Build Tools 27.0.2
- Android SDK 27

## Contributors

- [Jan Rabe](jan.rabe@kibotu.net)

### License

<pre>
Copyright 2018 Jan Rabe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
