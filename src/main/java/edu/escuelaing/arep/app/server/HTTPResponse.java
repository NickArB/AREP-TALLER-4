package edu.escuelaing.arep.app.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The `HTTPResponse` class provides methods for generating HTTP responses, including headers and content,
 * to be sent by an HTTP server. It includes methods to create different types of responses such as OK, Not Found,
 * and Internal Server Error, as well as methods to set the content type and send files as responses.
 * @author Nicolas Ariza Barbosa
 */
public class HTTPResponse {

    private String CONTENT_TYPE;
    private String[] mediaFilesLst = {"jpeg", "png", "ico", "jpg", "gif"};
    private Socket socketClient = null;
    private String STATIC_PATH = "web-files";
    private String RESPONSE = "";
    private String HEADER = "";
    private String BODY = "";
    private String LENGTH = "";

    /**
     * Constructs an `HTTPResponse` object with the specified content type.
     * @param contentType The content type for the HTTP response.
     */
    public HTTPResponse(String contentType, Socket client){
        this.CONTENT_TYPE = contentType;
        this.socketClient = client;
    }

    public void setClient(Socket client){
        this.socketClient = client;
    }

    /**
     * Generates the HTTP response headers for a successful (OK) response with the specified content type.
     * @return The HTTP response headers for a successful response.
     */
    public String OKResponse(){
        String reponse = "HTTP/1.1 200 OK\r\n" +
                        "Cache-Control: no-store \r\n" +
                        "Pragma: no-cache \r\n" +
                        "Content-Type: text/" + this.CONTENT_TYPE + "\r\n\r\n";

        for(String contentType: this.mediaFilesLst){
            if(CONTENT_TYPE.equals(contentType)){
                return "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: image/" + this.CONTENT_TYPE + "\r\n\r\n";
            }
        }
        return reponse;
    }

    /**
     * Generates the HTTP response headers for a "created" response with the specified content type.
     * @return The HTTP response headers for a "created" response.
     */
    public String createdResponse(){
        String response = "HTTP/1.1 201 Created\r\n"
                    + "Content-Type: text/" + this.CONTENT_TYPE +"\r\n"
                    + "\r\n";
        return response;
    }

    /**
     * Generates the HTTP response headers for a "not found" response with the specified content type.
     * @return The HTTP response headers for a "not found" response.
     */
    public String NotFoundResponse(){
        return "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/" + this.CONTENT_TYPE + "\r\n" +
                "\r\n";
    }

    /**
     * Sets the content type for the HTTP response headers.
     * @param newContentType The new content type to be set.
     */
    public void setContentType(String newContentType) {
        this.CONTENT_TYPE = newContentType;
    }

    /**
     * Generates the HTML content for the index page, which includes a form to query movies and a
     * div to display the API response as an HTML table.
     * @param requestedfile The URI of the requested file
     * @param client The socket used to communicate with the client
     * @throws IOException 
     */
    public void sendFile(URI requestedFile, Socket client) throws IOException {
        OutputStream out = client.getOutputStream();
        Path imagePath = Paths.get("web-files", requestedFile.getPath());
        byte[] imageData = Files.readAllBytes(imagePath);
        out.write(imageData);
        out.flush();
        out.close();
    }


    /**
     * Calls the HTML content for the "not found" page, indicating that the requested resource does not exist.
     * @throws URISyntaxException 
     * @throws IOException 
     */
    public void sendNotFoundPage() throws IOException, URISyntaxException{
        sendFile(new URI("/not-found.html"), socketClient);
    }

    public void internalServerError() throws IOException{
        try {
            sendFile(new URI("/internal-server-error.html"), socketClient);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setStaticPath(String path){
        this.STATIC_PATH = path;
    }

    public String getStaticPath(){
        return this.STATIC_PATH;
    }

    public void setBody(String newBody){
        this.BODY = newBody.replace("\r\n", "");
    }

    public String getBody(){
        return this.BODY;
    }

    public void setHeader(String newHeader){
        this.HEADER = newHeader;
    }

    public String getHeader(){
        return this.HEADER;
    }

    public void setContentLength(String newLength){
        this.LENGTH = newLength;
    }

    public String getContentLength(){
        return this.LENGTH;
    }

    public String createAndGetResponse(){
        RESPONSE = "";
        RESPONSE += HEADER;
        RESPONSE += BODY;
        return RESPONSE;
    }

    
}
