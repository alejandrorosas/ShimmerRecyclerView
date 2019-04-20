/**
 *
 * Copyright 2019 Alejandro Rosas
 * Copyright 2017 Harish Sridharan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cooltechworks.views.shimmer

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ShimmerAdapter : RecyclerView.Adapter<ShimmerViewHolder>() {

    var shimmerAngle: Int? = null
    var shimmerColor: Int? = null
    var minItemCount = 0
    var maskWidth: Float? = null
    var shimmerItemBackground: Drawable? = null
    var shimmerDuration: Int? = null
    var layoutReference: Int = 0

    var isAnimationReversed = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ShimmerViewHolder(LayoutInflater.from(parent.context), parent, layoutReference).apply {
            shimmerColor?.let { setShimmerColor(it) }
            shimmerAngle?.let { setShimmerAngle(it) }
            maskWidth?.let { setShimmerMaskWidth(it) }
            setShimmerViewHolderBackground(shimmerItemBackground)
            shimmerDuration?.let { setShimmerAnimationDuration(it) }
            setAnimationReversed(isAnimationReversed)
        }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) = holder.bind()

    override fun getItemCount(): Int = minItemCount
}
