package com.hxq.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class SocketNioNoBlockingDemo {

    /**
     * 客户端
     * @throws IOException
     */
    @Test
    public  void  client() throws IOException {
        //客户端管道
        SocketChannel socketChannel=SocketChannel.open(new InetSocketAddress("127.0.0.1",9896));
        //设置为非阻塞模式
        socketChannel.configureBlocking(false);
        Scanner scanner=new Scanner(System.in);
        //缓存字节
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        Selector selector = Selector.open();
        socketChannel.register(selector,SelectionKey.OP_READ);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (selector.select()>0){
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = keys.iterator();
                        while (keyIterator.hasNext()){
                            SelectionKey key = keyIterator.next();
                            if(key.isReadable()){
                                SocketChannel socketChannel1= (SocketChannel) key.channel();
                                socketChannel.configureBlocking(false);
                                int len=0;
                                while ((len=socketChannel1.read(byteBuffer))>0){
                                    System.out.println(new String(byteBuffer.array(),0,len));
                                }
                            }
                            keyIterator.remove();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (scanner.hasNext()){
            String inputStr = scanner.next()+new Date().toString();
            System.out.println(inputStr);
            byteBuffer.put(inputStr.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
    }
    /**
     * 服务端
     */
    @Test
    public  void  server() throws IOException {
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9896));
        serverSocketChannel.configureBlocking(false);
        //选择器，监听事件
        Selector selector = Selector.open();
        //将接受客户端通道事件注册到选择器中
        SelectionKey register = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //当监听器有事件时，轮询获取事件
        while (selector.select()>0){
            //获取当前选择器下所有事件
            Set<SelectionKey> keys = selector.selectedKeys();
            //遍历所有事件
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()){
                SelectionKey next = keyIterator.next();
                //连接事件
                if(next.isAcceptable()){
                    //获取客户端管道
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //设置非阻塞模式
                    socketChannel.configureBlocking(false);
                    //将该管道的读请求注册到选择器中
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }
                //读请求事件
                if(next.isReadable()){
                    //获取客户端通道
                    SocketChannel socketChannel = (SocketChannel) next.channel();
                    socketChannel.configureBlocking(false);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
                    int len=0;
                    while ( (len=socketChannel.read(byteBuffer))>0){
                        System.out.println(new String(byteBuffer.array(),0,len));
                    }
                    byteBuffer.clear();
                    byteBuffer.put("已接受完毕".getBytes());
                    byteBuffer.flip();
                    socketChannel.write(byteBuffer);
                }
                //删除事件
                keyIterator.remove();
            }
        }
    }

    @Test
    public void sc(){
        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNext()){
            String s = scanner.next();
            System.out.println(s);
        }
    }
}
