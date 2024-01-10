import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ReadThreadServer implements Runnable{

    private Thread thread;
    private NetworkUtil networkUtil;
    private String clientName;
    private HashMap<String, ClientInfo> clients;

    private String directoryPath;


    public ReadThreadServer(NetworkUtil networkUtil, HashMap<String, ClientInfo> clients, String clientName, String directoryPath){
        this.networkUtil = networkUtil;
        this.clients = clients;
        this.clientName = clientName;
        this.directoryPath = directoryPath;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try{
            while(true){
                Message msgFromClient = (Message) networkUtil.read();
                System.out.println(msgFromClient.getMsg());
                if(msgFromClient.getMsg().contains("clientsList")){
                    networkUtil.write(getClientList());
                }
                else if(msgFromClient.getMsg().contains("reqFile123")){
                    String[] req = msgFromClient.getMsg().split(",");
                    String reqId = req[2];
                    String reqDes = req[1];
                    String reqFileName = req[3];
                    if(Server.hasFile(reqFileName)){
                        networkUtil.write(new Message("Server", "The file you are looking for may be there"));
                    }

                    if(Server.hasReq(reqFileName)){
                        //System.out.println("eikhane");
                        Server.requests.get(Server.getIdxByFileName(reqFileName)).addNewClient(clientName);
                    }
                    else{
                        RequestClass requestClass = new RequestClass(reqFileName);
                        requestClass.addNewClient(clientName);
                        Server.requests.add(requestClass);
                        //System.out.println("Else e");
                        //System.out.println("First e size " + Server.requests.size());
                        Server.printReq();
                    }
                    Message msgToSend = new Message("Server", "Request id: " + reqId + '\n' + "File name - " + reqFileName + "\n" + reqDes + '\n');
                    for(String name : clients.keySet()){
                        if(!name.equals(clientName)){
                            clients.get(name).addUnreadMsg(msgToSend.getMsg());
                        }
                    }
                    networkUtil.write(new Message("Server", "Request granted"));
                    //System.out.println("Req blk theke ber howar time e size " + Server.requests.size());
                }
                else if(msgFromClient.getMsg().contains("unreadMsg123")){
                    networkUtil.write(getUnreadMsg(clients.get(msgFromClient.getFrom()).getUnreadMsg()));
                    clients.get(msgFromClient.getFrom()).deleteUnreadMsg();
                }
                else if(msgFromClient.getMsg().contains("upload123")){

                    //System.out.println("Upload e dhuke size " + Server.requests.size());

                    String msgFromClientNow = msgFromClient.getMainMsg();
                    System.out.println(msgFromClientNow);
                    String[] words = msgFromClientNow.split(",");
                    String msgToSend1 = "Continue123" + "," + words[0] + "," + words[1];
                    networkUtil.write(new Message("Server", msgToSend1));


                    long size = (long) networkUtil.read();
                    //System.out.println(size);
                    if(Server.getRemBuffer() < size){
                        networkUtil.write(new Message("Server", "Disallowed file uploading"));
                    }
                    else{
                        //System.out.println("Upload er ELSE e dhuke size " + Server.requests.size());
                        int chunkSize = Server.getMinChunkSize() +  (int) (Math.random() * ((Server.getMaxChunkSize() - Server.getMinChunkSize()) + 1));
                        Server.decreaseRemBuffer(chunkSize);
                        String fileName = words[0];
                        //System.out.println("Filename: " + fileName);
                        String fileAccess = words[1];
                        String msgToSend = "Upload allowed123," + chunkSize;
                        networkUtil.write(new Message("Server", msgToSend));

                        int bytes = 0;
                        int fileId = ((clients.get(clientName).getClientId())*100) + clients.get(clientName).getFileCount();
                        //System.out.println(fileId);
                        //allowing 100 files
                        String[] fileNameFull = fileName.split("\\.");
                        fileName = fileNameFull[0];
                        String fileExtension = fileNameFull[1];
                        String fullFileName = fileNameFull[0] + "." + fileNameFull[1];
                        //System.out.println(fileName);
                        String path = directoryPath + "/" + fileName + "_" + fileId + "_" + fileAccess + "." + fileExtension;
                        clients.get(clientName).fileCountIncrease();
                        //System.out.println("File outSteream create er aage size " + Server.requests.size());
                        FileOutputStream fileOutputStream = new FileOutputStream(path);
                        byte[] buffer = new byte[4 * 1024];
                        //System.out.println("Ei while er aage");
                        long fileSize = size;
                        while (size > 0 && (bytes = networkUtil.readForFile(buffer, 0, (int)Math.min(chunkSize, size))) != -1) {
                            fileOutputStream.write(buffer, 0, bytes);
                            //System.out.println("Eije ami");
                            networkUtil.write("Server: Got a chunk");
                            String midMsg = (String) networkUtil.read();
                            if(midMsg.contains("TimeOut123")){
                                File fileDel = new File(path);
                                fileDel.delete();
                                break;
                            }
                            //System.out.println("Got a chunk from " + clientName);
                            size -= bytes;
                        }
                        // Here we received file
                        //System.out.println("File is Received from " + clientName);
                        networkUtil.write(new Message("Server", "Upload successful"));
                        Server.addFile(fileName, fileId, fileAccess, clientName, fileSize, path);
                        Server.increaseRemBuffer(chunkSize);
                        //System.out.println("File outSteream close er aage size " + Server.requests.size());
                        fileOutputStream.close();
                        //System.out.println("Filename " + fileName + ", FullName " + fullFileName);
//                        System.out.println();
//                        System.out.println("Size of req " + Server.requests.size());
//                        System.out.println();
//                        Server.printReq();
//                        System.out.println("Full file name " + fullFileName);
                        if(Server.hasReq(fullFileName)){
                            //System.out.println("If e dhuksi");
                            ArrayList<String> reqClients = Server.requests.get(Server.getIdxByFileName(fullFileName)).getReqClients();
                            for(String name : reqClients){
                                clients.get(name).addUnreadMsg("Your requested file - " + fullFileName + " has been uploaded");
                            }
                            //Server.requests.remove(Server.getIdxByFileName(fullFileName));
                        }
                    }
                } else if (msgFromClient.getMsg().contains("myFilesList123")) {
                    Message msgNow = getOwnFilesList();
                    networkUtil.write(msgNow);
                    if (msgNow.getMsg().contains("Enter file id to download")){
                        //System.out.println("read korar aage");
                        String msgFromClientNow = (String) networkUtil.read();
                        System.out.println(msgFromClientNow);
                        if(msgFromClientNow.equalsIgnoreCase("dontWannaDownload123")){
                            continue;
                        }
                        String[] msges = msgFromClientNow.split(" ");
                        int fileId = Integer.parseInt(msges[2]);
                        MyFile downFile = Server.getFileById(fileId);
                        String filenamee = downFile.getName();
                        String fileDetails = filenamee + ",," + downFile.getSize() + ",," + downFile.getExtension();
                        networkUtil.write(fileDetails);
                        //Sending file
                        String path =  directoryPath + "/" + filenamee + "_" + downFile.getId() + "_" + downFile.getAccess() + "." + downFile.getExtension();
                        File file = new File(path);
                        FileInputStream fileInputStream = new FileInputStream(file);

                        int chunk = 10;
                        byte[] buffer = new byte[chunk];
                        int bytes = 0;
                        while ((bytes = fileInputStream.read(buffer)) != -1) {
                            //System.out.println("While e dhuklam");
                            networkUtil.writeForFile(buffer, 0, bytes);
                            networkUtil.flush();
                            //System.out.println("Write er pore asi");
                        }
                        fileInputStream.close();
                    }
                }
                else if (msgFromClient.getMsg().contains("othersFilesList123")) {
                    //System.out.println("Dhukse");
                    Message msgNow = getOthersFilesList();
                    networkUtil.write(msgNow);
                    if (msgNow.getMsg().contains("Enter file id to download")){
                        //System.out.println("read korar aage");
                        String msgFromClientNow = (String) networkUtil.read();
                        System.out.println(msgFromClientNow);
                        if(msgFromClientNow.equalsIgnoreCase("dontWannaDownload123")){
                            continue;
                        }
                        String[] msges = msgFromClientNow.split(" ");
                        int fileId = Integer.parseInt(msges[2]);
                        MyFile downFile = Server.getFileById(fileId);
                        String filenamee = downFile.getName();
                        String fileDetails = filenamee + ",," + downFile.getSize() + ",," + downFile.getExtension();
                        networkUtil.write(fileDetails);

                        //Sending file
                        String path =  "D:/Study/Books/L-3,T-2/321 - Networks/Lab/Offline1/SocketProg/Clients/" + downFile.getOwner() + "/" + filenamee + "_" + downFile.getId() + "_" + downFile.getAccess() + "." + downFile.getExtension();
                        File file = new File(path);
                        FileInputStream fileInputStream = new FileInputStream(file);

                        int chunk = 10;
                        byte[] buffer = new byte[chunk];
                        int bytes = 0;
                        while ((bytes = fileInputStream.read(buffer)) != -1) {
                            //System.out.println("While e dhuklam");
                            networkUtil.writeForFile(buffer, 0, bytes);
                            networkUtil.flush();
                            //System.out.println("Write er pore asi");
                        }
                        fileInputStream.close();
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }finally {
            try{
                networkUtil.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Message getClientList() throws IOException {
        String online = "Online" + '\n', offline = "Offline" + '\n';
        for(String name : clients.keySet()){
            if(clients.get(name).getNetworkUtil().isOnline()){
                online = online + '\t' + name + '\n';
            }
            else{
                offline = offline + '\t' + name + '\n';
            }
        }
        return new Message("Server", online+offline);
    }

    private Message getUnreadMsg(ArrayList<String> msg){
        String msgToSend = "";
        if(msg.size() == 0){
            msgToSend = "No message";
        }
        else{
            for(int i = 0; i < msg.size(); i++){
                msgToSend = msgToSend + (i+1) + ") " + msg.get(i) + "\n";
            }
        }
        return new Message("Server", msgToSend);
    }

    private Message getOwnFilesList() throws IOException {
        String publicFile = "public" + '\n', privateFile = "private" + '\n';
        ArrayList<MyFile> allFiles = Server.getFiles();
        boolean hasFile = false;
        for(MyFile f : allFiles){
            if(f.getOwner().equalsIgnoreCase(clientName)){
                hasFile = true;
                if(f.getAccess().equalsIgnoreCase("public")){
                    publicFile = publicFile + '\t' + f.getName() + " --- " + f.getId() + '\n';
                }
                else{
                    privateFile = privateFile + '\t' + f.getName() + " --- " + f.getId() + '\n';
                }
            }
        }
        if(!hasFile){
            return new Message("Server", "No files uploaded123");
        }
        return new Message("Server", publicFile+privateFile+"\nEnter file id to download");
    }

    private Message getOthersFilesList() throws IOException {
        String fileStr = "\nFileName --- Owner --- FileID\n";
        ArrayList<MyFile> allFiles = Server.getFiles();
        boolean hasFile = false;
        for(MyFile f : allFiles){
            if(f.getAccess().equalsIgnoreCase("public")){
                if(!f.getOwner().equalsIgnoreCase(clientName)){
                    fileStr = fileStr + '\t' + f.getName() + " --- " + f.getOwner() + " --- " + f.getId() + '\n';
                    hasFile = true;
                }
            }
        }
        if(!hasFile){
            return new Message("Server", "No files uploaded123");
        }
        return new Message("Server", fileStr+"\nEnter file id to download");
    }

}
