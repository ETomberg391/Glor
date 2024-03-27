package com.example.glor;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.database.Cursor;
import android.os.Build;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.appcompat.app.AlertDialog;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final int RECORD_AUDIO_REQUEST_CODE = 1;
    private WebView webView;
    private WebAppInterface webAppInterface;
    private AudioRecorder audioRecorder;
    private SharedPreferences sharedPreferences;

    private void enableAutoplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        enableAutoplay(); // Add this line

        webAppInterface = new WebAppInterface(this);
        webView.addJavascriptInterface(webAppInterface, "Android");

        // Set a custom WebViewClient to bypass SSL certificate validation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // Bypass SSL certificate validation
                handler.proceed();
            }
        });

        webView.loadUrl("file:///android_asset/www/index.html");

        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);

        requestPermissions();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do nothing
            } else {
                Log.e("MainActivity", "Audio recording permission denied");
                Toast.makeText(this, "Please grant audio recording permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class WebAppInterface {
        private MainActivity activity;

        public WebAppInterface(MainActivity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void saveSettings(String endpointUrl, String openaiApiKey, int maxTokens, double temperature) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("endpointUrl", endpointUrl);
            editor.putString("openaiApiKey", openaiApiKey);
            editor.putInt("maxTokens", maxTokens);
            editor.putFloat("temperature", (float) temperature);
            editor.apply();
        }

        @JavascriptInterface
        public String getSettings(String key) {
            switch (key) {
                case "endpointUrl":
                    return sharedPreferences.getString("endpointUrl", "https://10.9.0.3:5000");
                case "openaiApiKey":
                    return sharedPreferences.getString("openaiApiKey", "");
                case "maxTokens":
                    return String.valueOf(sharedPreferences.getInt("maxTokens", 2000));
                case "temperature":
                    return String.valueOf(sharedPreferences.getFloat("temperature", 0.7f));
                default:
                    return null;
            }
        }

        @JavascriptInterface
        public void startRecording() {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                audioRecorder = new AudioRecorder(activity.getApplicationContext());
                audioRecorder.startRecording();
            } else {
                Toast.makeText(activity, "Please grant audio recording permission", Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void stopRecording() {
            if (audioRecorder != null) {
                Uri audioUri = audioRecorder.stopRecording();
                if (audioUri != null) {
                    // Convert the audioUri to a byte array or blob
                    byte[] audioBytes = getAudioBytesFromUri(audioUri);

                    // Call the JavaScript function to send the audio to the server on the main UI thread
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.evaluateJavascript("javascript:sendAudioToServer(new Blob([new Uint8Array(" + Arrays.toString(audioBytes) + ")], { type: 'audio/mpeg' }))", null);
                        }
                    });
                }
            }
        }
    }

    private byte[] getAudioBytesFromUri(Uri audioUri) {
        String audioFilePath = getAudioFilePathFromUri(audioUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(audioFilePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
        return baos.toByteArray();
    }

    private class AudioRecorder {
        private MediaRecorder mediaRecorder;
        private Uri audioUri;
        private Context context;

        AudioRecorder(Context context) {
            this.context = context;
        }

        void startRecording() {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                String fileName = generateFileName();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/recordings");

                audioUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

                try {
                    ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(audioUri, "w");
                    if (pfd != null) {
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.setAudioEncodingBitRate(128000); // Increase bit rate for better quality
                        mediaRecorder.setAudioSamplingRate(44100); // Set sampling rate to 44.1 kHz
                        mediaRecorder.setOutputFile(pfd.getFileDescriptor());

                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception gracefully
                    stopRecording();
                }
            } else {
                // Permission is not granted, handle accordingly (e.g., request permission or show an error)
                Log.e("AudioRecorder", "Record audio permission not granted");
            }
        }

        Uri stopRecording() {
            if (mediaRecorder != null) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    return audioUri; // Return the audioUri instead of null
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    // Handle the exception gracefully
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
            }
            return null; // Return null if mediaRecorder is null
        }

        private String generateFileName() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());
            return "recording_" + timestamp + ".mp3";
        }
    }

    private String getAudioFilePathFromUri(Uri audioUri) {
        String filePath = null;
        Cursor cursor = getContentResolver().query(audioUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    private void sendAudioToServer(Uri audioUri) {
        OkHttpClient client = new OkHttpClient();

        // Create a RequestBody for the audio file
        File audioFile = new File(getAudioFilePathFromUri(audioUri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio_file", audioFile.getName(), requestBody);

        // Create the MultipartBody
        RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(body)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url("https://192.168.1.193:5000/v1/audio/speechAndTextGenerations")
                .post(multipartBody)
                .build();

        try {
            // Send the request and handle the response
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (response.isSuccessful()) {
                // Save the .wav file
                saveWavFile(responseBody.byteStream());

                // Process the text response
                String text = responseBody.string();
                processTextResponse(text);
            } else {
                // Handle error
                Log.e("MainActivity", "Server error: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveWavFile(InputStream inputStream) {
        try {
            // Create a file in the app's external storage directory
            File externalFilesDir = getExternalFilesDir(null);
            if (externalFilesDir == null) {
                Log.e("MainActivity", "Failed to get external files directory");
                return;
            }

            File wavFile = new File(externalFilesDir, "response.wav");

            // Create the parent directories if they don't exist
            File parentDir = wavFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Write the input stream to the file
            byte[] buffer = new byte[1024];
            int bytesRead;
            FileOutputStream outputStream = new FileOutputStream(wavFile);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            // Display a success message
            Toast.makeText(this, "WAV file saved: " + wavFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save WAV file", Toast.LENGTH_SHORT).show();
        }
    }

    private void processTextResponse(String text) {
        // Create a new TextView to display the text
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 16);

        // Create a ScrollView to make the text scrollable
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(textView);

        // Create an AlertDialog to display the ScrollView
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Text Response")
                .setView(scrollView)
                .setPositiveButton("OK", null)
                .show();
    }
}