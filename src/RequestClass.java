import java.util.ArrayList;
import java.util.HashMap;

public class RequestClass {

    private String fileName;
    private ArrayList<String> clientsNames;

    public RequestClass(String fileName){
        this.fileName = fileName;
        clientsNames = new ArrayList<>();
    }

    public void addNewClient(String clientName){
        clientsNames.add(clientName);
    }

    public ArrayList<String> getReqClients(){
        return clientsNames;
    }

    public String getFileName(){
        return fileName;
    }

    public void printIt(){
        System.out.println("ReqFile " + fileName + ", Clients " + clientsNames);
    }

}
