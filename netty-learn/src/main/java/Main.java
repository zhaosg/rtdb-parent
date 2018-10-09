import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import demo.DriftLogEntry;
import demo.ScribeService;
import demo.ScribeServiceImpl;
import io.airlift.drift.client.DriftClientFactory;
import io.airlift.drift.client.address.AddressSelector;
import io.airlift.drift.client.address.SimpleAddressSelector;
import io.airlift.drift.codec.ThriftCodecManager;
import io.airlift.drift.server.DriftServer;
import io.airlift.drift.server.DriftService;
import io.airlift.drift.server.stats.NullMethodInvocationStatsFactory;
import io.airlift.drift.transport.netty.client.DriftNettyClientConfig;
import io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory;
import io.airlift.drift.transport.netty.server.DriftNettyServerConfig;
import io.airlift.drift.transport.netty.server.DriftNettyServerTransportFactory;
import io.airlift.units.DataSize;
import io.airlift.units.Duration;
import java.util.List;
import java.util.Optional;
import static io.airlift.units.DataSize.Unit.MEGABYTE;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Main {
    private static StatInfo counter = new StatInfo();

    public static void test() {
        counter.init();
        for (int i = 0; i < 100; i++) {
            new Thread(new ClientTask()).start();
        }
    }

    static class ClientTask implements Runnable {
        @Override
        public void run() {
            List<HostAndPort> addresses = ImmutableList.of(HostAndPort.fromParts("localhost", 99));
            ThriftCodecManager codecManager = new ThriftCodecManager();
            AddressSelector addressSelector = new SimpleAddressSelector(addresses, true);
            DriftNettyClientConfig config = new DriftNettyClientConfig();
            DriftNettyMethodInvokerFactory<?> methodInvokerFactory = DriftNettyMethodInvokerFactory.createStaticDriftNettyMethodInvokerFactory(config);
            DriftClientFactory clientFactory = new DriftClientFactory(codecManager, methodInvokerFactory, addressSelector);
            ScribeService scribeService = clientFactory.createDriftClient(ScribeService.class).get();
            while (true) {
                scribeService.log(ImmutableList.of(new DriftLogEntry("category", "message")));
                counter.increment();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ScribeService scribeService = new ScribeServiceImpl();
        DriftService service = new DriftService(scribeService, Optional.empty(), true);
        DriftNettyServerConfig config = new DriftNettyServerConfig()
                .setPort(99)
                .setAcceptBacklog(101)
                .setIoThreadCount(100)
                .setWorkerThreadCount(100)
                .setRequestTimeout(new Duration(33, MINUTES))
                .setMaxFrameSize(new DataSize(100, MEGABYTE))
                .setSslContextRefreshTime(new Duration(33, MINUTES))
                .setAllowPlaintext(true)
                .setSslEnabled(false)
                .setSessionCacheSize(678)
                .setSessionTimeout(new Duration(78, HOURS))
                .setAssumeClientsSupportOutOfOrderResponses(true);
        DriftServer driftServer = new DriftServer(
                new DriftNettyServerTransportFactory(config),
                new ThriftCodecManager(),
                new NullMethodInvocationStatsFactory(),
                ImmutableSet.of(service),
                ImmutableSet.of());

        driftServer.start();

        test();
    }


}
