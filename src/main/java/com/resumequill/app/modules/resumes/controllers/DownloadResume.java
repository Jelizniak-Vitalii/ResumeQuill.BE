package com.resumequill.app.modules.resumes.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping(value = "/resume")
public class DownloadResume {
  private static final Logger logger = LoggerFactory.getLogger(DownloadResume.class);

  @Value("${node.url}")
  private String nodeUrl;

  @PostMapping(value = "/pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
  public void proxyJson(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    String path = nodeUrl + "/api/resume";
    byte[] body = req.getInputStream().readAllBytes();

    HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(60000);
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Accept", "application/json, text/plain, */*");
    connection.setFixedLengthStreamingMode(body.length);

    try (OutputStream out = connection.getOutputStream()) {
      out.write(body);
    }

    int status = connection.getResponseCode();
    resp.setStatus(status);
    String contentType = connection.getContentType();
    if (contentType != null) {
      resp.setContentType(contentType);
    }
    String contentDisposition = connection.getHeaderField("Content-Disposition");
    if (contentDisposition != null) {
      resp.setHeader("Content-Disposition", contentDisposition);
    }

    try (InputStream in = status >= 400 ? connection.getErrorStream() : connection.getInputStream(); OutputStream out = resp.getOutputStream()) {
      if (in != null) {
        in.transferTo(out);

        logger.info("Downloaded resume");
      }
    } catch (Exception e) {
      logger.error("Error while downloading resume", e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error happened while downloading resume");
    } finally {
      connection.disconnect();
    }
  }
}
