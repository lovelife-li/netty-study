import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdaterAnalyzeTest {

    private volatile int total = 1;
    private static final AtomicIntegerFieldUpdater<AtomicIntegerFieldUpdaterAnalyzeTest>
            total_updater = AtomicIntegerFieldUpdater.newUpdater(AtomicIntegerFieldUpdaterAnalyzeTest.class, "total");


    public static void main(String[] args) {
        AtomicIntegerFieldUpdaterAnalyzeTest test = new AtomicIntegerFieldUpdaterAnalyzeTest();
//        test.testValue();
        System.out.println(total_updater.getAndIncrement(test));
        System.out.println(total_updater.getAndIncrement(test));
        System.out.println(test.total);
        System.out.println(test.total);

    }

    public AtomicIntegerFieldUpdater<DataDemo> updater(String name) {
        return AtomicIntegerFieldUpdater.newUpdater(DataDemo.class, name);
    }

    public void testValue() {
        DataDemo data = new DataDemo();
//      //访问父类的public 变量，报错：java.lang.NoSuchFieldException
//      System.out.println("fatherVar = "+updater("fatherVar").getAndIncrement(data));
//      
//      //访问普通 变量，报错：java.lang.IllegalArgumentException: Must be volatile type
//      System.out.println("intVar = "+updater("intVar").getAndIncrement(data));

//      //访问public volatile int 变量，成功
        System.out.println("publicVar = " + updater("publicVar").getAndIncrement(data));
        System.out.println(data.publicVar);
//      
//      //访问protected volatile int 变量，成功
//      System.out.println("protectedVar = "+updater("protectedVar").getAndIncrement(data));
//      
//      //访问其他类private volatile int变量，失败：java.lang.IllegalAccessException
//      System.out.println("privateVar = "+updater("privateVar").getAndIncrement(data));
//      
//      //访问，static volatile int，失败，只能访问实例对象：java.lang.IllegalArgumentException
//      System.out.println("staticVar = "+updater("staticVar").getAndIncrement(data));
//      
//      //访问integer变量，失败， Must be integer type
//      System.out.println("integerVar = "+updater("integerVar").getAndIncrement(data));
//      
//      //访问long 变量，失败， Must be integer type
//      System.out.println("longVar = "+updater("longVar").getAndIncrement(data));

        //自己在自己函数里面可以访问自己的private变量，所以如果可见，那么可以进行原子性字段更新
        data.testPrivate();
    }
}

class Father {

    public volatile int fatherVar = 4;
}

class DataDemo extends Father {

    public int intVar = 4;

    public volatile int publicVar = 3;
    protected volatile int protectedVar = 4;
    private volatile int privateVar = 5;

    public volatile static int staticVar = 10;
    //The field finalVar can be either final or volatile, not both
    //public final volatile int finalVar = 11;

    public volatile Integer integerVar = 19;
    public volatile Long longVar = 18L;

    public void testPrivate() {
        DataDemo data = new DataDemo();
        System.out.println(AtomicIntegerFieldUpdater.newUpdater(DataDemo.class, "staticVar").getAndIncrement(data));
    }
}