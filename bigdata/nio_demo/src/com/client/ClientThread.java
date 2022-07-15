package com.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ClientThread implements Runnable {
    private Selector selector;

    public ClientThread(Selector selector) {
        this.selector = selector;
    }

    /**
     * 处理读状态的操作
     *
     * @param selector
     * @param key
     */
    private void read(Selector selector, SelectionKey key) throws IOException {
        // 从selectionKey中获取到已经就绪的通道
        SocketChannel channel = (SocketChannel) key.channel();
        // 创建buffer用于读取消息
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 读取服务器端消息, 字节长度, 大于0即有收到消息
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
        if (message.length() > 0) {
            System.out.println("收到来自服务器的消息： " + message);
        }
    }

    @Override
    public void run() {
        try{
            while(true){
                int numOfChannel = selector.select();
                if(numOfChannel == 0){
                    continue;
                }
                // 获取可用channel
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 从set集合移除当前key
                    iterator.remove();
                    if (key.isReadable()) {
                        read(selector, key);
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
