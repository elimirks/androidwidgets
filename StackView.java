package ca.carpages.carpagesconsumer;

import android.content.Context;
import android.util.AttributeSet;

import android.view.ViewGroup;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * @author Elijah Mirecki
 *
 * A simple type of layout to organize sub views into multiple stacks.
 */
public class StackView extends LinearLayout {
	private int columnCount = 1;
	private int currentlyPushedViewCount = 0;
	private LinearLayout bottomLinearLayout;
	
	public StackView(Context context) {
		super(context);
		init(null);
	}
	public StackView(Context context, AttributeSet attributes) {
		super(context, attributes);
		init(attributes);
	}
	public StackView(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);
		init(attributes);
	}
	private void init(AttributeSet attributes) {
		setOrientation(LinearLayout.HORIZONTAL);
		if (attributes != null) {
			setColumnCount(
				attributes.getAttributeIntValue(null, "columnCount", columnCount));
		}
		createColumns(columnCount);
	}
	private void createColumns(int amount) {
		final Context context = getContext();

		for (int i = 0; i < amount; i++) {
			LinearLayout column = new LinearLayout(context);
			column.setOrientation(LinearLayout.VERTICAL);
			
			int paddingAmount = Utils.dpToPx(context, 2);
			if (i == 0) {
				column.setPadding(0, 0, paddingAmount, 0);
			} else if (i == columnCount - 1) {
				column.setPadding(paddingAmount, 0, 0, 0);
			} else {
				column.setPadding(paddingAmount, 0, paddingAmount, 0);
			}

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				0, // Flexible width
				LinearLayout.LayoutParams.WRAP_CONTENT,
				1); // Equal weight on all columns.
			addView(column, params);
		}
	}
	/**
	 * The amount of columns to display specifications in.
	 * Must be called before pushing or popping views,
	 * or undefined behavior will occur.
	 */
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
	public void pushView(View view) {
		int columnNumber = currentlyPushedViewCount++ % columnCount;
		LinearLayout column = (LinearLayout)getChildAt(columnNumber);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT,
			LayoutParams.WRAP_CONTENT);
		column.addView(view, params);
	}
	public void popAllViews() {
		currentlyPushedViewCount = 0;
		removeAllViews();
		createColumns(columnCount);
	}
}

