package com.ignite;

import fi.iki.elonen.NanoHTTPD;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpServer extends NanoHTTPD {
    private final Path configDir;

    public HttpServer(int port, Path configDir) {
        super(port);
        this.configDir = configDir;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Path filePath = configDir.resolve(uri.substring(1)); // Remove leading "/"

        if (!Files.exists(filePath)) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File not found");
        }

        if (!Files.isReadable(filePath)) {
            return newFixedLengthResponse(Response.Status.FORBIDDEN, "text/plain", "Access is denied");
        }

        String mimeType;
        try {
            mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
        } catch (IOException e) {
            e.printStackTrace();
            mimeType = "application/octet-stream";
        }

        try {
            FileInputStream fis = new FileInputStream(filePath.toFile());
            return newChunkedResponse(Response.Status.OK, mimeType, fis);
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal server error");
        }
    }
}