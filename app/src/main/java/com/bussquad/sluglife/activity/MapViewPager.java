package com.bussquad.sluglife.activity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by Jose on 8/7/2016.
 */
public class MapViewPager extends ViewPager {

    public MapViewPager(Context context) {
        super(context);
    }

    public MapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}
