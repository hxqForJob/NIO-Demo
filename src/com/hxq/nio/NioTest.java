package com.hxq.nio;

import javafx.scene.shape.Path;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class NioTest {

    @Test
    public void bufferDemo(){
        String userName="The world is beautiful";
        ByteBuffer  byteBuffer=ByteBuffer.allocate(1024);
        System.out.println("------------allocate-----------------");
        System.out.println("capacity:"+byteBuffer.capacity());
        System.out.println("limit:"+byteBuffer.limit());
        System.out.println("position:"+byteBuffer.position());

        byteBuffer.put(userName.getBytes());
        System.out.println("------------put-----------------");
        System.out.println("capacity:"+byteBuffer.capacity());
        System.out.println("limit:"+byteBuffer.limit());
        System.out.println("position:"+byteBuffer.position());

        byteBuffer.flip();
        System.out.println("------------flip-----------------");
        System.out.println("capacity:"+byteBuffer.capacity());
        System.out.println("limit:"+byteBuffer.limit());
        System.out.println("position:"+byteBuffer.position());

        byte []b = byteBuffer.array();
        System.out.println("------------get-----------------");
        System.out.println("capacity:"+byteBuffer.capacity());
        System.out.println("limit:"+byteBuffer.limit());
        System.out.println("position:"+byteBuffer.position());
        System.out.println("get:"+new String(b));
    }


    /**
     * 非直接缓存拷贝
     */
    @Test
    public void nioCopyDemo(){
        File file=null;
        FileInputStream fI=null;
        FileOutputStream fO=null;
        FileChannel fIChannel=null;
        FileChannel fOChannel=null;
        try {
            file=new File("1.mp4");
            fI=new FileInputStream(file);
           fO=new FileOutputStream("2.mp4");
            fIChannel = fI.getChannel();
            fOChannel = fO.getChannel();
            ByteBuffer byteBuffer =ByteBuffer.allocate(1024*1024);
            while(fIChannel.read(byteBuffer)>0){
                byteBuffer.flip();
                fOChannel.write(byteBuffer);
                byteBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if(fOChannel!=null){
                    fOChannel.close();
                }
                if(fIChannel!=null){
                    fIChannel.close();
                }
                if (fO!=null){
                    fO.close();
                }
                if(fI!=null){
                    fI.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 直接缓存拷贝
     */
    @Test
    public  void  nioCopy2() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.mp4"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.mp4"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        MappedByteBuffer inByteBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outByteBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, outChannel.size());

        byte [] dst=new byte[inByteBuffer.limit()];
        inByteBuffer.get(dst);
        outByteBuffer.put(dst);
        inChannel.close();
        outChannel.close();
    }
}
