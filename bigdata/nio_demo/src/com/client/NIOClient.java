package com.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * 客户端
 */
public class NIOClient {
    public void startClient(String name) throws IOException {
        // 连接服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));
        // 接收来自服务器的相应消息：server connected...
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 创建线程
        new Thread(new ClientThread(selector)).start();
        // 向服务端发送消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            if (message.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(name + "发送消息：" + message));
            }
        }

    }
    /*
    public static void main(String[] args) {
        try {
            new NIOClient().startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
