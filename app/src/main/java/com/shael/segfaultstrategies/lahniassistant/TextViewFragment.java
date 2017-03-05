package com.shael.segfaultstrategies.lahniassistant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextViewFragment extends Fragment {

    private String message;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            message = getArguments().getString("Message");
        }
        return inflater.inflate(R.layout.textview_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (message != null) {
            if (!message.equals("")) {
                textView = ((TextView) this.getView().findViewById(R.id.instructionTextView));
                textView.setText(message);
            }
        }
    }

    public void setTextView(String message) {
        this.textView.setText(message);
    }
}
