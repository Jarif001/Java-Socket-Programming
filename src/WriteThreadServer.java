import java.util.HashMap;

public class WriteThreadServer implements Runnable{

    private Thread thread;
    private NetworkUtil networkUtil;
    private HashMap<String, ClientInfo> clients;

    public WriteThreadServer(NetworkUtil networkUtil, HashMap<String, ClientInfo> map){
        this.networkUtil = networkUtil;
        this.clients = map;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try{
            while(true){

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
}
