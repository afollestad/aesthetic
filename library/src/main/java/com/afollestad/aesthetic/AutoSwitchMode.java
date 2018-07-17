package com.afollestad.aesthetic;

/** @author Aidan Follestad (afollestad) */
public enum AutoSwitchMode {
  OFF(0),
  ON(1),
  AUTO(2);

  private final int value;

  AutoSwitchMode(int value) {
    this.value = value;
  }

  public int toInt() {
    return value;
  }

  public static AutoSwitchMode fromInt(int value) {
    switch (value) {
      case 0:
        return OFF;
      case 1:
        return ON;
      default:
        return AUTO;
    }
  }
}
