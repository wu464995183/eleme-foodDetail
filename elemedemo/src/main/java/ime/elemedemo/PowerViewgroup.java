package ime.elemedemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by wudi on 10/08/2017.
 */

public class PowerViewgroup extends RelativeLayout implements PowerScrollView.OnScrollChange {

    private final float DAMPING = .5f;
    private final float GOODSSCALE = .7f;
    private final int DURATION = 200;

    private PowerScrollView mScrollView;
    private View mChildView;
    private RelativeLayout mThumbnailView;
    private int mDefaultMarginTop = 450;
    private float mLastY;
    private MotionEvent mLastMoveEvent;
    private boolean mMoveUp;
    private boolean mMoveDown;
    private boolean mFlag = true;
    private int mViewWidth;

    public PowerViewgroup(Context context) {
        this(context, null);
    }

    public PowerViewgroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerViewgroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDefaultMarginTop = getResources().getDimensionPixelSize(R.dimen.margin_top);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mScrollView = (PowerScrollView) getChildAt(1);

        mScrollView.setAlpha(0);

        mScrollView.setScrollChange(this);


        mThumbnailView = (RelativeLayout) getChildAt(0);

        mChildView = mScrollView.getCView();
    }

    @Override
    public void scrollChange(int t, int offset) {
        if (mFlag) {
            mFlag = false;
            mScrollView.setScrollY(0);
        } else {
            mThumbnailView.setTranslationY(-t * DAMPING);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        setGoodsViewSize();
        mScrollView.setHeaderHeight(mViewWidth);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUp();
                break;
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                dispatchTouchEventSupper(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;

                float distanceY = ev.getRawY() - mLastY;
                mMoveDown = distanceY > 0;
                mMoveUp = !mMoveDown;

                mLastY = ev.getRawY();

                if (mMoveDown && !isScrollTop(mScrollView)) {
                    return dispatchTouchEventSupper(ev);
                }

                if ((mMoveUp && isCanScrollUp(mChildView))) {
                    handerActionMoveUP((int) distanceY);
                    return true;
                }

                if (mMoveDown) {
                    handerActionMoveDOWN((int) distanceY);
                    return true;
                }
        }

        return dispatchTouchEventSupper(ev);
    }

    private void setGoodsViewSize() {
        RelativeLayout.LayoutParams params = (LayoutParams) mThumbnailView.getLayoutParams();
        params.width = mViewWidth;
        params.height = mViewWidth;
        mThumbnailView.setLayoutParams(params);
    }

    private void handerActionMoveUP(int distanceY) {

        float topScale = mThumbnailView.getTop() / (float) mDefaultMarginTop;
        if (topScale > 1) topScale = 1;
        float surplusWidthScale = 0.3f * (1 - topScale);

        int y = mChildView.getTop() + distanceY;
        int change;

        if (y < 0) {
            y = 0;
            topScale = 0;
            surplusWidthScale = .3f;
            change = y - mChildView.getTop();
            sendDownEvent();
        } else {
            change = distanceY;
        }


        mThumbnailView.offsetTopAndBottom(change);
        mChildView.offsetTopAndBottom(change);

        setScrollViewAlpha(topScale);
        setGoodsDescriptionScaleX(surplusWidthScale);
    }

    private void handerActionMoveDOWN(int distanceY) {

        if (mChildView.getTop() < mDefaultMarginTop) {
            float topScale = mThumbnailView.getTop() / (float) mDefaultMarginTop;
            float surplusWidthScale = 0.3f * (1 - topScale);

            mThumbnailView.offsetTopAndBottom(distanceY);
            mChildView.offsetTopAndBottom(distanceY);

            setScrollViewAlpha(topScale);
            setGoodsDescriptionScaleX(surplusWidthScale);
        } else {
            mThumbnailView.offsetTopAndBottom((int) (distanceY * DAMPING));
            mChildView.offsetTopAndBottom((int) (distanceY * DAMPING));
        }
    }

    private void setScrollViewAlpha(float topScale) {
        float scrollScale = 1 - topScale;
        if (scrollScale > 1) {
            scrollScale = 1;
        }

        mScrollView.setAlpha(scrollScale);
    }

    private void setGoodsDescriptionScaleX(float surplusWidthScale) {
        float scale = surplusWidthScale + GOODSSCALE;
        if (scale > 1) {
            scale = 1;
        }

        mThumbnailView.setScaleX(scale);
        mThumbnailView.setScaleY(scale);
    }

    private void handleActionUp() {

        int thresholdUp = mDefaultMarginTop * 2 / 3;
        int thresholdDown = mDefaultMarginTop / 3;

        if (mThumbnailView.getTop() > 0) {
            if (mMoveUp) {
                if (mThumbnailView.getTop() < thresholdUp) {
                    restoreChildView(0, 1f, 1f);
                } else {
                    restoreChildView(getResources().getDimensionPixelSize(R.dimen.margin_top), GOODSSCALE, 0f);
                }
            } else if (mMoveDown) {
                if (mThumbnailView.getTop() > thresholdDown) {
                    mFlag = true;
                    restoreChildView(getResources().getDimensionPixelSize(R.dimen.margin_top), GOODSSCALE, 0f);
                } else {
                    restoreChildView(0, 1f, 1f);
                }
            }
        }
    }

    private void restoreChildView(final int marinTop, float scale, float alpha) {

        final float currrentScale = mThumbnailView.getScaleX();
        final float surplusScale = scale - currrentScale;

        final float currrentAlpha = mScrollView.getAlpha();
        final float surplusAlpha = alpha - currrentAlpha;


        final int top = mChildView.getTop();
        final int distance = Math.abs(marinTop - top);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(mChildView.getTop(), marinTop);
        valueAnimator.setDuration(DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();

                float scale = Math.abs(value - top) / (float)distance;

                float sx = scale * surplusScale + currrentScale;

                float ap = scale * surplusAlpha + currrentAlpha;

                mThumbnailView.setScaleX(sx);
                mThumbnailView.setScaleY(sx);
                mScrollView.setAlpha(ap);


                RelativeLayout.LayoutParams params = (LayoutParams) mThumbnailView.getLayoutParams();
                params.topMargin = value;
                mThumbnailView.setLayoutParams(params);

                ScrollView.LayoutParams params1 = (ScrollView.LayoutParams) mChildView.getLayoutParams();
                params1.topMargin = value;
                mChildView.setLayoutParams(params1);
            }
        });

        valueAnimator.start();
    }

    private boolean dispatchTouchEventSupper(MotionEvent e) {
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
