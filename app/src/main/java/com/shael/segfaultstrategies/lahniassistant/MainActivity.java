package com.shael.segfaultstrategies.lahniassistant;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private final int SET_ALARM_PERMISSION = 4;

    //Views
    protected Button speakButton;
    protected ImageButton listenButton;
    protected TextView instructionTextView;
    protected TextView confirmTextView;
    protected ImageButton settingsButton;
    protected RelativeLayout relativeLayout;

    //Speech Recognizer
    protected SpeechRecognizer speechRecognizer;
    protected Intent speechRecognizerIntent;

    //Text to Speech
    protected TextToSpeech textToSpeech;

    //Send sms
    protected SmsManager smsManager;

    //Variable
    private boolean isTextVisible = true;

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
        //instructionTextView = (TextView) findViewById(R.id.instructionTextView);
        confirmTextView = (TextView) findViewById(R.id.confirmTextView);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        listenButton = (ImageButton) findViewById(R.id.listenButton);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState == null) {
                TextViewFragment fragment = new TextViewFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
            }
        }

        //Speak Recognizer instantiation
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(EXTRA_CALLING_PACKAGE, this.getPackageName());

        SpeechRecognitionListener listener = new SpeechRecognitionListener(this);
        speechRecognizer.setRecognitionListener(listener);

        //Send SMS
        smsManager = SmsManager.getDefault();

        //Text to Speech instantiation
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.UK);
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
                if (instructionTextView.getText() != null || !instructionTextView.getText().toString().equals("")) {
                    String utteranceId = this.hashCode() + "";
                    textToSpeech.speak(instructionTextView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTextVisible) {
                    ImageViewFragment imageViewFragment = new ImageViewFragment();
                    Bundle args = new Bundle();
                    args.putString("URL", "URL to pass");
                    imageViewFragment.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, imageViewFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    isTextVisible = false;
                } else {
                    TextViewFragment textViewFragment = new TextViewFragment();
                    Bundle args = new Bundle();
                    args.putString("Message", "Message to put");
                    textViewFragment.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, textViewFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    isTextVisible = true;
                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //speechRecognizer.startListening(speechRecognizerIntent);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void startTimer(String message, int seconds) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                            .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                            .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                            .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        PackageManager packageManager = this.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (int j = 0 ; j < activities.size() ; j++) {
            if (activities.get(j).toString().toLowerCase().contains("android.provider.AlarmClock")) {
                intent.setPackage("android.provider.AlarmClock");
            }
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
        int setAlarmCallPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM);

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
        if (setAlarmCallPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SET_ALARM)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SET_ALARM}, SET_ALARM_PERMISSION);
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
            case SET_ALARM_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
        }
    }

}
