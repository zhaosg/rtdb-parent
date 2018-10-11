package raft;

import netty.NettyClient;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Member> nodes = new ArrayList<>();

    public static void main(String[] args) {
        try {
            NettyClient client = new NettyClient();
            client.init("127.0.0.1", 8007);
            client.send(new AppendLogRequest(1, 1, 1, 1, null, 1));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
