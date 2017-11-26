# UeCalendar
a simple calendar for android

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
    android:id="@+id/calendar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    />
```

Options
-----

