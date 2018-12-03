/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.aestheticsample.appcompat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.aestheticsample.appcompat.MainAdapter.ViewHolder
import kotlinx.android.synthetic.main.list_item_rv.view.subtitle
import kotlinx.android.synthetic.main.list_item_rv.view.title

/** @author Aidan Follestad (afollestad) */
internal class MainAdapter : RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.list_item_rv, parent, false)
    return ViewHolder(view)
  }

  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    holder.itemView.title.text = "Item #$position"
    holder.itemView.subtitle.setText(R.string.hello_world)
  }

  override fun getItemCount(): Int {
    return 20
  }

  internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
