# Aesthetic

Aesthetic is an easy to use, fast, Rx-powered theme engine for Android applications.

[ ![jCenter](https://api.bintray.com/packages/drummer-aidan/maven/aesthetic/images/download.svg) ](https://bintray.com/drummer-aidan/maven/aesthetic/_latestVersion)
[![Build Status](https://travis-ci.org/afollestad/aesthetic.svg)](https://travis-ci.org/afollestad/aesthetic)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b085aa9e67d441bd960f1c6abce5764c)](https://www.codacy.com/app/drummeraidan_50/aesthetic?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/aesthetic&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

You can download an <a href="https://raw.githubusercontent.com/afollestad/aesthetic/master/sample.apk">APK of the sample project</a>.

<img src="https://raw.githubusercontent.com/afollestad/aesthetic/master/images/showcase1.png" /> 

---

# Table of Contents

1. [Gradle Dependency](https://github.com/afollestad/aesthetic#gradle-dependency)
2. [Integration](https://github.com/afollestad/aesthetic#integration)
3. [Basics](https://github.com/afollestad/aesthetic#basics) 
    1. [Basic Theme Colors](https://github.com/afollestad/aesthetic#basic-theme-colors)
    2. [Retrieving Current Values](https://github.com/afollestad/aesthetic#retrieving-current-values)
    3. [Status Bar](https://github.com/afollestad/aesthetic#status-bar)
    4. [Navigation Bar](https://github.com/afollestad/aesthetic#navigation-bar)
    5. [Text Colors](https://github.com/afollestad/aesthetic#text-colors)
    6. [Icon and Title Colors](https://github.com/afollestad/aesthetic#icon-and-title-colors)
    7. [Activity Styles](https://github.com/afollestad/aesthetic#activity-styles)
    8. [Window Background](https://github.com/afollestad/aesthetic#window-background)
4. [View Backgrounds](https://github.com/afollestad/aesthetic#view-backgrounds)
5. [Ignoring Views](https://github.com/afollestad/aesthetic#ignoring-views)
5. [Snackbars](https://github.com/afollestad/aesthetic#snackbars)
6. [Tab Layouts](https://github.com/afollestad/aesthetic#tab-layouts)
7. [Drawer Layouts](https://github.com/afollestad/aesthetic#drawer-layouts)
8. [Bottom Navigation](https://github.com/afollestad/aesthetic#bottom-navigation)
9. [Collapsible Toolbar Layouts](https://github.com/afollestad/aesthetic#collapsible-toolbar-layouts)
10. [Custom View Subclasses](https://github.com/afollestad/aesthetic#custom-view-subclasses)
11. [Activity Keys](https://github.com/afollestad/aesthetic#activity-keys)

---

# Gradle Dependency

The Gradle dependency is available via [jCenter](https://bintray.com/drummer-aidan/maven/aesthetic/view).
jCenter is the default Maven repository used by Android Studio.

Add this to your module's `build.gradle` file:

```gradle
dependencies {
    // ... other dependencies
    compile 'com.afollestad:aesthetic:0.4.2'
}
```

---

# Integration

The easiest way to integrate the library is to have your Activities extend `AestheticActivity`. 
This allows the library to handle lifecycle changes for you:

```java
public class MainActivity extends AestheticActivity {
  
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(...), etc.
    
    // If we haven't set any defaults, do that now
    if (Aesthetic.isFirstTime()) {
        Aesthetic.get()
            ...
            .apply();
    }
  }
}
```

If you don't want to extend `AestheticActivity`, there are a few methods you need to call:

```java
public class AestheticActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    Aesthetic.attach(this); // MUST come before super.onCreate(...)
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Aesthetic.resume(this);
  }

  @Override
  protected void onPause() {
    Aesthetic.pause(this);
    super.onPause();
  }
}

```

---

# Basics

### Basic Theme Colors

The primary color and accent color are the two base theme colors used by apps. Generally, you 
see the primary color on things such as `Toolbar`'s, and the accent color on widgets such as 
`EditText`'s, `CheckBox`'s, `RadioButton`'s, `Switch`'s, `SeekBar`'s, `ProgressBar`'s, etc.

```java
Aesthetic.get()
    .colorPrimaryRes(R.color.md_indigo)
    .colorAccentRes(R.color.md_yellow)
    .apply();
```

You use `Aesthetic.get()` to retrieve the current attached `Aesthetic` instance, set theme properties, 
and `apply()` theme. **This will trigger color changes in the visible Activity WITHOUT recreating it. 
The set theme properties will also be persisted automatically.**

---

### Retrieving Current Values

All the setter methods also have equivalent getters. For an example, you can get the current 
primary theme color:

```java
Aesthetic.get()
    .colorPrimary()
    .take(1)
    .subscribe(color -> {
      // Use color (an integer)
    });
```

`colorPrimary()` returns an RxJava `Observable<Integer>`. `take(1)` here retrieves the latest value, and 
automatically unsubscribes so you don't continue to receive updates when the primary color changes.

If you were to leave `take(1)` out, you need to manage the subscription. You will continue to receive 
updates every time the primary color is changed, until you unsubscribe.

```java
Disposable subscription = 
  Aesthetic.get()
      .colorPrimary()
      .subscribe(color -> {
        // Use color (an integer)
      });
      
// Later, you should unsubscribe, e.g. when your Activity pauses
subscription.dispose();
```

---

### Status Bar

The status bar is the bar on the top of your screen that shows notifications, the time, etc. (I'm 
sure you're aware of that). Per the Material Design guidelines, the status bar color should be a slightly
 darker version of the primary color. You can manually set the color:

```java
Aesthetic.get()
    .colorStatusBar(R.color.md_indigo_dark)
    .apply();
```

Or you can have it automatically generated from the primary color (**you need to set the primary color first**):

```java
Aesthetic.get()
    .colorStatusBarAuto()
    .apply();
```

By default, Aesthetic will automatically use light status bar mode (on Android Marshmallow and above) 
if your status bar color is light. You can modify this behavior:

```java
// AUTO is the default. ON forces light status bar mode, OFF forces it to stay disabled.
Aesthetic.get()
    .lightStatusBarMode(AutoSwitchMode.AUTO)
    .apply();
```

---

### Navigation Bar

By default, the navigation bar on the bottom of your screen is black. You can set it to any color 
you wish, although generally it should be the same as your primary color if not black or transparent:

```java
Aesthetic.get()
    .colorNavigationBarRes(R.color.md_indigo)
    .apply();
```

You can automatically set it to the primary color, also (**you need to set the primary color first**):

```java
Aesthetic.get()
    .colorNavigationBarAuto()
    .apply();
```

---

### Text Colors

You can customize text colors which are used on `TextView`'s, `EditText`'s, etc.

```java
Aesthetic.get()
    .textColorPrimaryRes(android.R.color.black)
    .textColorPrimaryInverseRes(android.R.color.white)
    .textColorSecondaryRes(R.color.dark_gray)
    .textColorSecondaryInverseRes(R.color.lesser_white)
    .apply();
```

Take this layout:

```xml
<LinearLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:orientation="vertical">

  <TextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Hello, world!"
    android:textColor="?android:textColorPrimary"
    android:textSize="24sp"/>

  <TextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="My name is Aidan."
    android:textColor="?android:textColorSecondary"
    android:textSize="16sp"/>

</LinearLayout>
```

The first `TextView` uses the stock Android framework attribute `?android:textColorPrimary`, this library 
 will see that and automatically swap it out with whatever value you set to `textColorPrimary`. 
 The second `TextView` also uses a stock framework attribute, `?android:textColorSecondary`. It will 
 be swapped out with whatever you set to `textColorSecondary` in this library. **If you do not specify a 
 `textColor` attribute at all, TextView's will use the secondary text color as a default.**

You can set the text color and hint text color on `EditText`'s too. **By default, the text 
color will match your primary text color, and the hint text color will match your secondary text 
color.**

In addition to the two stock attributes above, these are some other attributes that are auto swappable: 
`?colorPrimary`, `?colorPrimaryDark`, `?colorAccent`, `?android:windowBackground`, 
`?android:textColorPrimaryInverse`, `?android:textColorSecondaryInverse`.

---

### Icon and Title Colors

You can modify the "icon and title" colors which are used in various places. A main example is on 
toolbars. The color of the toolbar title and the menu icons are taken from this theme value. **By 
default, the Material Design guideline colors are used, when isDark() is true and when it's false.**

```java
Aesthetic.get()
    .colorIconTitleActiveRes(R.color.md_black)
    .colorIconTitleInactiveRes(R.color.md_dark_gray)
    .apply();
```

The getter for these methods are combined into one: `Observable<ActiveInactiveColors> colorIconTitle(Observable<Integer>)`.

Another example of where this is used is the text color for Tab Layout tabs. There are others that 
you will see if you change these values and observe differences.

---

### Activity Styles

Aesthetic allows you to change the actual styles.xml theme applied to Activities:

```java
// Apply an overall light theme
Aesthetic.get()
    .activityTheme(R.style.Theme_AppCompat_Light_NoActionBar)
    .isDark(false)
    .apply();

// Apply an overall dark theme
Aesthetic.get()
    .activityTheme(R.style.Theme_AppCompat_NoActionBar)
    .isDark(false)
    .apply();
```

`isDark` is important, it's used as a hint by this library for various things. For an example, 
with a `Switch` widget, the unchecked state is either light gray or dark gray based on whether it's 
being used with a dark theme or light theme.

**When the `activityTheme` property is changed, `apply()` WILL recreate the visible `Activity`. 
This is the ONLY property which requires a recreate.**

---

### Window Background

Aside from changing the entire base theme of an `Activity`, you can also change just the window background:

```java
Aesthetic.get()
    .colorWindowBackgroundRes(R.color.window_background_gray)
    .apply();
```

---

# View Backgrounds

When you set stock or AppCompat attributes to the background of certain views, Aesthetic will
swap out the attribute with your dynamic theme colors at inflation time:

```java
<LinearLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  android:layout_width="match_parent"
  android:layout_height="196dp"
  android:background="?colorAccent"
  android:orientation="vertical" />
```

Above, `?colorAccent` is an attribute provided by AppCompat. Aesthetic will automatically set the 
background to whatever `accentColor` you have set.

You could also use: `?colorPrimary`, `?colorPrimaryDark`, `?android:windowBackground`, 
`?android:textColorPrimary`, `?android:textColorPrimaryInverse`, 
`?android:textColorSecondary`, `?android:textColorSecondaryInverse`.

---

# Ignoring Views

You can make this library ignore views from being themed by setting the view's tag to `:aesthetic_ignore`.

---

# Snackbars

Snackbar theming is pretty simple. You can change the color of the message text and the color of the 
(optional) action button.

<img src="https://raw.githubusercontent.com/afollestad/aesthetic/master/images/snackbars.png" />

```java
Aesthetic.get()
    .snackbarTextColorRes(R.color.white)
    .snackbarActionTextColorRes(R.color.md_blue)
    .apply();
```

By default, the text color will match `textColorPrimary` when `isDark()` is true, or `textColorPrimaryInverse` 
when `isDark()` is false. By default, the action text color will match `colorAccent()`.

---

# Tab Layouts

Tab Layouts from the Design Support library are automatically themed. The main screen in the 
 sample project is an example of this, you see the two tabs under the toolbar at the top.

You can customize background theming behavior:

```java
// The background of the tab layout will match your primary theme color. This is the default.
Aesthetic.get()
    .tabLayoutBackgroundMode(
        TabLayoutBgMode.PRIMARY)
    .apply();

// The background of the tab layout will match your accent theme color.
Aesthetic.get()
    .tabLayoutBackgroundMode(
        TabLayoutBgMode.ACCENT)
    .apply();
```

And indicator (underline) theming behavior:

```java
// The selected tab underline will match your primary theme color.
Aesthetic.get()
    .tabLayoutIndicatorMode(
        TabLayoutIndicatorMode.PRIMARY)
    .apply();

// The selected tab underline will match your accent theme color. This is the default.
Aesthetic.get()
    .tabLayoutIndicatorMode(
        TabLayoutIndicatorMode.ACCENT)
    .apply();
```

*The color of icons and text in your tab layout will automatically be white or black, depending on 
what is more visible over the set background color.*

---

# Drawer Layouts

When your `Activity` has a `DrawerLayout` at its root, your status bar color will get set to the 
`DrawerLayout` instead of the `Activity`, and the `Activity`'s status bar color will be made 
transparent per the Material Design guidelines (so that the drawer goes behind the status bar).
 
If you use `NavigationView`, it will be themed automatically, also. 

<img src="https://raw.githubusercontent.com/afollestad/aesthetic/master/images/drawer_layout.png" />

You can customize behavior:


```java
// Checked nav drawer item will use your set primary color
Aesthetic.get()
    .navigationViewMode(
        NavigationViewMode.SELECTED_PRIMARY)
    .apply();

// Checked nav drawer item will use your set accent color
Aesthetic.get()
    .navigationViewMode(
        NavigationViewMode.SELECTED_ACCENT)
    .apply();
```

In addition, unselected nav drawer items will be shades of white or black based on the set `isDark` value.

---

# Bottom Navigation

Bottom Navigation Views from the Design Support library are automatically themed.

<img src="https://raw.githubusercontent.com/afollestad/aesthetic/master/images/bottom_tabs.png" />

You can customize background theming behavior:

```java
// The background of the bottom tabs will match your primary theme color.
Aesthetic.get()
    .bottomNavigationBackgroundMode(
        BottomNavBgMode.PRIMARY)
    .apply();

// The background of the bottom tabs will match your status bar theme color.
Aesthetic.get()
    .bottomNavigationBackgroundMode(
        BottomNavBgMode.PRIMARY_DARK)
    .apply();

// The background of the bottom tabs will match your accent theme color.
Aesthetic.get()
    .bottomNavigationBackgroundMode(
        BottomNavBgMode.ACCENT)
    .apply();

// The background of the bottom tabs will be dark gray or white depending on the isDark() property.
// This is the default.
Aesthetic.get()
    .bottomNavigationBackgroundMode(
        BottomNavBgMode.BLACK_WHITE_AUTO)
    .apply();
```

You can also customize icon/text theming behavior:

```java
// The selected tab icon/text color will match your primary theme color.
Aesthetic.get()
    .bottomNavigationIconTextMode(
        BottomNavIconTextMode.SELECTED_PRIMARY)
    .apply();

// The selected tab icon/text color will match your accent theme color. This is the default.
Aesthetic.get()
    .bottomNavigationIconTextMode(
        BottomNavIconTextMode.SELECTED_ACCENT)
    .apply();

// The selected tab icon/text color will be black or white depending on which is more visible 
// over the background of the bottom tabs.
Aesthetic.get()
    .bottomNavigationIconTextMode(
        BottomNavIconTextMode.BLACK_WHITE_AUTO)
    .apply();
```

---

# Collapsible Toolbar Layouts

Collapsible Toolbar Layouts are automatically themed, as seen in the sample project.

<img src="https://raw.githubusercontent.com/afollestad/aesthetic/master/images/collapsing_appbar.png" width="600" />

In the sample layout, we automatically set the accent color to the expanded view. The collapsed toolbar 
color will match whatever color your toolbar uses, which is the primary theme color by default. You'll 
also notice that the icons and title color are updated to be most visible over the background color.

---

# Custom View Subclasses

If you have custom view subclasses in your app, such as:

```java
public class MyCustomTextView extends TextView {
  ...
}
```

You may want your view to be themable. You can do so by swapping out `TextView` with `AestheticTextView`,
or any of Aesthetic's other view classes beginning with "Aesthetic". These views handle subscribing 
and unsubscribing from theme property updates automatically; they also handle pulling out attributes 
such as `android:textColor`. Note that all views support background color theming, you don't need to 
extend any special views for background color support.

```java
public class MyCustomTextView extends AestheticTextView {
  ...
}
```

Otherwise, you can subscribe to theme properties (as seen in [Retrieving Current Values](https://github.com/afollestad/aesthetic#retrieving-current-values)) 
such as `Aesthetic.get().primaryColor()` and manually update your custom view (text color or whatever else).

---

# Activity Keys

`AestheticActivity` has an optional override named `key()`:

```java
public class MyActivity extends AestheticActivity {

    ...
    
    @Nullable
    @Override
    public String key() {
      return "my_activity";
    }
}
```

You can return whatever you wish. If this key is specified, this specific Activity will save its 
own version of the `activityTheme()`, `colorStatusBar()`, and `colorNavigationBar()` theme properties. 

An example of where this can be useful: You have an Activity which displays a list of videos, this 
main activity has a colored status bar and navigation bar. When you tap a video, it brings you to a 
player Activity. This player Activity needs a different theme so that the status bar and nav bar 
are transparent and hidden. You can use a separate key for this player Activity so that it doesn't 
adopt the same window theme properties as the main activity.

If you do not use `AestheticActivity`, your custom Activity can implement the `AestheticKeyProvider` 
interface.