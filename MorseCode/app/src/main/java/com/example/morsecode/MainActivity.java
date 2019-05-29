package com.example.morsecode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final Map<Character, String> morseCode = new HashMap<Character, String>();
    static String smsMessage = new String();
    static StringBuilder show = new StringBuilder();


    MainActivity()
    {
        //morseCode = new HashMap<Character, String>();
        morseCode.put('a', ".-");
        morseCode.put('b', "-...");
        morseCode.put('c',  "-.-");
        morseCode.put('d',  "-..");
        morseCode.put('e',    ".");
        morseCode.put('f', "..-.");
        morseCode.put('g',  "--.");
        morseCode.put('h', "....");
        morseCode.put('i',   "..");
        morseCode.put('j', ".---");
        morseCode.put('k',   "-.");
        morseCode.put('l', ".-..");
        morseCode.put('m',   "--");
        morseCode.put('n',   "-.");
        morseCode.put('o',  "---");
        morseCode.put('p', ".--.");
        morseCode.put('q', "--.-");
        morseCode.put('r', ".-.");
        morseCode.put('s',  "...");
        morseCode.put('t',   "-");
        morseCode.put('u',  "..-");
        morseCode.put('v', "...-");
        morseCode.put('w',  ".--");
        morseCode.put('x', "-..-");
        morseCode.put('y', "-.--");
        morseCode.put('z', "--..");
        morseCode.put('1', ".----");
        morseCode.put('2',"..---");
        morseCode.put('3', "...--");
        morseCode.put('4', "....-");
        morseCode.put('5', ".....");
        morseCode.put('6', "-....");
        morseCode.put('7', "--...");
        morseCode.put('8', "---..");
        morseCode.put('9', "----.");
        morseCode.put('0', "-----");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView encodedSMS = (TextView)findViewById(R.id.decodesms);
        final TextView encodedMorseDisplay = (TextView)findViewById(R.id.encodedMorse);
        final EditText editText = (EditText) findViewById(R.id.inputMessage);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    //sendMessage();
                    String message = "----------------------------------- message";
                    String textToDecode = editText.getText().toString();
                    Log.d("Text ", message);
                    Log.d("Text2 ", textToDecode);
                    handled = true;




                    StringBuilder morseEncoding = new StringBuilder();
                    for (int i = 0; i < textToDecode.length(); i++){
                        char c = textToDecode.charAt(i);
                        //Process char
                        morseEncoding.append(morseCode.get(Character.toLowerCase(c))).append(' ');
                    }
                    Log.d("Encoding ", morseEncoding.toString());

                    getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                    final String code = morseEncoding.toString();
                    smsMessage = code;

                    encodedMorseDisplay.setText(code);

                    AsyncTask.execute(new Runnable() {
                        final TextView morseCharacterDisplay = (TextView)findViewById(R.id.morseCharacter);

                        @Override
                        public void run() {
                            Camera cam = Camera.open();
                            Camera.Parameters p = cam.getParameters();

                            final String characters[] = code.split(" ");
                            int position = 0;
                            for (int i = 0; i < code.length(); i++) {
                                if (code.charAt(i) == ' ') {
                                    position++;
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(10);
                                    } catch (Exception e) {
                                        String m = "Could not sleep;";
                                        Log.d("Sleep ", m);
                                    }
                                } else {
                                    final int finalPosition = position;
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            // Stuff that updates the UI
                                            morseCharacterDisplay.setText(characters[finalPosition]);
                                        }
                                    });


                                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    cam.setParameters(p);
                                    cam.startPreview();

                                    try {
                                        if (code.charAt(i) == '.')
                                        {
                                            TimeUnit.MILLISECONDS.sleep(15);
                                        }
                                        if(code.charAt(i) == '_')
                                        {
                                            TimeUnit.MILLISECONDS.sleep(70);

                                        }

                                    } catch (Exception e) {
                                        String m = "Couldd not sleep;";
                                        Log.d("Sleep ", m);
                                    }
                                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    cam.setParameters(p);
                                    cam.stopPreview();
                                }
                                try {
                                    TimeUnit.MILLISECONDS.sleep(100);
                                } catch (Exception e) {
                                    String m = "Couldd not sleep;";
                                    Log.d("Sleep ", m);
                                }
                            }
                            cam.release();
                        }
                    });

                }
                return handled;
            }
        });


        findViewById(R.id.sendsms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("vnd.android-dir/mms-sms");
                sendIntent.putExtra("sms_body", smsMessage);
                startActivity(sendIntent);
            }
        });

        findViewById(R.id.decryptsms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // public static final String INBOX = "content://sms/inbox";
                // public static final String SENT = "content://sms/sent";
                // public static final String DRAFT = "content://sms/draft";
                Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
                String encoded = new String();
                if (cursor.moveToFirst()) { // must check the result to prevent exception
                    do {
                        for(int idx = 0; idx < cursor.getColumnCount(); idx++)
                        {
                            if(cursor.getColumnName(idx).equals("body"))
                            {
                                encoded = cursor.getString(idx);
                                break;
                            }
                        }
                        if (encoded.length() > 0)
                            break;
                    } while (cursor.moveToNext());
                } else {
                    // empty box, no SMS
                }
                Log.d("my message ", encoded);

                show = new StringBuilder();

                String characters[] = encoded.split(" ");
                for(int i = 0; i < characters.length; i++)
                {
                    //Log.d("character", characters[i]);
                    Iterator it = morseCode.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        Log.d("my message decccc ", pair.getValue().toString());
                        if (pair.getValue().toString().equals(characters[i]))
                        {
                            show.append(pair.getKey());
                        }
                        //it.remove(); // avoids a ConcurrentModificationException
                    }
                }

                Log.d("my message dec", show.toString());
                encodedSMS.setText(show.toString());
            }
        });
    }
}
