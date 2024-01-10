import java.io.Serializable;

public class Message implements Serializable {

    private String from;
    private String msg;
    private boolean hasRead;

    public Message(String from, String msg){
        this.from = from;
        this.msg = msg;
        this.hasRead = false;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return from + ": " + msg;
    }
    public String getMainMsg(){
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean hasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }
}
