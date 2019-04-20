/**
 *
 * Copyright 2019 Alejandro Rosas
 * Copyright 2017 Harish Sridharan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cooltechworks.views.shimmer

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShimmerRecyclerView : RecyclerView {

    /**
     * Retrieves the actual adapter that contains the data set or null if no adapter is set.
     *
     * @return The actual adapter
     */
    var actualAdapter: Adapter<*>? = null
        private set

    private var mShimmerLayoutManager: LayoutManager? = null
    private var mActualLayoutManager: LayoutManager? = null
    private lateinit var mLayoutMangerType: LayoutMangerType

    private var mCanScroll: Boolean = false
    private var layoutReference: Int = 0
    private var mGridCount: Int = 0

    lateinit var shimmerAdapter: ShimmerAdapter

    enum class LayoutMangerType {
        LINEAR_VERTICAL, LINEAR_HORIZONTAL, GRID
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        shimmerAdapter = ShimmerAdapter()
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerRecyclerView, 0, 0)

        try {
            setDemoLayoutReference(a.getResourceId(R.styleable.ShimmerRecyclerView_shimmer_demo_layout, R.layout.layout_sample_view))
            setDemoChildCount(a.getInteger(R.styleable.ShimmerRecyclerView_shimmer_demo_child_count, 10))
            setGridChildCount(a.getInteger(R.styleable.ShimmerRecyclerView_shimmer_demo_grid_child_count, 2))

            when (a.getInteger(R.styleable.ShimmerRecyclerView_shimmer_demo_layout_manager_type, 0)) {
                0 -> setDemoLayoutManager(LayoutMangerType.LINEAR_VERTICAL)
                1 -> setDemoLayoutManager(LayoutMangerType.LINEAR_HORIZONTAL)
                2 -> setDemoLayoutManager(LayoutMangerType.GRID)
                else -> throw IllegalArgumentException("This value for layout manager is not valid!")
            }

            shimmerAdapter.apply {
                shimmerAngle = a.getInteger(R.styleable.ShimmerRecyclerView_shimmer_demo_angle, 0)
                shimmerColor = a.getColor(R.styleable.ShimmerRecyclerView_shimmer_demo_shimmer_color, getColor(R.color.default_shimmer_color))
                maskWidth = a.getFloat(R.styleable.ShimmerRecyclerView_shimmer_demo_mask_width, 0.5f)
                shimmerItemBackground = a.getDrawable(R.styleable.ShimmerRecyclerView_shimmer_demo_view_holder_item_background)
                shimmerDuration = a.getInteger(R.styleable.ShimmerRecyclerView_shimmer_demo_duration, 1500)
                isAnimationReversed = a.getBoolean(R.styleable.ShimmerRecyclerView_shimmer_demo_reverse_animation, false)
            }
        } finally {
            a.recycle()
        }

        showShimmerAdapter()
    }

    /**
     * Specifies the number of child should exist in any row of the grid layout.
     *
     * @param count - count specifying the number of child.
     */
    fun setGridChildCount(count: Int) {
        mGridCount = count
    }

    /**
     * Sets the layout manager for the shimmer adapter.
     *
     * @param type layout manager reference
     */
    fun setDemoLayoutManager(type: LayoutMangerType) {
        mLayoutMangerType = type
    }

    /**
     * Sets the number of demo views should be shown in the shimmer adapter.
     *
     * @param count - number of demo views should be shown.
     */
    fun setDemoChildCount(count: Int) {
        shimmerAdapter.minItemCount = count
    }

    /**
     * Specifies the animation duration of shimmer layout.
     *
     * @param duration - count specifying the duration of shimmer in millisecond.
     */
    fun setDemoShimmerDuration(duration: Int) {
        shimmerAdapter.shimmerDuration = duration
    }

    /**
     * Specifies the the width of the shimmer line.
     *
     * @param maskWidth - float specifying the width of shimmer line. The value should be from 0 to less or equal to 1.
     * The default value is 0.5.
     */
    fun setDemoShimmerMaskWidth(maskWidth: Float) {
        shimmerAdapter.maskWidth = maskWidth
    }

    /**
     * Sets the shimmer adapter and shows the loading screen.
     */
    fun showShimmerAdapter() {
        mCanScroll = false

        if (mShimmerLayoutManager == null) {
            initShimmerManager()
        }

        layoutManager = mShimmerLayoutManager
        adapter = shimmerAdapter
    }

    /**
     * Hides the shimmer adapter
     */
    fun hideShimmerAdapter() {
        mCanScroll = true
        layoutManager = mActualLayoutManager
        adapter = actualAdapter
    }

    override fun setLayoutManager(manager: LayoutManager?) {
        if (manager == null) {
            mActualLayoutManager = null
        } else if (manager !== mShimmerLayoutManager) {
            mActualLayoutManager = manager
        }

        super.setLayoutManager(manager)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter == null) {
            actualAdapter = null
        } else if (adapter !== shimmerAdapter) {
            actualAdapter = adapter
        }

        super.setAdapter(adapter)
    }

    /**
     * Sets the demo layout reference
     *
     * @param mLayoutReference layout resource id of the layout which should be shown as demo.
     */
    fun setDemoLayoutReference(mLayoutReference: Int) {
        layoutReference = mLayoutReference
        shimmerAdapter.layoutReference = layoutReference
    }

    private fun initShimmerManager() {
        mShimmerLayoutManager = when (mLayoutMangerType) {
            LayoutMangerType.LINEAR_VERTICAL -> object : LinearLayoutManager(context) {
                override fun canScrollVertically() = mCanScroll
            }
            LayoutMangerType.LINEAR_HORIZONTAL -> object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun canScrollHorizontally() = mCanScroll
            }
            LayoutMangerType.GRID -> object : GridLayoutManager(context, mGridCount) {
                override fun canScrollVertically() = mCanScroll
            }
        }
    }

    private fun getColor(id: Int): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(id)
        } else {
            @Suppress("DEPRECATION")
            resources.getColor(id)
        }
}
