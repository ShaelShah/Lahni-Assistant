package com.shael.segfaultstrategies.lahniassistant;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;

/**
 * Created by root on 04/03/17.
 */
class SpeechRecognitionListener implements RecognitionListener {

    private MainActivity mainActivity;

    public SpeechRecognitionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        mainActivity.confirmTextView.setText("Ready for text");
    }

    @Override
    public void onBeginningOfSpeech() {
        mainActivity.confirmTextView.setText("You have started speaking");
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
    }

    @Override
    public void onError(int i) {
        //speechRecognizer.startListening(speechRecognizerIntent);
        mainActivity.confirmTextView.setText("Error listening, please ask again.");

        String utteranceId = this.hashCode() + "";
        mainActivity.textToSpeech.speak("I'm sorry, I didn't get that.", TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onResults(Bundle results) {
        float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        int maxIndex = 0;

        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > scores[i - 1]) {
                maxIndex = i;
            }
        }
        mainActivity.confirmTextView.setText("[" + maxIndex + "]" + " " + matches.get(maxIndex));
    }

    @Override
    public void onPartialResults(Bundle bundle) {
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }
}
