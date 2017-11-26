# UeCalendar
a simple calendar for android

<img src="/images/screen.jpg" alt="Demo Screen" width="300px" />

简单实用，可左右滑动，基于ViewPager + GridView 布局
高度自适应

Usage
-----

Step 1. Add the JitPack repository to your build file

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency

```
	dependencies {
		compile 'com.github.User:Repo:Tag'
	}
```

Step 3. Add `UeCalendar` into your layouts or view hierarchy.
Step 4. Implements a interface `CalendarListener` when you need it.

Example:

```xml
<com.uemaker.uecalendar.UeCalendar
    android:id="@+id/ue_calendar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#f5f5f5"
    app:titleColor="#000"
    app:titleSize="16sp"
    app:weekSize="16sp"
    app:weekBackground="#fff"
    />
```

Options
-----

