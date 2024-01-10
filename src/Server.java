import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Server {

    private HashMap<String, ClientInfo> clients;
    private ServerSocket serverSocket;
    private File mainDirectory;
    private int clientCount = 0;

    private int maxBuffer = 4000;
    private static int minChunkSize = 5;//1byte
    private static int maxChunkSize = 256;//100 bytes
    private static int remBuffer = 4000;//remaining buffer

    private static ArrayList<MyFile> files;
    static ArrayList<RequestClass> requests;

    public Server(){
        clients = new HashMap<>();
        files = new ArrayList<>();
        requests = new ArrayList<>();
        try{
            serverSocket = new ServerSocket(33333);
            System.out.println("Server started");
            mainDirectory = new File("Clients");
            mainDirectory.mkdir();
            while(true){
                Socket clientSocket = serverSocket.accept();
                serve(clientSocket);
            }
        }catch (Exception e){
            System.out.println("Server starts: " + e);
        }
    }

    private void serve(Socket clientSocket) throws IOException, ClassNotFoundException {
        NetworkUtil networkUtil = new NetworkUtil(clientSocket);
        String clientName = (String) networkUtil.read();
        if(clients.containsKey(clientName) && clients.get(clientName).getNetworkUtil().isOnline()){
            networkUtil.write("Name not allowed");
            System.out.println("Connection Declined");
        }
        else{
            if(!clients.containsKey(clientName)){
                clientCount++;
                clients.put(clientName, new ClientInfo(clientName, networkUtil, clientCount));
                File clientFolder = new File(mainDirectory.getAbsolutePath() + "/" + clientName);
                clientFolder.mkdir();
            }
            else{
                clients.get(clientName).setNetworkUtil(networkUtil);
            }
            networkUtil.write("Connection Successful");
            System.out.println(clientName + " connected");
            new ReadThreadServer(networkUtil, clients, clientName, (mainDirectory.getAbsolutePath() + "/" + clientName));
            new WriteThreadServer(networkUtil, clients);
        }
    }

    public int getMaxBuffer() {
        return maxBuffer;
    }

    public void setMaxBuffer(int maxBuffer) {
        this.maxBuffer = maxBuffer;
    }

    public static int getMinChunkSize() {
        return minChunkSize;
    }

    public void setMinChunkSize(int minChunkSize) {
        this.minChunkSize = minChunkSize;
    }

    public static int getMaxChunkSize() {
        return maxChunkSize;
    }

    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }

    public static int getRemBuffer() {
        return remBuffer;
    }

    public static void decreaseRemBuffer(int a) {
        remBuffer = remBuffer - a;
    }
    public static void increaseRemBuffer(int a){
        remBuffer = remBuffer + a;
    }

    public static void addFile(String name, int id, String access, String owner, long size, String path){
        files.add(new MyFile(name, id, access, owner, size, path));
    }

    public static ArrayList<MyFile> getFiles(){
        return files;
    }

    public static boolean hasFile(String fileName){
        for (MyFile mf : files){
            if(mf.getName().equalsIgnoreCase(fileName) || mf.getName().contains(fileName)){
                if(mf.getAccess().equals("public")){
                    return true;
                }
            }
        }
        return false;
    }
    public static MyFile getFileById(int id){
        for(MyFile f : files){
            if(f.getId() == id){
                return f;
            }
        }
        return null;
    }

    public static boolean hasReq(String fileName){
        for (RequestClass requestClass : requests){
            if(requestClass.getFileName().equals(fileName)){
                return true;
            }
        }
        return false;
    }

    public static int getIdxByFileName(String fileName){
        int idx = 0;
        for (RequestClass requestClass : requests){
            if(requestClass.getFileName().equals(fileName)){
                return idx;
            }
            idx++;
        }
        return idx;
    }

    public static void printReq(){
        System.out.println("Call to printReq");
        for (RequestClass rc : requests){
            rc.printIt();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
    }

}
