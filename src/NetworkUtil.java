import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class NetworkUtil {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean online = true;

    public NetworkUtil(String serverName, int port) throws IOException {
        socket = new Socket(serverName, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public NetworkUtil(Socket clientSocket) throws IOException {
        socket = clientSocket;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void write(Object obj) throws IOException {
        oos.writeUnshared(obj);
    }

    public void writeLong(long v) throws IOException {
        oos.writeLong(v);
    }

    public void writeForFile(byte[] buffer, int a, int bytes) throws IOException {
        oos.write(buffer, a, bytes);
    }

    public Object read() throws IOException, ClassNotFoundException {
        return ois.readUnshared();
    }

    public long readLong() throws IOException {
        return ois.readLong();
    }

    public int readForFile(byte[] buffer, int a, int bytes) throws IOException {
        return ois.read(buffer, a, bytes);
    }

    public void flush() throws IOException {
        oos.flush();
    }
    public void close() throws IOException {
        ois.close();
        oos.close();
        online = false;
    }

    public void disconnect() throws IOException {
        online = false;
        socket.close();
    }

    public boolean isOnline(){
        return online;
    }

    public void setMyTimeOut(int timeOut) throws SocketException {
        socket.setSoTimeout(timeOut);
    }

}
