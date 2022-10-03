package org.example;

import org.example.model.Command;
import org.example.model.Message;
import org.example.network.Client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        get();
    }

    private static void put() {
        new Thread(() -> {
            Path send = Path.of("client", "file.txt");
            try {
                Message message = Message.builder()
                        .command(Command.PUT)
                        .file(send.getFileName().toString())
                        .length(Files.size(send))
                        .data(Files.readAllBytes(send))
                        .build();
                new Client().send(message, resposne -> {
                    System.out.printf("File %s %s", resposne.getFile(), resposne.getStatus());
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static void get() {
        new Thread(() -> {
            Message message = Message.builder()
                    .command(Command.GET)
                    .file("file.txt")
                    .build();
            new Client().send(message, resposne -> {
                Path file = Path.of("client", resposne.getFile());
                try {
                    Files.createFile(file);
                } catch (FileAlreadyExistsException e) {

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try (FileOutputStream output = new FileOutputStream(file.toFile())) {
                    output.write(resposne.getData());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }).start();
    }
}