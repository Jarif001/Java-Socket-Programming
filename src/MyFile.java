import java.io.Serializable;
public class MyFile implements Serializable {

    private String path;
    private String name;//provided by client
    private String access;//provided by client
    private String owner;
    private long size;//provided by client
    private int id;////provided by server
    private String extension;

    public MyFile(String name, int id, String access, String owner, long size, String path){
        this.access = access;
        this.size = size;
        this.name = name;
        this.owner = owner;
        this.path = path;
        this.id = id;
        extension = path.split("\\.")[1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension(){
        return extension;
    }
}
