package edu.escuelaing.arep.app.server;

import edu.escuelaing.arep.app.reflex.ReflexiveManager;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

/**
 * The `HTTPServer` class represents a simple HTTP server that handles client connections.
 * It uses multiple threads to handle each client independently. The server responds to different
 * queries, including returning the index page, querying an API based on a movie name, and handling errors.
 * @author Nicolas Ariza Barbosa
 */
public class HTTPServer {

    private static HTTPResponse serverResponse = new HTTPResponse("html", null);
    private static HTTPServer _instance = new HTTPServer();

    private HTTPServer(){}

    public static HTTPServer getInstance(){
        return _instance;
    }

    /**
     * The runServer method initializes the server socket and continuously accepts client connections.
     * For each client, a new thread is created to handle the connection independently.
     * @throws IOException If an I/O error occurs.
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ReflexiveManager.defineAllComponents();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;

        while(running){
            try {
                Socket clientSocket = serverSocket.accept();
                handleClientConnection(clientSocket);
            } catch (IOException | URISyntaxException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        serverSocket.close();
    }

    /**
     * Handles the client connection, including reading the query, processing it, and sending the response.
     * @param client The client socket for the connection.
     * @throws IOException If an I/O error occurs.
     * @throws URISyntaxException 
     */
    private static void handleClientConnection(Socket client) throws IOException, URISyntaxException {
        serverResponse.setClient(client);
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String inputLine;
        String query = "";

        // Read the headers
        while ((inputLine = in.readLine()) != null) {
            if(query.isEmpty()){
                query = inputLine.split(" ")[1].toLowerCase();
            }
            if (!in.ready()) {
                break;
            }
        }
        handleClientRequest(out, client, query);

        out.close();
        in.close();
        client.close();
    }

    public static void handleClientRequest(PrintWriter outPut, Socket client, String query){
        try {
            // Prepare to read the URI
            URI request = new URI(query);

            if(query.contains(".")){
                HTTPFileProcessor(outPut, client, request);
            }else{
                String action = query.split("/")[1];
                String arg = query.split("/")[2];
                String ans = ReflexiveManager.invokeMethod(action, arg);
                serverResponse.setContentType("plain");
                serverResponse.setBody(ans);
                serverResponse.setHeader(serverResponse.OKResponse());
                outPut.println(serverResponse.createAndGetResponse());
            }

        } catch (NoSuchMethodException | IllegalAccessException | 
                    InvocationTargetException | URISyntaxException | IOException e) {
            System.err.println(e);
            HTTPErrorConstructor(outPut, client);
        }
    }

    /**
     * Sends the HTTP files for a resource to the client.
     * @param outPut The PrintWriter for sending the file.
     * @param client The socket used to communicate with the client
     * @param request The request from the client
     * @throws FileNotFoundException If the index page file is not found.
     * @throws IOException If an I/O error occurs.
     * @throws URISyntaxException 
     */
    public static void HTTPFileProcessor (PrintWriter outPut, Socket client, URI request) throws FileNotFoundException, IOException, URISyntaxException {
        // Validates if the file exists
        if(!Files.exists(Paths.get("web-files", request.getPath()))){
            throw new NoSuchFileException("File: " + request.getPath() + " does not exists!");
        }
        // Send headers to the client
        String extension = request.toString();
        extension = extension.substring(extension.lastIndexOf(".") + 1);
        // Set content type
        serverResponse.setContentType(extension);
        outPut.println(serverResponse.OKResponse());
        // Prevents the connection to be closed by exchanging output stream
        outPut.flush();
        // Send file to the client
        serverResponse.sendFile(request, client);
    }

    /**
     * Sends the HTTP error response to the client.
     * @param outPut The PrintWriter for sending the response.
     * @throws URISyntaxException
     * @throws IOException
     */
    private static void HTTPErrorConstructor (PrintWriter outPut, Socket client) {
        // Send headers to the client
        serverResponse.setContentType("html");
        outPut.println(serverResponse.NotFoundResponse());
        // Prevents the connection to be closed by exchanging output stream
        outPut.flush();
        // Send HTML structure to the client
        try {
            serverResponse.sendNotFoundPage();
        } catch (IOException | URISyntaxException e) {
            System.err.println(e);
        }
    }

    // public static void get(String path, Function svc) throws IOException{
    //     usersRequest.put(path,svc);
    // }

    // public static void post(String path, Function svc) throws IOException{
    //     usersRequest.put(path, svc);
    // }
}



