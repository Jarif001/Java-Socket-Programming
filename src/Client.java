import java.util.Scanner;

public class Client {

    private String name;
    private WriteThreadClient wtc;

    public Client(String serverAddress, int serverPort){
        Scanner scanner = new Scanner(System.in);
        try{
            System.out.println("Enter the name");
            this.name = scanner.nextLine();
            NetworkUtil networkUtil = new NetworkUtil(serverAddress, serverPort);
            networkUtil.write(name);
            String msgFromServer = (String) networkUtil.read();
            if(msgFromServer.contains("Name not allowed")){
                System.out.println("Name is not valid. Disconnecting...");
                networkUtil.close();
                networkUtil.disconnect();
            }
            else {
                System.out.println(msgFromServer);
                wtc = new WriteThreadClient(name, networkUtil);
                new ReadThreadClient(networkUtil, wtc, name);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 33333;
        Client client = new Client(serverAddress, serverPort);
    }

}
