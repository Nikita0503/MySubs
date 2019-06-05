package com.example.unnamedapp;

import android.view.View;

public interface BaseContract {
    interface BaseView{
        void initViews();
    }

    interface BasePresenter{
        void onStart();
        void onStop();
    }
}
