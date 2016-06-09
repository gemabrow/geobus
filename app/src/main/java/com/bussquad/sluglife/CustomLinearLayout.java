package com.bussquad.sluglife;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by Jose on 2/25/2016.
 */
public class CustomLinearLayout extends LinearLayoutManager {

        private boolean isScrollEnabled = true;

        public CustomLinearLayout(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }

}
