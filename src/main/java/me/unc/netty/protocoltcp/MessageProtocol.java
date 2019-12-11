package me.unc.netty.protocoltcp;

/**
 * 协议包
 * tcp粘包 拆包问题
 */
public class MessageProtocol {

    private int len;          //关键，数据长度，服务器会根据这个长度来读取
    private byte[] content;   //内容

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
