package com.afollestad.aestheticsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.afollestad.aesthetic.AestheticActivity;

/** @author Aidan Follestad (afollestad) */
public class RecyclerViewActivity extends AestheticActivity {

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private Unbinder unbinder;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recyclerview);
    unbinder = ButterKnife.bind(this);

    toolbar.setNavigationOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            finish();
          }
        });
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new MainAdapter());
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}
