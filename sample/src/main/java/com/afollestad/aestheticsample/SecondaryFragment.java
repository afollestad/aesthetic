package com.afollestad.aestheticsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/** @author Aidan Follestad (afollestad) */
public class SecondaryFragment extends Fragment {

  private Unbinder unbinder;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_secondary, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);
  }

  @Override
  public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
  }

  @OnClick(R.id.drawer_layout)
  public void onClickDrawerLayout() {
    startActivity(new Intent(getActivity(), DrawerActivity.class));
  }

  @OnClick(R.id.coordinator_layout)
  public void onClickCoordinatorlayout() {
    startActivity(new Intent(getActivity(), CoordinatorLayoutActivity.class));
  }

  @OnClick(R.id.bottom_tabs)
  public void onClickBottomTabs() {
    startActivity(new Intent(getActivity(), BottomNavActivity.class));
  }

  @OnClick(R.id.recycler_view)
  public void onClickRecyclerView() {
    startActivity(new Intent(getActivity(), RecyclerViewActivity.class));
  }
}
