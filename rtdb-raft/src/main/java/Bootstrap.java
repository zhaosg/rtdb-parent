import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zhaosg.rtdb.Cfg;
import cn.zhaosg.rtdb.base.Server;
import cn.zhaosg.rtdb.raft.ConsenusService;
import cn.zhaosg.rtdb.raft.Member;

public class Bootstrap {
    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static List<Member> members = null;
    private Server server;

    public void start() {
        try {
            loadConfig();
            server = new Server();
            logger.info(String.format("the server start at port %d", Cfg.port()));
            members = Cfg.members();
            server.start();
            Thread.sleep(2000);
//            ConsenusService service = ConsenusService.instance();
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
