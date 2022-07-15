package com.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务器端
 */
public class NIOServer {

    public void startServer() throws IOException {
        //1. 创建selector选择器
        Selector selector = Selector.open();
        //2. 创建serversocketchannel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3. 为channel通道绑定监听的端口, 设置为非阻塞模式
        serverSocketChannel.bind(new InetSocketAddress(8000));
        serverSocketChannel.configureBlocking(false);
        //4. 等待通道内连接接入, 注册
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器端已上线");
        //5. 根据就绪状态，调用对应方法实现具体的业务操作
        while (true) {
            // 获取通道数量
            int numOfChannel = selector.select();
            if (numOfChannel == 0) {
                continue;
            }
            // 获取可用channel
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 从set集合移除当前key
                iterator.remove();

                if (key.isAcceptable()) {
                    accept(serverSocketChannel, selector);
                }
                if (key.isReadable()) {
                    read(selector, key);
                }
            }
        }
    }

    /**
     * 处理读状态的操作
     *
     * @param selector
     * @param key
     */
    private void read(Selector selector, SelectionKey key) {
        SocketChannel channel = null;
        try {
            // 从selectionKey中获取到已经就绪的通道
            channel = (SocketChannel) key.channel();
            // 创建buffer用于读取消息
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 读取客户端消息, 字节长度, 大于0即有收到消息
            int read = channel.read(buffer);
            StringBuffer message = new StringBuffer();
            if (read > 0) {
                // 切换读模式
                buffer.flip();
                // 读取内容
                message.append(Charset.forName("UTF-8").decode(buffer));
            }
            // 将channel再次注册到选择器，监听可读状态
            channel.register(selector, SelectionKey.OP_READ);
            // 广播消息给所有客户端
            if (message.length() > 0) {
                System.out.println("向所有客户端广播... " + message);
                broadcasting(message, selector, channel);
            }

        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + "已离线");
                key.cancel();
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //e.printStackTrace();
            System.out.println("连接中断");
        }
    }

    /**
     * 广播
     *
     * @param message
     * @param selector
     * @param channel
     */
    private void broadcasting(StringBuffer message, Selector selector, SocketChannel channel) throws IOException {
        // 获取所有已经接入的客户端
        Set<SelectionKey> keySet = selector.keys();
        // 向所有channel里广播消息
        for (SelectionKey key : keySet) {
            Channel targetChannel = key.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != channel) {
                ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(message.toString()));
            }
        }
    }

    /**
     * 处理接入状态的操作
     *
     * @param serverSocketChannel
     * @param selector
     */
    private void accept(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        //1. 创建socketChannel，设置为非阻塞模式
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        //2. 把channel注册到selector的选择器上，监听可读状态
        channel.register(selector, SelectionKey.OP_READ);
        // 回复客户端
        channel.write(Charset.forName("UTF-8").encode(channel.getRemoteAddress() + "：您已连接到服务器..."));
        System.out.println(channel.getRemoteAddress() + "已上线");
    }

    public static void main(String[] args) {
        try {
            new NIOServer().startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
