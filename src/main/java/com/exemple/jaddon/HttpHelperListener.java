package com.exemple.jaddon;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.TaskListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelperListener implements TaskListener {

    private Expression kong;
    private Expression payload;

    @Override
    public void notify(DelegateTask delegateTask) {
        String kongUrl = (String) kong.getValue(delegateTask);
        String payloadContent = (String) payload.getValue(delegateTask);

        HttpURLConnection conn = null;
        try {
            URL urlObj = new URL(kongUrl);
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer 1234564566878e");
            conn.setRequestProperty("client_id", "456");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payloadContent.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // Log or handle the response as needed
                System.out.println("Response: " + response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log the error appropriately
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // Setters for field injection with Expression types
    public void setKong(Expression kong) {
        this.kong = kong;
    }

    public void setPayload(Expression payload) {
        this.payload = payload;
    }
}