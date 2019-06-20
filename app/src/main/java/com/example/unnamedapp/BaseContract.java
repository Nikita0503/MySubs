package com.example.unnamedapp;

import android.view.View;

public interface BaseContract {
    interface BaseView{
        void initViews();
        void showMessage(String message);
    }

    interface BasePresenter{
        void onStart();
        void onStop();
    }
}
