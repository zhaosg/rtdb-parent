import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

	public static ConfigurableApplicationContext ctx;
	
	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext("/server.xml");
		ctx.registerShutdownHook();
		ctx.start();
	}
	
}
