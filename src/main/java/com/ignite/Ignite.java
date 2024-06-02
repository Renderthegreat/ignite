package com.ignite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import fi.iki.elonen.NanoHTTPD;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Ignite implements ModInitializer {
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("ignite");
    private HttpServer httpServer;

    @Override
    public void onInitialize() {
        try {
            if (Files.notExists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                httpServer = new HttpServer(8080, CONFIG_DIR);
                httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                System.out.println("HTTP server started on port 8080");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (httpServer != null) {
                httpServer.stop();
                System.out.println("HTTP server stopped");
            }
        });
    }
}
