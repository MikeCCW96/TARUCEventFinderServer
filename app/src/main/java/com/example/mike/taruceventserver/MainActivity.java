package com.example.mike.taruceventserver;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;
    private Button btnPostEvent;

    public static TextView receiveHex, receiveHex2, convertedHex, convertedHex2,commandCode, commandCode2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPostEvent = (Button)findViewById(R.id.btnPostEvent);

        receiveHex = (TextView)findViewById(R.id.receiveHex);
        convertedHex = (TextView)findViewById(R.id.convertedHex);
        commandCode = (TextView)findViewById(R.id.commandCode);

        receiveHex2 = (TextView)findViewById(R.id.receiveHex2);
        convertedHex2 = (TextView)findViewById(R.id.convertedHex2);
        commandCode2 = (TextView)findViewById(R.id.commandCode2);

        Intent intent = new Intent(MainActivity.this, MqttMessageService.class);
        startService(intent);

        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(getApplicationContext(), MQTTConstants.MQTT_BROKER_URL, MQTTConstants.CLIENT_ID);

        String topic = "MY/TARUC/ERS/000000099/PUB";
        try {
            pahoMqttClient.subscribe(client, topic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        btnPostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, PostEvent.class);
                startActivity(intent2);
                /*
                String messageBefore = "{\"command\": \"000003\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                        "\"category\": \"" + Action.asciiToHex("Meeting") + "\" ," +
                        "\"eventname\": \"" + Action.asciiToHex("Event Name Right Here") + "\"}";
                String messageSend = Action.asciiToHex(messageBefore);
                String topic = "MY/TARUC/ERS/000000099/PUB";

                try {
                    client.publish(topic, messageSend.getBytes() ,0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }*/
            }
        });


    }
}
