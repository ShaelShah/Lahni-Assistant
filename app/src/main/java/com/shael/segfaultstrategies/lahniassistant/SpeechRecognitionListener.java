package com.shael.segfaultstrategies.lahniassistant;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

class SpeechRecognitionListener implements RecognitionListener {

    private MainActivity mainActivity;

    public SpeechRecognitionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        mainActivity.confirmTextView.setText("Ready for text");
        mainActivity.speechProgressImageView.setImageResource(R.drawable.ic_signal_cellular_1_bar_black_24dp);
    }

    @Override
    public void onBeginningOfSpeech() {
        mainActivity.confirmTextView.setText("You have started speaking");
        mainActivity.speechProgressImageView.setImageResource(R.drawable.ic_signal_cellular_2_bar_black_24dp);
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        mainActivity.confirmTextView.setText("Started receiving data");
    }

    @Override
    public void onEndOfSpeech() {
        mainActivity.confirmTextView.setText("You have stopped talking");
        mainActivity.speechProgressImageView.setImageResource(R.drawable.ic_signal_cellular_3_bar_black_24dp);
    }

    @Override
    public void onError(int i) {
        mainActivity.speechProgressImageView.setImageAlpha(R.drawable.ic_signal_cellular_0_bar_black_24dp);

        String utteranceId = this.hashCode() + "";
        mainActivity.textToSpeech.speak("I'm sorry, I didn't get that.", TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    /*@Override
    public void onResults(Bundle results) {
        String hashCode = hashCode() + "";
        switch (mainActivity.globalHackCount) {
            case 1:
                mainActivity.textToSpeech.speak("On which device would you like to watch?", TextToSpeech.QUEUE_FLUSH, null, hashCode);
                mainActivity.globalHackCount++;
                break;
            case 2:
                mainActivity.textToSpeech.speak("Do you have the remote?", TextToSpeech.QUEUE_FLUSH, null, hashCode);
                mainActivity.globalHackCount++;
                break;
            case 3:
                mainActivity.textToSpeech.speak("On which device would you like to watch?", TextToSpeech.QUEUE_FLUSH, null, hashCode);
                mainActivity.globalHackCount++;
                break;
            case 4:
                mainActivity.textToSpeech.speak("On which device would you like to watch?", TextToSpeech.QUEUE_FLUSH, null, hashCode);
                mainActivity.globalHackCount++;
                break;
            case 5:
                mainActivity.textToSpeech.speak("On which device would you like to watch?", TextToSpeech.QUEUE_FLUSH, null, hashCode);
                mainActivity.globalHackCount++;
                break;
            case 6:
                mainActivity.textToSpeech.speak("On which device would you like to watch?", TextToSpeech.QUEUE_FLUSH, null, hashCode);
                mainActivity.globalHackCount++;
                break;
        }
    }*/

    @Override
    public void onResults(Bundle results) {
            mainActivity.speechProgressImageView.setImageResource(R.drawable.ic_signal_cellular_4_bar_black_24dp);
            float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            int maxIndex = 0;

            for (int i = 1; i < scores.length; i++) {
                if (scores[i] > scores[i - 1]) {
                    maxIndex = i;
                }
            }
            mainActivity.confirmTextView.setText(matches.get(maxIndex));
            String json = "{\"query\":\"" + matches.get(maxIndex) + "\"}";

            try {
                mainActivity.performPost(mainActivity.endpoint, json);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mainActivity.instructionTextView.setText(mainActivity.postString);
                        String utteranceID = this.hashCode() + "";
                        mainActivity.textToSpeech.speak(mainActivity.postString, TextToSpeech.QUEUE_FLUSH, null, utteranceID);
                        //mainActivity.postString = "";
                    }
            }, 2000);
        } catch (IOException e) {
                //Log.d("", "" + e.printStackTrace());
                Log.d("ERRORERROR", "E.PRINTSTACKTRACE");
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }
}
