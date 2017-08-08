package ime.elemedemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by wudi on 03/08/2017.
 */

public class PowerScrollView extends ScrollView {
    interface OnScrollChange {
        void scrollChange(int t, int offset);
    }

    private OnScrollChange mScrollChange;
    private View mChildView;
    private View mHeader;

    public PowerScrollView(Context context) {
        super(context);
    }

    public PowerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PowerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollChange(OnScrollChange change) {
        mScrollChange = change;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mChildView = getChildAt(0);
        mHeader = mChildView.findViewById(R.id.header);
    }

    public View getCView() {
        return mChildView;
    }

    public void setHeaderHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeader.getLayoutParams();
        params.height = height;
        mHeader.setLayoutParams(params);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollChange.scrollChange(t, oldt - t);
    }
}
