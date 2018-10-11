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
            AppendLogRequest request = new AppendLogRequest(1, 1, 1, 1, null, 1);
            RaftService service = RaftService.instance();
            for (int i = 0; i < 20000; i++) {
                service.remoteAppendLog(members.get(0), request, (result) -> {
                    System.out.println(JSON.toJSONString(result));
                });
            }
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
