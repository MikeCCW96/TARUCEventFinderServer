package com.example.mike.taruceventserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class PostEvent extends AppCompatActivity {
    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;
    Button btnMeeting, btnAnnual, btnClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_event);

        btnMeeting = (Button)findViewById(R.id.btnMeeting);
        btnAnnual = (Button)findViewById(R.id.btnAnnual);
        btnClass = (Button)findViewById(R.id.btnClass);

        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(getApplicationContext(), MQTTConstants.MQTT_BROKER_URL, MQTTConstants.CLIENT_ID);

        btnMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBefore = "{\"command\": \"000003\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                        "\"category\": \"" + Action.asciiToHex("Meeting") + "\" ," +
                        "\"eventname\": \"" + Action.asciiToHex("Smartcampus Meeting") + "\"}";
                String messageSend = Action.asciiToHex(messageBefore);
                String topic = "MY/TARUC/ERS/000000099/PUB";
                try {
                    client.publish(topic, messageSend.getBytes() ,0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

        btnAnnual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBefore = "{\"command\": \"000003\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                        "\"category\": \"" + Action.asciiToHex("Annual") + "\" ," +
                        "\"eventname\": \"" + Action.asciiToHex("Yearly TARUC Meet") + "\"}";
                String messageSend = Action.asciiToHex(messageBefore);
                String topic = "MY/TARUC/ERS/000000099/PUB";
                try {
                    client.publish(topic, messageSend.getBytes() ,0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

        btnClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBefore = "{\"command\": \"000003\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                        "\"category\": \"" + Action.asciiToHex("Class") + "\" ," +
                        "\"eventname\": \"" + Action.asciiToHex("Guitar Class") + "\"}";
                String messageSend = Action.asciiToHex(messageBefore);
                String topic = "MY/TARUC/ERS/000000099/PUB";
                try {
                    client.publish(topic, messageSend.getBytes() ,0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
