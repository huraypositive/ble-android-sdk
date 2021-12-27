//
//  IState.java
//
//  Copyright (c) 2016 OMRON HEALTHCARE Co.,Ltd. All rights reserved.
//

package net.huray.omronsdk.utility.sm;

import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

interface IState {

    void enter(@Nullable Object[] transferObjects);

    boolean processMessage(@NonNull Message msg);

    void exit();

    @NonNull
    String getName();
}
