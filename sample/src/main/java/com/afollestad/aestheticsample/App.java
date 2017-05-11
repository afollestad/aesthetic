package com.afollestad.aestheticsample;

import android.app.Application;

import com.afollestad.aesthetic.Aesthetic;

public class App extends Application {

  @Override
  public void onTerminate() {
    Aesthetic.destroy();
    super.onTerminate();
  }
}
