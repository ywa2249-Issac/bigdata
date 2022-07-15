package com.client;

import java.io.IOException;

public class TestClientBeta {
    public static void main(String[] args) {
        try {
            new NIOClient().startClient("Beta客户端");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
