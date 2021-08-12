package com.puma211.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.HashMap;

/**
 * @author YEh
 * @date 2021/08/11 
 * 飞星Demo
 */
public class FlyingStarActivity extends Activity implements View.OnClickListener {
    private final String TAG = "FlyingStarActivity";
    private String[] groupList = new String[]{"what", "is", "your", "name", "?", "哈哈哈"};
    private HashMap<Integer, View> mSelectedInViewMap = new HashMap<>();
    private HashMap<View, Integer> mSelectedViewInMap = new HashMap<>();
    private CustomLinearLayout llAnswers;
    private CustomLinearLayout llQuestions;
    private View curAnswerView;
    private TextView curQuesitonView;
    private int mAnimTime = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flying_star);
        llQuestions = (CustomLinearLayout) findViewById(R.id.ll_group_selected);
        llAnswers = (CustomLinearLayout) findViewById(R.id.ll_group_all);
        addViews("");
        for (int i = 0; i < groupList.length; i++) {
            TextView et = new TextView(this);
            et.setSingleLine();
            et.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 15;
            layoutParams.rightMargin = 15;
            layoutParams.topMargin = 15;
            et.setLayoutParams(layoutParams);
            et.setBackgroundResource(R.drawable.bl_shape_corners_textview);
            et.setPadding(15, 15, 15, 15);
            et.setText(groupList[i]);
            et.setTextColor(getResources().getColor(R.color.color_333333));
            et.setId(0);
            et.setOnClickListener(this);
            llAnswers.addView(et);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                for (int i = 0; i < llQuestions.getChildCount(); i++) {
                    View questionView = llQuestions.getChildAt(i);
                    if (v.equals(questionView)) {
                        animQuesViewToAnsView(this, questionView, llAnswers.getChildAt(mSelectedViewInMap.get(questionView)), 0);
                        llQuestions.removeViewAt(i);
                        Integer num;
                        if ((num = mSelectedViewInMap.get(questionView)) != null) {
                            mSelectedViewInMap.remove(questionView);
                            curAnswerView = llAnswers.getChildAt(num);
                            mSelectedInViewMap.remove(num);
                        }
                        if (llQuestions != null && llQuestions.getChildCount() == 0) {
                            llQuestions.removeAllViews();
                            addViews("");
                        }
                        return;
                    }
                }
                break;
            case 0:
                for (int i = 0; i < llAnswers.getChildCount(); i++) {
                    View answerView = llAnswers.getChildAt(i);
                    if (v.equals(answerView)) {
                        for (Integer position : mSelectedInViewMap.keySet()) {  //判断点击的项是否已经点击过
                            if (position == i) {
                                return;
                            }
                        }
                        if (llQuestions != null && llQuestions.getChildCount() == 1 && TextUtils.isEmpty(((TextView) llQuestions.getChildAt(0)).getText().toString())) {
                            llQuestions.removeAllViews();
                        }
                        View newQuestionView = addViews(((TextView) answerView).getText().toString());
                        ((TextView) newQuestionView).setBackgroundResource(R.drawable.bl_shape_corners_textview);
                        mSelectedViewInMap.put(newQuestionView, i);
                        mSelectedInViewMap.put(i, answerView);
                        animAnsViewToQuesView(this, answerView, newQuestionView, 0);
                        answerView.setBackgroundResource(R.drawable.bl_shape_corners_textview_empty);
                        ((TextView) answerView).setTextColor(getResources().getColor(R.color.app_text_white_color));
                        return;
                    }
                }
                break;
        }
    }

    private View addViews(String sText) {
        curQuesitonView = new TextView(this);
        curQuesitonView.setSingleLine();
        curQuesitonView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        layoutParams.topMargin = 15;
        curQuesitonView.setPadding(15, 15, 15, 15);
        curQuesitonView.setLayoutParams(layoutParams);
        curQuesitonView.setText(sText);
        curQuesitonView.setVisibility(View.INVISIBLE);
        curQuesitonView.setTextColor(getResources().getColor(R.color.color_333333));
        curQuesitonView.setOnClickListener(this);
        llQuestions.addView(curQuesitonView, llQuestions.getChildCount());
        return llQuestions.getChildAt(llQuestions.getChildCount() - 1);
    }

    private void animQuesViewToAnsView(Activity activity, View quesView, View ansView, float scale) {
        int[] toXY = new int[2];
        int ansviewPos = 0;
        if (llAnswers != null && llAnswers.getChildCount() > 0) {
            for (int i = 0; i < llAnswers.getChildCount(); i++) {
                if (llAnswers.getChildAt(i) == ansView) {
                    ansviewPos = i;
                }
            }
        }
        for (int i = 0; i < ansviewPos; i++) {
            View childAt = llAnswers.getChildAt(i);
            toXY[0] += childAt.getPaddingLeft() + childAt.getWidth() + childAt.getPaddingRight();
        }
        toXY[1] = llAnswers.getBottom() + llAnswers.getHeight() / 2 + 5;
        int centerX = (int) (toXY[0] + ansView.getMeasuredWidth() / 2f);
        int centerY = (int) (toXY[1] + ansView.getMeasuredHeight() / 2f);
        animViewToView(activity, quesView, centerX, centerY, scale, 1);
    }

    private void animAnsViewToQuesView(Activity activity, View ansView, View quesView, float scale) {
        int[] toXY = new int[2];
        int childCount = llQuestions.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = llQuestions.getChildAt(i);
            toXY[0] += childAt.getPaddingLeft() + childAt.getWidth() + childAt.getPaddingRight();
        }
        toXY[1] = llQuestions.getTop() + llQuestions.getHeight() / 2 + 5;
        int centerX = (int) (toXY[0] + quesView.getMeasuredWidth() / 2f);
        int centerY = (int) (toXY[1] + quesView.getMeasuredHeight() / 2f);
        animViewToView(activity, ansView, centerX, centerY, scale, -1);
    }

    private void animViewToView(Activity activity, View tagView, int toCenterX, int toCenterY, float scale, int orientation) {
        int[] winXY = new int[2];
        tagView.getLocationOnScreen(winXY);
        float toX = tagView.getMeasuredWidth() * scale;
        float toY = tagView.getMeasuredHeight() * scale;
        float pivotX = (toCenterX - winXY[0]) * 1f / tagView.getMeasuredWidth();
        float pivotY = orientation;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, toX, 1f, toY, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        final ImageView tempMoveView = new ImageView(activity);
        tempMoveView.setScaleType(ImageView.ScaleType.FIT_XY);
        Bitmap tempBm = getViewBitmap(tagView);
        tempMoveView.setImageBitmap(tempBm);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(tagView.getMeasuredWidth(), tagView.getMeasuredHeight());
        params.setMargins(winXY[0], winXY[1], winXY[0] + tagView.getMeasuredWidth(), winXY[1] + tagView.getMeasuredHeight());
        tempMoveView.setLayoutParams(params);

        final FrameLayout frameLayout = (FrameLayout) activity.getWindow().getDecorView().getRootView();
        frameLayout.addView(tempMoveView);
        scaleAnimation.setDuration(mAnimTime);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                frameLayout.removeView(tempMoveView);
                if (curAnswerView != null) {
                    curAnswerView.setBackgroundResource(R.drawable.bl_shape_corners_textview);
                    ((TextView) curAnswerView).setTextColor(getResources().getColor(R.color.color_333333));
                    curAnswerView = null;
                }
                if (curQuesitonView != null) {
                    curQuesitonView.setVisibility(View.VISIBLE);
                    curQuesitonView = null;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tempMoveView.startAnimation(scaleAnimation);
    }

    private Bitmap getViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

}
