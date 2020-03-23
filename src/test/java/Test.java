import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ldb
 * @date 2020-01-29 17:32
 * @dsscription
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {

        Executors.newScheduledThreadPool(5)
                .schedule(() -> System.out.println("hello.."), 5, TimeUnit.SECONDS);
        for (int i = 1; i <= 5; i++) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println(i+"s..");
        }
    }
}
