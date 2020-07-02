package com.hxq.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ScoketNIOBlockingDemo {

    @Test
    public void  client() throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1",9896);
        SocketChannel socketChannel=SocketChannel.open(socketAddress);
        FileChannel fileChannel = FileChannel.open(Paths.get("1.mp4"), StandardOpenOption.READ);
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024*1024);
        while (fileChannel.read(byteBuffer)!=-1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        socketChannel.shutdownOutput();

       int len=0;
       while ((len=socketChannel.read(byteBuffer))>0){
            byteBuffer.flip();
           System.out.println(new String(byteBuffer.array(), 0, len));
           byteBuffer.clear();
       }
        fileChannel.close();
        socketChannel.close();
    }

    @Test
    public void  server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9896));
        FileChannel fileChannel = FileChannel.open(Paths.get("3.mp4"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        SocketChannel socketChannel = serverSocketChannel.accept();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024*1024);
        while (socketChannel.read(byteBuffer)!=-1){
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.clear();
        }
        socketChannel.shutdownInput();

       byteBuffer.put("服务器接受完毕".getBytes());
       byteBuffer.flip();
        socketChannel.write(byteBuffer);

        socketChannel.close();
        fileChannel.close();
        serverSocketChannel.close();
    }
}
