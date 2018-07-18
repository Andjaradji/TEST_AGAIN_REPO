package com.vexanium.vexgift.module.box.ui.helper;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Created by Amang on 18/07/2018.
 */

public interface BoxFragmentChangeListener extends Serializable{
    void onClick(boolean toHistory);
}
