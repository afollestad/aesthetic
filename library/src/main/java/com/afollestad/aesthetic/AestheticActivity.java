package com.afollestad.aesthetic;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class AestheticActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    Aesthetic.attach(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Aesthetic.resume(this);
  }

  @Override
  protected void onPause() {
    Aesthetic.pause();
    super.onPause();
  }
}
