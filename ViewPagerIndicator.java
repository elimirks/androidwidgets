package <your package>;

import android.os.Bundle;

import android.view.View;

import android.util.AttributeSet;
import android.util.Log;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import android.support.v4.view.ViewPager;

/*
 * AGREEMENT
 *
 * USERS OF THIS WIDGET MUST AGREE TO INCLUDE THIS AGREEMENT
 * AND RETAIN CREDIT FOR THE ORIGINAL CREATOR ONLY IF THEY FEEL LIKE IT.
 * IF YOU DON'T WANT THIS USELESS MESSAGE, DELETE IT NOW, OR
 * SUFFER THE CONSEQUENCES.
 */

/*
 * TODO before publishing this on github, document it a bit more.
 * Also, add a means of specifying the id of the viewpager in the xml layout to set the pager.
 */

/**
 * A simple view pager indicator.
 *
 * @author Elijah Mirecki
 */
public class ViewPagerIndicator extends View {
	private final static int encasementColour = 0x26000000;
	private final static int activeColour     = 0xffffffff;
	private final static int inactiveColour   = 0x77ffffff;
	private ViewPager pager = null;
	
	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/**
	 * Specify the view pager for which this object should indicate the page of.
	 *
	 * @param pager The pager!
	 */
	public void setViewPager(ViewPager pager) {
		this.pager = pager;
	}
	/**
	 * Converts dp to pixel equivilents.
	 *
	 * @param dp The dp to convert.
	 * @return The amount in pixels.
	 */
	private int dpToPx(int dp) {
		final float density =
			getContext().getResources().getDisplayMetrics().density;
		return (int)Math.ceil(dp * density);
	}
	
	// Allocate outside the onDraw method for performance reasons.
	final Paint boxPaint = new Paint();
	final ShapeDrawable dot = new ShapeDrawable(new OvalShape());
	final RectF encasementRect = new RectF();
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Screw drawing if a view pager isn't specified!
		if (pager == null) return;

		int dotCount = pager.getAdapter().getCount();
		int currentDot = pager.getCurrentItem();
		
		// Hard coded values for the size of the indicator.
		final int dotSize = dpToPx(6);
		final int padding = dpToPx(7);
		
		final int vHeight = getMeasuredHeight();
		final int vWidth  = getMeasuredWidth();
		
		final int yPos = vHeight - dotSize - padding;
		
		// Calculate the space that the dots need to take up.
		final int listWidth = padding + dotCount * (dotSize + padding);

		final boolean canFitInParent = listWidth + padding * 2 <= vWidth;
		final int initialXPos = (int)(canFitInParent
			// Center the dots horizintally.
			? vWidth / 2 - listWidth / 2
			// Parallax scrolling to not let the current indicator run of the ends of the screen.
			: padding + (- (float)pager.getScrollX() / pager.getMeasuredWidth()) * ((float)(listWidth + padding * 2 - vWidth) / dotCount));

		final int encasementYPadding = dpToPx(4);
		final int encasementHeight = dotSize + encasementYPadding;
		final int encasementWidth  = listWidth;
		final int encasementRadius = encasementHeight - encasementYPadding;
		
		encasementRect.set(
			initialXPos,
			yPos - encasementYPadding,
			initialXPos + encasementWidth,
			yPos + encasementHeight
		);
		boxPaint.setColor(encasementColour);
		canvas.drawRoundRect(
			encasementRect,
			encasementRadius,
			encasementRadius,
			boxPaint);

		for (int i = 0; i < dotCount; i++) {
			final int xPos = i * (dotSize + padding) + padding + initialXPos;
			final int color = i == currentDot
				? activeColour
				: inactiveColour;

			dot.getPaint().setColor(color);
			dot.setBounds(xPos, yPos, xPos + dotSize, yPos + dotSize);
			dot.draw(canvas);
		}
	}
}

