package com.afollestad.aestheticsample;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/** @author Aidan Follestad (afollestad) */
class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rv, parent, false);
    return new ViewHolder(view);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.title.setText("Item #" + position);
    holder.subtitle.setText(R.string.hello_world);
  }

  @Override
  public int getItemCount() {
    return 20;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.subtitle)
    TextView subtitle;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
