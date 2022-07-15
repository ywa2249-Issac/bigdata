package com.client;

import java.io.IOException;

public class TestClientAlpha {
    public static void main(String[] args) {
        try {
            new NIOClient().startClient("Alpha客户端");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
