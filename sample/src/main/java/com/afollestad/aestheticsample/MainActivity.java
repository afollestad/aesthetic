package com.afollestad.aestheticsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AestheticActivity {

  private Unbinder unbinder;

  @BindView(R.id.switch_theme)
  SwitchCompat switchThemeView;

  @BindView(R.id.spinner)
  Spinner spinnerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);

    //noinspection ConstantConditions
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // If we haven't set any defaults, do that now
    if (Aesthetic.isFirstTime()) {
      Aesthetic.get()
          .activityTheme(R.style.AppTheme)
          .primaryTextColorRes(R.color.text_color_primary)
          .secondaryTextColorRes(R.color.text_color_secondary)
          .primaryColorRes(R.color.md_white)
          .accentColorRes(R.color.md_blue)
          .statusBarColorAuto()
          .navBarColorAuto()
          .primaryTextColor(Color.BLACK)
          .apply();
    }

    // Update the dark theme switch to the last saved isDark value.
    Aesthetic.get().isDark().take(1).subscribe(isDark -> switchThemeView.setChecked(isDark));

    // Further view setup
    ArrayAdapter<String> spinnerAdapter =
        new ArrayAdapter<>(
            this,
            R.layout.list_item_spinner,
            new String[]{"One", "Two", "Three", "Four", "Five", "Six"});
    spinnerAdapter.setDropDownViewResource(R.layout.list_item_spinner_dropdown);
    spinnerView.setAdapter(spinnerAdapter);
  }

  @OnClick(R.id.switch_theme)
  public void onThemeChange(SwitchCompat switchCompat) {
    if (switchCompat.isChecked()) {
      Aesthetic.get().activityTheme(R.style.AppThemeDark).isDark(true).apply();
    } else {
      Aesthetic.get().activityTheme(R.style.AppTheme).isDark(false).apply();
    }
  }

  @OnClick({R.id.btn_red, R.id.btn_purple, R.id.btn_blue, R.id.btn_green, R.id.btn_white})
  public void onClickButton(View view) {
    switch (view.getId()) {
      case R.id.btn_red:
        Aesthetic.get()
            .primaryColorRes(R.color.md_red)
            .accentColorRes(R.color.md_blue_grey)
            .statusBarColorAuto()
            .navBarColorAuto()
            .apply();
        break;
      case R.id.btn_purple:
        Aesthetic.get()
            .primaryColorRes(R.color.md_purple)
            .accentColorRes(R.color.md_lime)
            .statusBarColorAuto()
            .navBarColorAuto()
            .apply();
        break;
      case R.id.btn_blue:
        Aesthetic.get()
            .primaryColorRes(R.color.md_blue)
            .accentColorRes(R.color.md_pink)
            .statusBarColorAuto()
            .navBarColorAuto()
            .apply();
        break;
      case R.id.btn_green:
        Aesthetic.get()
            .primaryColorRes(R.color.md_green)
            .accentColorRes(R.color.md_blue_grey)
            .statusBarColorAuto()
            .navBarColorAuto()
            .apply();
        break;
      case R.id.btn_white:
        Aesthetic.get()
            .primaryColorRes(R.color.md_white)
            .accentColorRes(R.color.md_blue)
            .statusBarColorAuto()
            .navBarColorAuto()
            .apply();
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.info) {
      Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}
