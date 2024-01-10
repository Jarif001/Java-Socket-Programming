import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class ReadThreadClient implements Runnable{

    private NetworkUtil networkUtil;
    private Thread thread;
    private WriteThreadClient wtc;
    private String clientName;

    public ReadThreadClient(NetworkUtil networkUtil, WriteThreadClient wtc, String clientName){
        this.networkUtil = networkUtil;
        this.thread = new Thread(this);
        thread.start();
        this.wtc = wtc;
        this.clientName = clientName;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        try{
            while(true){
                Message msgFromServer = (Message) networkUtil.read();
                if(msgFromServer.getMsg().contains("Continue123")){
                    String serverMsg = msgFromServer.getMainMsg();
                    //System.out.println(serverMsg);

                    String[] words1 = serverMsg.split(",");
                    String fileName = words1[1];
                    String path = "D:/Study/Books/L-3,T-2/321 - Networks/Lab/Offline1/SocketProg/ClientFiles" + "/" + fileName;
                    File file = new File(path);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    networkUtil.write(file.length());
                    //System.out.println("File length er pore asi");

                    Message msgFromServerNow = (Message) networkUtil.read();
                    String msggFromServerr = msgFromServerNow.getMainMsg();
                    //System.out.println(msggFromServerr);
                    if(msggFromServerr.contains("Upload allowed123")){
                        String[] words = msggFromServerr.split(",");
                        int chunk = Integer.parseInt(words[1]);
                        byte[] buffer = new byte[chunk];
                        int bytes = 0;
                        while ((bytes = fileInputStream.read(buffer)) != -1) {
                            //System.out.println("While e dhuklam");
                            networkUtil.writeForFile(buffer, 0, bytes);
                            networkUtil.flush();
                            //System.out.println("Write er pore asi");
                            //Timer starts
                            networkUtil.setMyTimeOut(30000);
                            try{
                                String middleMsg = (String) networkUtil.read();
                                networkUtil.write("Ok transmission");
                                System.out.println(middleMsg);
                            }catch (SocketTimeoutException e){
                                System.out.println("Time out, quit uploading");
                                networkUtil.write("TimeOut123");
                                break;
                            }
                        }
                        networkUtil.setMyTimeOut(0);
                        fileInputStream.close();
                    }

                }
                else if(msgFromServer.getMsg().contains("Enter file id to download")){
                    System.out.println(msgFromServer.getMsg());
                    System.out.println("Enter 0 to quit");
                    scanner = new Scanner(System.in);//eita add korlam. dekha jak ki hoy(Shokaale xD)
                    int fileId = scanner.nextInt();

                    //eikhan thekei prb, scanner e kisu hoitese maybe

                    //System.out.println("Paisi " + fileId);
                    fileDownloadFunc(fileId);
                    wtc.getThread().resume();
                }
                else if(msgFromServer.getMainMsg().equalsIgnoreCase("No files uploaded123")){
                    System.out.println("No files uploaded");
                    wtc.getThread().resume();
                }
                else{
                    System.out.println(msgFromServer.getMsg());
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

    private void fileDownloadFunc(int fileId) throws IOException, ClassNotFoundException {
        if(fileId == 0){
            //System.out.println("Dhuksi Dont e");
            networkUtil.write("dontWannaDownload123");
        }
        else{
            //System.out.println("File id ditesi");
            networkUtil.write("Download request123 " + fileId);
            String fileDetails = (String) networkUtil.read();
            String[] words = fileDetails.split(",,");
            String fileName = words[0];
            long size = Long.parseLong(words[1]);
            File downDirectory = new File("D:/Study/Books/L-3,T-2/321 - Networks/Lab/Offline1/SocketProg/Download" + "/" + clientName);
            if (!downDirectory.exists()){
                downDirectory.mkdir();
            }
            String path = "D:/Study/Books/L-3,T-2/321 - Networks/Lab/Offline1/SocketProg/Download" + "/" + clientName + "/" + fileName + "." + words[2];
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            int bytes = 0;
            byte[] buffer = new byte[4 * 1024];
            //System.out.println("Ei while er aage");

            while (size > 0 && (bytes = networkUtil.readForFile(buffer, 0, (int)Math.min(40, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                //System.out.println("Eije ami");
                size -= bytes;
            }
            // Here we received file
            fileOutputStream.close();
            System.out.println("File downloaded");
        }
    }

}
