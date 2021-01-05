package com.example.greenleaf.ui.sales;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SalesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SalesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Liste des livres demandé au acheté");
    }

    public LiveData<String> getText() {
        return mText;
    }
}