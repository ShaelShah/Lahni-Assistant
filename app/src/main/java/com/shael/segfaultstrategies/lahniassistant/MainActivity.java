package com.shael.segfaultstrategies.lahniassistant;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.AlarmClock;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.speech.RecognizerIntent.EXTRA_CALLING_PACKAGE;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL;
import static android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;

public class MainActivity extends Activity {

    //Permission constants
    private final int RECORD_AUDIO_PERMISSION = 1;
    private final int SEND_SMS_PERMISSION = 2;
    private final int PHONE_CALL_PERMISSION = 3;
    private final int SET_ALARM_PERMISSION = 4;
    private final int INTERNET_PERMISSION = 5;

    //Views
    protected Button speakButton;
    protected ImageButton listenButton;
    protected ImageButton phoneButton;
    protected ImageButton messageButton;
    protected TextView instructionTextView;
    protected TextView confirmTextView;
    protected ImageButton settingsButton;
    protected ImageButton cameraImageButton;
    protected ImageView speechProgressImageView;
    protected RelativeLayout relativeLayout;

    //Endpoint
    protected String endpoint = "https://9466009a.ngrok.io/api/v1/instructions/";
    protected String intentID;

    //Speech Recognizer
    protected SpeechRecognizer speechRecognizer;
    protected Intent speechRecognizerIntent;

    //Text to Speech
    protected TextToSpeech textToSpeech;

    //Send sms
    protected SmsManager smsManager;

    //OKHTTP
    protected OkHttpClient client;

    //Variable
    private boolean isTextVisible = true;

    //Emergency Contact Info
    private String name;
    private String number;

    protected String getString;
    protected String postString;
    protected String[] steps;

    //protected TextViewFragment textViewFragment;
    //protected ImageViewFragment imageViewFragment;

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
        confirmTextView = (TextView) findViewById(R.id.confirmTextView);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        listenButton = (ImageButton) findViewById(R.id.listenButton);
        phoneButton = (ImageButton) findViewById(R.id.phoneImageButton);
        messageButton = (ImageButton) findViewById(R.id.messageImageButton);
        cameraImageButton = (ImageButton) findViewById(R.id.cameraImageButton);
        speechProgressImageView = (ImageView) findViewById(R.id.speechProgressImageButton);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);

        /*if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState == null) {
                textViewFragment = new TextViewFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, textViewFragment).commit();
            }
        }*/

        instructionTextView = (TextView) findViewById(R.id.instructionTextView);

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

        client = new OkHttpClient();
        steps = new String[15];

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (isTextVisible) {
                    ImageViewFragment imageViewFragment = new ImageViewFragment();
                    Bundle args = new Bundle();
                    args.putString("URL", "https://cdn1.iconfinder.com/data/icons/dinosaur/154/small-dino-dinosaur-dragon-512.png");
                    args.putString("Message", "This is something you should do");
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
                }*/

                Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
                startActivityForResult(settingsIntent, 1);


                /*try {
                    run("https://8654b187.ngrok.io/api/v1/instructions/");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

               /* try {
                    Log.d("JSON OUTPUT", "{\"query\":\"coffee\",");
                    performPost("https://8654b187.ngrok.io/api/v1/instructions/", "{\"query\":\"I want to watch ellen\"}");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                /*Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String utteranceID = this.hashCode() + "";
                        textToSpeech.speak(postString, TextToSpeech.QUEUE_FLUSH, null, utteranceID);
                    }
                }, 1000);*/

                //String utteranceID = this.hashCode() + "";
                //textToSpeech.speak(postString, TextToSpeech.QUEUE_FLUSH, null, utteranceID);
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = false;
                if (number != null) {
                    if (!number.equals("")) {
                        dialPhoneNumber();
                        success = true;
                    }
                }
                if (!success) {
                    Toast.makeText(getApplicationContext(), "No emergency number set", Toast.LENGTH_LONG).show();
                }
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = false;
                if (number != null) {
                    if (!number.equals("")) {
                        smsManager.sendTextMessage(number, null, "Sending text message to emergency contact", null, null);
                        success = true;
                    }
                }
                if (!success) {
                    Toast.makeText(getApplicationContext(), "No emergency number set", Toast.LENGTH_LONG).show();
                }
            }
        });

        cameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //speechRecognizer.startListening(speechRecognizerIntent);
            }
        });

        client = new OkHttpClient();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                name = data.getStringExtra("Name");
                number = data.getStringExtra("Number");
            }
        }
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

    public void dialPhoneNumber() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
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
        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

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
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION);
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
            case INTERNET_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                break;
        }
    }

    public void performGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //TextView myTextView = (TextView) findViewById(R.id.confirmTextView);
                            //myTextView.setText(responseData);
                            getString = responseData;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void performPost(String url, final String json) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        //Log.d("OUTPUT", json);

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(responseData);

                            Log.d("JOBJECTOUTPUT", jObject.toString());

                            if (jObject.has("steps")) {
                                ArrayList<String> listData = new ArrayList<>();
                                JSONArray jArray = jObject.getJSONArray("steps");
                                if (jArray != null) {
                                    for (int i = 0; i < jArray.length(); i++) {
                                        listData.add(jArray.getString(i));
                                    }
                                }

                                for (String s : listData) {
                                    Log.d("ARRAY OUTPUT", s);
                                    textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");

                                    //speechRecognizer.startListening(speechRecognizerIntent);
                                }


                            }
                            postString = jObject.getString("message");
                            intentID = jObject.getString("intentID");
                        } catch (JSONException e) {
                            Log.d("JSON EXCEPTION", "We failed fam");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}


                            /*for (int i = 0; i <steps.length; i++) {
                                //performPost(endpoint, "{\"query\":\"" + steps[i] + "\"}");
                                String utteranceId = this.hashCode() + "";
                                textToSpeech.speak(steps[i], TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                            }*/