androidwidgets
==============

StackView
---------

Used to create multi column vertical linear layouts.

Usage:

```java
StackView stack = (StackView)view.findViewById(R.id.grid);
stack.setColumnCount(3);

// Remove old views, in case this view is being recycled.
stack.popAllViews();

stack.pushView(child1);
stack.pushView(child2);
stack.pushView(child3);
// etc...
```

ViewPagerIndicator
------------------

Used to nicely indicate the current position in a ViewPager.
It is composed of dots representing pages which parallax if there isn't enough room to fit in the parent ViewPager.

Usage:

Layout file:
```xml
<RelativeLayout
	android:id="@+id/photopager_container"
	android:layout_width="fill_parent"
	android:layout_height="200dp"
	android:background="#000"
>
	<ViewPager
		android:id="@+id/photopager"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
	/>
	<.ViewPagerIndicator
		android:id="@+id/photopagerindicator"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
	/>
</RelativeLayout>
```

```java
photoIndicator.setViewPager(photoPager);

// Required to invalidate it when scrolling, or else it will appear a bit janky when parallaxing.
photoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	public void onPageScrollStateChanged(int state) {
	}

	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		photoIndicator.invalidate(); // Redraw the view.
	}

	public void onPageSelected(int position) {
	}
});
```

ZoomView
--------

Performs various zooming operations like double tap, pinch to zoom, and flick to scroll.
Should only contain one child!

Usage:

```objective-c
ZoomView zv = new ZoomView(context);
zv.setBackgroundColor(0xff000000);
zv.addView(image);
```

You should also be able to create it from a layout file.

