package ime.elemedemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by wudi on 04/08/2017.
 */

public class PowerViewgroupTest extends RelativeLayout implements PowerScrollView.OnScrollChange {

    private PowerScrollView mScrollView;
    private View mChildView;
    private RelativeLayout mThumbnailView;
    private int mThumbnailTop = 450;
    private int mThumbnailWidth;

    public PowerViewgroupTest(Context context) {
        super(context);
    }

    public PowerViewgroupTest(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PowerViewgroupTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mScrollView = (PowerScrollView) getChildAt(1);


        mScrollView.setScrollChange(this);


        mThumbnailView = (RelativeLayout) getChildAt(0);

        mChildView = mScrollView.getCView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mThumbnailTop = mThumbnailView.getTop();
        mThumbnailWidth = mThumbnailView.getMeasuredWidth();
        Log.e("test", "onMeasure *****");
    }


    private boolean flag = true;

    @Override
    public void scrollChange(int t, int offset) {
        if (flag) {
            flag = false;
            mScrollView.setScrollY(0);
        } else {
            mThumbnailView.setTranslationY(-t * 0.5f);
        }
    }

    private float lastX;
    private float lastY;
    private int lastPoint;
    private int currentPoint = 450;
    private MotionEvent mLastMoveEvent;
    private boolean mMoveUp;
    private boolean mMoveDown;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (mMoveUp) {
                    if (mThumbnailView.getTop() > 0) {

//                        mChildView.setScrollY(0);
                    }
                } else if (mMoveDown) {

                }

                break;
            case MotionEvent.ACTION_DOWN:
//                mThumbnailView.setTranslationY(100);
                mThumbnailView.setTranslationY(1000);
                lastX = ev.getRawX();
                lastY = ev.getRawY();
                dispatchTouchEventSupper(ev);
                return true;
            case MotionEvent.ACTION_MOVE:

                mLastMoveEvent = ev;


                float distanceX = ev.getRawX() - lastX;
                float distanceY = ev.getRawY() - lastY;
                mMoveDown = distanceY > 0;
                mMoveUp = !mMoveDown;

                lastX = ev.getRawX();
                lastY = ev.getRawY();


                float topScale = mThumbnailView.getTop() / (float) mThumbnailTop;
                float distanceWidth = 1080 - mThumbnailWidth;
                float surplusWidth = 0.3f * (1 - topScale);

//                Log.e("test",distanceWidth +  " ////*****----- " + (1 - topScale));

                if (mMoveUp) {
                    if (mThumbnailView.getTop() > 0) {
//                        mThumbnailView.setTranslationY();
                        Log.e("test", "--------------" + mScrollView.getScrollY());
                        mThumbnailView.offsetTopAndBottom((int) (distanceY));
                    } else {
//                            mThumbnailView.offsetTopAndBottom((int) (distanceY * 0.5f));

//                        if (mThumbnailView.getTop() > -1200) {
//                        } else  {
//                            if (mThumbnailView.getTop() != -1200) {
//                                mThumbnailView.setTop(-1200);
//                            }
//                        }
                    }

                    if (mThumbnailView.getScaleX() < 1) {
                        float scale = surplusWidth + 0.7f;
                        if (scale > 1) {
                            scale = 1;
                        }

                        mThumbnailView.setScaleX(scale);
                    }
                }

                if (mMoveDown && mThumbnailView.getTop() < 0) {
//                    Log.e("test",mScrollView.getScrollY() + "***********" );

//                    mThumbnailView.offsetTopAndBottom(Math.round(distanceY * 0.5f));
                }

                if (mMoveDown && !isScrollTop(mScrollView)) {
                    return dispatchTouchEventSupper(ev);
                }

                if ((mMoveUp && isCanScrollUp(mChildView)) || (mMoveDown && mChildView.getTop() > 0)) {


                    int y = currentPoint + (int) distanceY;


                    if (y < 0) {
                        y = 0;
                    }
                    if (y == 0) {
                        sendDownEvent();
                    }


                    lastPoint = currentPoint;

                    currentPoint = y;


                    int change = y - lastPoint;
//                    Log.e("test", change + "..........."+distanceY +".........." + y);

                    mChildView.offsetTopAndBottom(change);
                    return true;
                }
        }

        return dispatchTouchEventSupper(ev);
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    private void sendDownEvent() {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private boolean isScrollTop(View view) {
        return !view.canScrollVertically(-1);
    }

    private boolean isCanScrollUp(View view) {
        return view.getTop() > 0;
    }
}
