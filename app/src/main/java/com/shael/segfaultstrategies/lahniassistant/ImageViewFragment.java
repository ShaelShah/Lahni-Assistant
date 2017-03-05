package com.shael.segfaultstrategies.lahniassistant;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewFragment extends Fragment {

    private String URL;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        URL = getArguments().getString("URL");
        return inflater.inflate(R.layout.imageview_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ImageView) this.getView().findViewById(R.id.imageView)).setBackgroundColor(Color.BLUE);
    }

}
