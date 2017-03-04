package com.shael.segfaultstrategies.lahniassistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.speech.RecognizerIntent.EXTRA_CALLING_PACKAGE;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL;
import static android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;

public class MainActivity extends Activity {

    //Permission constants
    private final int RECORD_AUDIO_PERMISSION = 1;
    private final int SEND_SMS_PERMISSION = 2;
    private final int PHONE_CALL_PERMISSION = 3;

    //Views
    private Button speakButton;
    private Button listenButton;
    private TextView outputTextView;
    private ImageButton settingsButton;

    //Speech Recognizer
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    //Text to Speech
    private TextToSpeech textToSpeech;

    //Send sms
    private SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }

        //View instantiation
        speakButton = (Button) findViewById(R.id.speakButton);
        outputTextView = (TextView) findViewById(R.id.outputTextView);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        listenButton = (Button) findViewById(R.id.listenButton);

        //Speak Recognizer instantiation
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(EXTRA_CALLING_PACKAGE, this.getPackageName());

        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        speechRecognizer.setRecognitionListener(listener);

        //Send SMS
        smsManager = SmsManager.getDefault();

        //Text to Speech instantiation
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
//                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.setLanguage(Locale.FRENCH);
            }
        });

        //OnClick listeners
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (outputTextView.getText() != null || !outputTextView.getText().toString().equals("")) {
                    String utteranceId = this.hashCode() + "";
                    textToSpeech.speak(outputTextView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    //smsManager.sendTextMessage("4167704050", null, "This is the text message that I am sending", null, null);
    //dialPhoneNumber("6473837027");
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        PackageManager packageManager = this.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (int j = 0 ; j < activities.size() ; j++) {
            if (activities.get(j).toString().toLowerCase().contains("com.android.phone")) {
                intent.setPackage("com.android.phone");
            }
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void requestPermissions() {
        //Permissions
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int sendSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int phoneCallPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (recordAudioPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION);
            }
        }
        if (sendSmsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION);
            }
        }
        if (phoneCallPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, PHONE_CALL_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
            case SEND_SMS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
            case PHONE_CALL_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
        }
    }

    protected class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {}

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float v) {}

        @Override
        public void onBufferReceived(byte[] bytes) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int i) {
            //speechRecognizer.startListening(speechRecognizerIntent);
            outputTextView.setText("Error listening, please ask again.");
        }

        @Override
        public void onResults(Bundle results) {
            float[] scores  = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            int maxIndex = 0;

            for (int i = 1; i < scores.length; i++) {
                if (scores[i] > scores[i - 1]) {
                    maxIndex = i;
                }
            }
            outputTextView.setText("[" + maxIndex + "]" + " " + matches.get(maxIndex));
        }

        @Override
        public void onPartialResults(Bundle bundle) {}

        @Override
        public void onEvent(int i, Bundle bundle) {}
    }
}
