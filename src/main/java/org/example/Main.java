package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Main {

    public static void main(String[] args) {

    }

    public static void client() throws IOException {
        File file = new File("");
        FileChannel fileChannel = new FileInputStream(file).getChannel();
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(9999));

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(file.length());
        buffer.flip();
        socketChannel.write(buffer);

        long total = 0;
        while (total < file.length()) {
            long transferred = fileChannel.transferTo(total, file.length() - total, socketChannel);
            total += transferred;
        }
    }

    public static void server() throws IOException {
        File file = new File("in-file");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        SocketChannel socketChannel = serverSocketChannel.accept();

        ByteBuffer buffer = ByteBuffer.allocate(8);
        socketChannel.read(buffer);
        buffer.flip();
        long size = buffer.getLong();

        FileChannel fileChannel = new FileOutputStream(file).getChannel();

        long total = 0;
        while (total < size) {
            long transfer = fileChannel.transferFrom(socketChannel, total, size - total);
            if (transfer <= 0) {
                break;
            }
            total =+ transfer;
        }
    }
}