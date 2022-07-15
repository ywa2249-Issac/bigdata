package com.client;

import java.io.IOException;

public class TestClientGamma {
    public static void main(String[] args) {
        try {
            new NIOClient().startClient("Gamma客户端");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
