import java.util.ArrayList;

public class ClientInfo {

    private String name;
    private NetworkUtil networkUtil;
    private ArrayList<String> unreadMsg;
    private boolean isOnline;
    private int clientId;
    private int fileCount = 0;

    public ClientInfo(String name, NetworkUtil networkUtil, int clientId){
        this.name = name;
        this.networkUtil = networkUtil;
        this.isOnline = true;
        this.unreadMsg = new ArrayList<>();
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NetworkUtil getNetworkUtil() {
        return networkUtil;
    }

    public void setNetworkUtil(NetworkUtil networkUtil) {
        this.networkUtil = networkUtil;
    }

    public ArrayList<String> getUnreadMsg() {
        return unreadMsg;
    }

    public void setUnreadMsg(ArrayList<String> unreadMsg) {
        this.unreadMsg = unreadMsg;
    }
    public void addUnreadMsg(String msg){
        this.unreadMsg.add(msg);
    }
    public void deleteUnreadMsg(){
        this.unreadMsg.clear();
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getFileCount() {
        return fileCount;
    }
    public void fileCountIncrease(){
        fileCount++;
    }
}
