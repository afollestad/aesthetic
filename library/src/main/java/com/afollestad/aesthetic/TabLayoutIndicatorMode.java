package com.afollestad.aesthetic;

/** @author Aidan Follestad (afollestad) */
public enum TabLayoutIndicatorMode {
  PRIMARY(0),
  ACCENT(1);

  private final int value;

  TabLayoutIndicatorMode(int value) {
    this.value = value;
  }

  public int toInt() {
    return value;
  }

  public static TabLayoutIndicatorMode fromInt(int value) {
    switch (value) {
      case 0:
        return PRIMARY;
      default:
        return ACCENT;
    }
  }
}
