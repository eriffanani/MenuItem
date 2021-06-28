# Simple Menu Item
This is my first library, the use of this library is very simple, just create menu items where there are only square and circular backgrounds, icons, and text as menu titles.

## Installation
#### build.gradle(Project)
```
maven {url 'https://jitpack.io'}
```

#### build.gradle(Module)
```
implementation 'com.github.eriffanani:MenuItem:0.0.3'
```

## How to use
```
<com.erif.menuitem.MenuItem
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:icon_menu="@drawable/ic_setting_white"
    app:background_color="@color/purple_700"
    app:shape="circle"
    app:title="Menu Item 3" />
```

## Screenshot
![Snip20210628_3](https://user-images.githubusercontent.com/26743731/123600888-4a7a9500-d821-11eb-8aa4-02f8810be018.png)
