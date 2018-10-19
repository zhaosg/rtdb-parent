import com.infosys.rpc.Application;
import com.infosys.rpc.api.RtdbService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureWebTestClient
public class ClientTests {

    @Resource
    private RtdbService rtdbService;

    @Test
    public void exampleTest() {
        rtdbService.value(new String[]{});
    }

}