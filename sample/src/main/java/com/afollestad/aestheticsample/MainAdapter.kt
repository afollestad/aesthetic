package com.afollestad.aestheticsample

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_rv.view.subtitle
import kotlinx.android.synthetic.main.list_item_rv.view.title

/** @author Aidan Follestad (afollestad) */
internal class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

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
