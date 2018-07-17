package com.afollestad.aestheticsample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.BottomNavBgMode;
import com.afollestad.aesthetic.BottomNavIconTextMode;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/** @author Aidan Follestad (afollestad) */
public class MainFragment extends Fragment {

  @BindView(R.id.root)
  FrameLayout rootView;

  @BindView(R.id.switch_theme)
  SwitchCompat switchThemeView;

  @BindView(R.id.spinner)
  Spinner spinnerView;

  private Snackbar snackbar;
  private Unbinder unbinder;
  private Disposable isDarkSubscription;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);

    // Update the dark theme switch to the last saved isDark value.
    isDarkSubscription =
        Aesthetic.get()
            .isDark()
            .subscribe(
                new Consumer<Boolean>() {
                  @Override
                  public void accept(@NonNull Boolean isDark) {
                    switchThemeView.setChecked(isDark);
                  }
                });

    // Further view setup
    ArrayAdapter<String> spinnerAdapter =
        new ArrayAdapter<>(
            getContext(),
            R.layout.list_item_spinner,
            new String[] {
                "Spinner One",
                "Spinner Two",
                "Spinner Three",
                "Spinner Four",
                "Spinner Five",
                "Spinner Six"
            });
    spinnerAdapter.setDropDownViewResource(R.layout.list_item_spinner_dropdown);
    spinnerView.setAdapter(spinnerAdapter);
  }

  @Override
  public void onDestroyView() {
    isDarkSubscription.dispose();
    unbinder.unbind();
    super.onDestroyView();
  }

  @OnClick(R.id.switch_theme)
  public void onThemeChange(SwitchCompat switchCompat) {
    if (switchCompat.isChecked()) {
      Aesthetic.get()
          .activityTheme(R.style.AppThemeDark)
          .isDark(true)
          .textColorPrimaryRes(R.color.text_color_primary_dark)
          .textColorSecondaryRes(R.color.text_color_secondary_dark)
          .apply();
    } else {
      Aesthetic.get()
          .activityTheme(R.style.AppTheme)
          .isDark(false)
          .textColorPrimaryRes(R.color.text_color_primary)
          .textColorSecondaryRes(R.color.text_color_secondary)
          .apply();
    }
  }

  @OnClick(R.id.btn_dialog)
  public void showMaterialDialog() {
    new AlertDialog.Builder(getActivity())
        .setTitle(R.string.hello_world)
        .setMessage(R.string.lorem_ipsum)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {

          }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {

          }
        })
        .show();
  }

  @OnClick({
      R.id.btn_black,
      R.id.btn_red,
      R.id.btn_purple,
      R.id.btn_blue,
      R.id.btn_green,
      R.id.btn_white
  })
  public void onClickButton(View view) {
    switch (view.getId()) {
      case R.id.btn_black:
        Aesthetic.get()
            .colorPrimaryRes(R.color.text_color_primary)
            .colorAccentRes(R.color.md_purple)
            .colorStatusBarAuto()
            .colorNavigationBarAuto()
            .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
            .bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
            .apply();
        break;
      case R.id.btn_red:
        Aesthetic.get()
            .colorPrimaryRes(R.color.md_red)
            .colorAccentRes(R.color.md_amber)
            .colorStatusBarAuto()
            .colorNavigationBarAuto()
            .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
            .bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
            .apply();
        break;
      case R.id.btn_purple:
        Aesthetic.get()
            .colorPrimaryRes(R.color.md_purple)
            .colorAccentRes(R.color.md_lime)
            .colorStatusBarAuto()
            .colorNavigationBarAuto()
            .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
            .bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
            .apply();
        break;
      case R.id.btn_blue:
        Aesthetic.get()
            .colorPrimaryRes(R.color.md_blue)
            .colorAccentRes(R.color.md_pink)
            .colorStatusBarAuto()
            .colorNavigationBarAuto()
            .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
            .bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
            .apply();
        break;
      case R.id.btn_green:
        Aesthetic.get()
            .colorPrimaryRes(R.color.md_green)
            .colorAccentRes(R.color.md_blue_grey)
            .colorStatusBarAuto()
            .colorNavigationBarAuto()
            .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
            .bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
            .apply();
        break;
      case R.id.btn_white:
        Aesthetic.get()
            .colorPrimaryRes(R.color.md_white)
            .colorAccentRes(R.color.md_blue)
            .colorStatusBarAuto()
            .colorNavigationBarAuto()
            .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
            .bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
            .apply();
        break;
    }
  }

  @OnClick(R.id.fab)
  public void onClickFab() {
    if (snackbar != null) {
      snackbar.dismiss();
    }
    snackbar = Snackbar.make(rootView, R.string.hello_world, Snackbar.LENGTH_LONG);
    snackbar.setAction(
        android.R.string.cancel,
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            // no-op
          }
        });
    snackbar.show();
  }
}
