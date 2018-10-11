import cn.zhaosg.rtdb.netty.NettyClient;
import cn.zhaosg.rtdb.raft.AppendLogRequest;
import cn.zhaosg.rtdb.raft.Member;

import java.util.ArrayList;
import java.util.List;

public class RaftBootstrap {
    public static List<Member> nodes = new ArrayList<>();

    public static void main(String[] args) {
        try {
            NettyClient client = new NettyClient();
            client.init("127.0.0.1", 8007);
            client.send(new AppendLogRequest(1, 1, 1, 1, null, 1));
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
