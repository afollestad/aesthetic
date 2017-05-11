package com.afollestad.aestheticsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

  private Unbinder unbinder;

  @BindView(R.id.switch_theme)
  SwitchCompat switchThemeView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Aesthetic.attach(this);
    Log.d("MainActivity", "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);

    //noinspection ConstantConditions
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    Aesthetic.get().isDark().take(1).subscribe(isDark -> switchThemeView.setChecked(isDark));
  }

  @OnClick(R.id.switch_theme)
  public void onThemeChange(SwitchCompat switchCompat) {
    if (switchCompat.isChecked()) {
      Aesthetic.get().activityTheme(R.style.AppThemeDark).apply();
    } else {
      Aesthetic.get().activityTheme(R.style.AppTheme).apply();
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
  protected void onResume() {
    super.onResume();
    Aesthetic.resume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    Aesthetic.pause();
  }

  @Override
  protected void onDestroy() {
    Aesthetic.destroy();
    unbinder.unbind();
    super.onDestroy();
  }
}
