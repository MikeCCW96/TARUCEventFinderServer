package com.example.mike.taruceventserver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MqttMessageService extends Service {

    private static final String TAG = "MqttMessageService";
    private PahoMqttClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;
    MqttAndroidClient client;
    static String topicStr = "MY/TARUC/ERS/000000099/PUB";
    String usernameWhole = "";





    public MqttMessageService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        pahoMqttClient = new PahoMqttClient();
        mqttAndroidClient = pahoMqttClient.getMqttClient(getApplicationContext(), MQTTConstants.MQTT_BROKER_URL, MQTTConstants.CLIENT_ID);
        client = pahoMqttClient.getMqttClient(this, MQTTConstants.MQTT_BROKER_URL, MQTTConstants.CLIENT_ID);


        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {



                String message = Action.hexToAscii(new String(mqttMessage.getPayload()));



                JSONObject jsonObject = new JSONObject(message);
                String command = jsonObject.getString("command");



                String messageSend = "";

                if (command.equals("000001")){
                    String username = Action.hexToAscii(jsonObject.getString("username"));
                    String password = Action.hexToAscii(jsonObject.getString("password"));

                    MainActivity.receiveHex.setText(new String(mqttMessage.getPayload()));
                    MainActivity.convertedHex.setText(message);
                    MainActivity.commandCode.setText(command);

                    usernameWhole = username;

                    String type = "login";
                    LoginTask loginTask = new LoginTask(MqttMessageService.this);
                    loginTask.execute(type, username, password);
                } else if (command.equals("000002")){

                    MainActivity.receiveHex2.setText(new String(mqttMessage.getPayload()));
                    MainActivity.convertedHex2.setText(message);
                    MainActivity.commandCode2.setText(command);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {

        //.setContentIntent(pendingIntent).build();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(topic)
                        .setContentText(msg);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }

    public class LoginTask extends AsyncTask<String, Void, String> {

        Context context;
        android.app.AlertDialog alertDialog;
        LoginTask(Context ctx){
            context = ctx;
        }
        ProgressDialog pd;
        String studentId = "";

        Connectivity connectivity = new Connectivity();

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String ipAddress = "" + connectivity.getIP();
            String login_url = ipAddress + "/Android/login2.php";

            if(type.equals("login")){
                try {
                    String username = params[1];
                    String password = params[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+"&"+
                            URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result="";
                    String line="";
                    while ((line = bufferedReader.readLine()) != null){
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(final String result) {

            if(result.equals("fail")){
                String messageBefore = "{\"command\": \"000002\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                        "\"username\": \"" + Action.asciiToHex(usernameWhole) + "\" ," +
                        "\"password\": \"" + Action.asciiToHex("fail") + "\"}";
                String messageSend = Action.asciiToHex(messageBefore);
                String topic = topicStr;

                try {
                    client.publish(topic, messageSend.getBytes() ,0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                            JSONObject response = new JSONObject(result);
                            studentId = response.getString("studentId");
                            String type = "Retrieve";
                            RetrieveEventTask retrieveEventTask = new RetrieveEventTask(context);
                            retrieveEventTask.execute(type, studentId);
                            Toast.makeText(context,"Login Success", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


        }

    public class RetrieveEventTask extends AsyncTask<String, Void, String> {
        Context context;
        android.app.AlertDialog alertDialog;
        String studentId = "";

        RetrieveEventTask(Context ctx) {
            context = ctx;
        }
        Connectivity connectivity = new Connectivity();

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String ipAddress = "" + connectivity.getIP();
            String retrieve_url = ipAddress + "/Android/retrieveEvent.php";

            if (type.equals("Retrieve")) {
                try {
                    studentId = params[1];
                    URL url = new URL(retrieve_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("", "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            ArrayList<String> IdArray = new ArrayList<>();
            ArrayList<String> titleArray = new ArrayList<>();
            ArrayList<String> descriptionArray = new ArrayList<>();
            String eventId = "";
            String eventTitle = "";
            String eventDescription = "";
            try {
                JSONObject response = new JSONObject(result);
                eventId = response.getString("EventId");
                eventTitle = response.getString("EventTitle");
                eventDescription = response.getString("EventDescription");

                JSONArray jsonArrayId = new JSONArray(eventId);
                JSONArray jsonArrayTitle = new JSONArray(eventTitle);
                JSONArray jsonArrayDescription = new JSONArray(eventDescription);

                for (int i = 0; i < jsonArrayId.length(); i++) {
                    JSONObject jsonObjectId = jsonArrayId.optJSONObject(i);
                    JSONObject jsonObjectTitle = jsonArrayTitle.optJSONObject(i);
                    JSONObject jsonObjectDescription = jsonArrayDescription.optJSONObject(i);
                    if(jsonObjectId == null) {
                        continue;
                    }
                    String jsonValueId = jsonObjectId.optString("EventId");
                    String jsonValueTitle = jsonObjectTitle.optString("EventTitle");
                    String jsonValueDescription = jsonObjectDescription.optString("EventDescription");
                    IdArray.add(jsonValueId);
                    titleArray.add(jsonValueTitle);
                    descriptionArray.add(jsonValueDescription);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            String messageBefore = "{\"command\": \"000002\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                    "\"result\": \"" + Action.asciiToHex(result) + "\" ," +
                    "\"username\": \"" + Action.asciiToHex(usernameWhole) + "\" ," +
                    "\"password\": \"" + Action.asciiToHex("") + "\" ," +
                    "\"studentId\": \"" + Action.asciiToHex(studentId) + "\"}";
            String messageSend = Action.asciiToHex(messageBefore);

            String topic = topicStr;

            try {
                client.publish(topic, messageSend.getBytes() ,0,false);
            } catch (MqttException e) {
                e.printStackTrace();
            }


        }
    }


    }



