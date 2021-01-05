package com.example.greenleaf.ui.purchase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PurchaseViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PurchaseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Liste des livres");
    }

    public LiveData<String> getText() {
        return mText;
    }
}