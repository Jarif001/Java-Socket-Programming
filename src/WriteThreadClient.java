import java.io.IOException;
import java.util.Scanner;

public class WriteThreadClient implements Runnable{

    private String name;
    private Thread thread;
    private NetworkUtil networkUtil;

    public WriteThreadClient(String name, NetworkUtil networkUtil){
        this.name = name;
        this.networkUtil = networkUtil;
        this.thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("1) Other Clients");
            System.out.println("2) Uploaded Files");
            System.out.println("3) Other Files");
            System.out.println("4) Request a file");
            System.out.println("5) Unread Messages");
            System.out.println("6) Upload a file");
            while(true){
                int choice = scanner.nextInt();
                scanner.nextLine();
                if(choice == 1){
                    Message msg = new Message(this.name, "clientsList");
                    networkUtil.write(msg);
                    System.out.println("Client list requested");
                }
                else if(choice == 2){
                    callMyFiles();
                    thread.suspend();
                }
                else if(choice == 3){
                    callOtherFiles();
                    thread.suspend();
                }
                else if(choice == 4){
                    System.out.println("Enter file name");
                    String fileName = scanner.nextLine();
                    System.out.println("Write file description");
                    String reqDes = scanner.nextLine();
                    System.out.println("Enter request id");
                    String reqId = scanner.nextLine();
                    String fullMsg = "reqFile123," + reqDes + "," + reqId + "," + fileName;
                    Message msg = new Message(this.name, fullMsg);
                    networkUtil.write(msg);
                }
                else if(choice == 5){
                    Message msg = new Message(this.name, "unreadMsg123");
                    networkUtil.write(msg);
                }
                else if(choice == 6){
                    System.out.println("Enter the name of the file");
                    String fileName = scanner.nextLine();
                    System.out.println(fileName);
                    System.out.println("Enter access specifier (public or private)");
                    String access = scanner.nextLine();
                    String fullMsg = fileName + "," + access + "," + "upload123";
                    networkUtil.write(new Message(this.name, fullMsg));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }finally {
            try{
                networkUtil.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callMyFiles() throws IOException {
        Message msg = new Message(this.name, "myFilesList123");
        networkUtil.write(msg);
    }

    private void callOtherFiles() throws IOException {
        Message msg = new Message(this.name, "othersFilesList123");
        networkUtil.write(msg);
    }

    public Thread getThread(){
        return thread;
    }

}
