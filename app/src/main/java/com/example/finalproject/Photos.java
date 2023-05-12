package com.example.finalproject;

import android.net.Uri;

public class Photos {
    private Uri imageUri;

    public Photos(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
