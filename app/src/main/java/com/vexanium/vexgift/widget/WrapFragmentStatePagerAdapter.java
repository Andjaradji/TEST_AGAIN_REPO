package com.vexanium.vexgift.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by mac on 12/14/17.
 */

public abstract class WrapFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private int mCurrentPosition = -1;

    public WrapFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * @param container View container (instanceof {@link WrapContentViewPager}))
     * @param position  Item position
     * @param object    {@link Fragment}
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (!(container instanceof WrapContentViewPager)) {
            throw new UnsupportedOperationException("ViewPager is not a WrappingViewPager");
        }

        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            WrapContentViewPager pager = (WrapContentViewPager) container;
            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.onPageChanged(fragment.getView());
            }
        }
    }
}