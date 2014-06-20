package ca.carpages.carpagesconsumer;

import android.view.View;
import android.widget.FrameLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.content.Context;

import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import android.os.Handler;
import android.graphics.Canvas;
import android.animation.Animator;

public class ZoomView extends FrameLayout
		implements GestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener,
		OnScaleGestureListener {
	private float maxScale = 2f;
	private float lastPressX = 0, lastPressY = 0;

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;

	public ZoomView(Context context) {
		super(context);
		init(context);
	}

	public ZoomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		maxScale = attrs.getAttributeFloatValue(null, "maxScale", maxScale);
	}

	private void init(Context context) {
		gestureDetector = new GestureDetector(context, this);
		gestureDetector.setOnDoubleTapListener(this);
		//gestureDetector.setIsLongPressEnabled(false);
		scaleGestureDetector = new ScaleGestureDetector(context, this);
	}

	private void setScale(float scale) {
		View child = getChild();
		child.setScaleX(scale);
		child.setScaleY(scale);
	}
	
	private float getScale() {
		return getChild().getScaleX();
	}

	public void setPivot(float pivotX, float pivotY) {
		View child = getChild();
		child.setPivotX(pivotX);
		child.setPivotY(pivotY);
	}

	private void increasePivotBy(float pivotX, float pivotY) {
		View child = getChild();
		child.setPivotX(child.getPivotX() + pivotX);
		child.setPivotY(child.getPivotY() + pivotY);
	}

	private float generateRestrainedScale(float scale) {
		return Math.max(Math.min(scale, maxScale), 1f);
	}
	
	private void increaseScaleBy(float scale) {
		float newScale = generateRestrainedScale(getScale() * scale);
		setScale(newScale);
	}

	private boolean atRightEdge() {
		View child = getChild();
		return !isZoomed() || child.getPivotX() == child.getWidth();
	}

	private boolean atLeftEdge() {
		View child = getChild();
		return !isZoomed() || child.getPivotX() == 0;
	}

	public boolean isZoomed() {
		return getScale() != 1f;
	}

	public void unzoom() {
		setScaleAnimated(1f);
	}

	public void zoomToMax() {
		setScaleAnimated(maxScale);
	}

	private View getChild() {
		int count = getChildCount();
		if (count > 0) {
			return getChildAt(0);
		} else {
			return null;
		}
	}

	private void setScaleAnimated(float scale) {
		final int shortAnimationDurtion = getContext().getResources().getInteger(
			android.R.integer.config_shortAnimTime);
		getChild().animate()
			.scaleX(scale)
			.scaleY(scale)
			.setDuration(shortAnimationDurtion)
			.setListener(new Animator.AnimatorListener() {
				public void onAnimationCancel (Animator animation) {}
				public void onAnimationEnd (Animator animation) {}
				public void onAnimationRepeat (Animator animation) {}
				public void onAnimationStart (Animator animation) {}
			})
		;
	}

	private void flingPanBy(float velocityX, float velocityY) {
		// MUCH MATHS. SUCH VELOCITY. WOW! FLING GESTURE! SO SMOOTH.
		int greastestVelocity =
			(int)Math.max(Math.abs(velocityX), Math.abs(velocityY));
		if (greastestVelocity > 0) {
			final int fps = 60;
			final int millisecondDelay = 1000 / fps;

			// Velocity is measured in pixels per second.
			final float xPanPerDelay = velocityX / (1000 / millisecondDelay);
			final float yPanPerDelay = velocityY / (1000 / millisecondDelay);
			panViewBy(-xPanPerDelay, -yPanPerDelay);

			final float decelleractionFactor = 0.9f;
			final float scale = getScale();
			final float newVelocityX = (velocityX * decelleractionFactor);
			final float newVelocityY = (velocityY * decelleractionFactor);

			new Handler().postDelayed(new Runnable() {
				public void run() {
					flingPanBy(newVelocityX, newVelocityY);
				}
			}, millisecondDelay);
		}
	}

	public void panViewBy(float amountX, float amountY) {
		float zoomFactorScaleMultiplier = 1.0f / (getScale() - 1.0f);
		
		View child = getChild();
		
		float currentPivotX = child.getPivotX();
		float currentPivotY = child.getPivotY();
		
		float nextPivotX = currentPivotX + amountX * zoomFactorScaleMultiplier;
		float nextPivotY = currentPivotY + amountY * zoomFactorScaleMultiplier;
		
		nextPivotX = Math.min(Math.max(nextPivotX, 0), child.getWidth());
		nextPivotY = Math.min(Math.max(nextPivotY, 0), child.getHeight());
		
		if (nextPivotX != currentPivotX || nextPivotY != currentPivotY) {
			setPivot(nextPivotX, nextPivotY);
		}
	}

	public boolean onTouchEvent(MotionEvent e) {
		boolean gestureDetectorResult = gestureDetector.onTouchEvent(e);
		boolean scaleGestureDetectorResult = scaleGestureDetector.onTouchEvent(e);

		return gestureDetectorResult || scaleGestureDetectorResult;
	}

	// Implements OnGestureListener

	public boolean onDown(MotionEvent e) {
		if (isZoomed()) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2,
			float velX, float velY) {
		flingPanBy(velX, velY);
		return true;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			float distX, float distY) {
		if (isZoomed()) {
			panViewBy(distX, distY);
			return true;
		} else {
			return false;
		}
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	// Implements OnDoubleTapListener

	public boolean onDoubleTap(MotionEvent e) {
		if (isZoomed()) {
			unzoom();
		} else {
			setPivot(e.getX(), e.getY());
			zoomToMax();
		}

		return true;
	}
	
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	// Implements OnScaleGestureListener

	public boolean onScale(ScaleGestureDetector detector) {
		final float scaleFactor = detector.getScaleFactor();
		increaseScaleBy(scaleFactor);
		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		getParent().requestDisallowInterceptTouchEvent(true);

		if (!isZoomed()) {
			setPivot(detector.getFocusX(), detector.getFocusY());
		}
		return true;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
	}
}

