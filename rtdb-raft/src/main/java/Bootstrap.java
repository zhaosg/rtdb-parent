import cn.zhaosg.rtdb.Cfg;
import cn.zhaosg.rtdb.base.Server;
import cn.zhaosg.rtdb.core.AppendLogRequest;
import cn.zhaosg.rtdb.core.Member;
import cn.zhaosg.rtdb.core.RaftService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Bootstrap {
    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static List<Member> members = new ArrayList<>();
    private Server server;

    public void start() {
        try {
            loadConfig();
            server = new Server();
            logger.info(String.format("the server start at port %d", Cfg.port()));
            members.add(new Member("123", "127.0.0.1", Cfg.port()));
            server.start();
            Thread.sleep(2000);
            RaftService service = RaftService.instance();
//            System.out.println("压力测试开始");
//            for (int i = 0; i <1000; i++) {
//                AppendLogRequest request = new AppendLogRequest(i, 1, 1, 1, null, 1);
//                final int ii=i;
//                service.remoteAppendLog(members.get(0), request, (result) -> {
//                    if(ii!=result.getTerm())
//                        System.out.println(ii+","+result.getTerm());
//                });
//            }
//            System.out.println("压力测试完成");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public void loadConfig() {
        Cfg.load();
    }

    public static void main(String[] args) {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.start();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
