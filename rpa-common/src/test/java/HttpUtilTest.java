import com.seebon.rpa.utils.HttpUtils;

public class HttpUtilTest {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 4000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("新线程中执行的代码 : " + Thread.currentThread().getName());
                    //String resp = HttpUtils.post("http://localhost:9090/api/robot/task/testEx");
                    String resp = HttpUtils.post("http://192.168.0.99:9999/#/controlAndReport/dataControl");
                    System.out.println(Thread.currentThread().getName() + " resp=" + resp);
                }
            }).start();
        }
    }
}
