public class CodeBlock {
    static{
        System.out.println("静态代码块");
    }
    {
        System.out.println("构造代码块");
    }
    public CodeBlock(){
        System.out.println("无参构造函数");
    }
     
    public void sayHello(){
        System.out.println("hello");
        {
            System.out.println("普通代码块");
        }
        System.out.println("world");
    }
     
    public static void main(String[] args) {
        System.out.println("执行了main方法");
         
        new CodeBlock().sayHello();;
        System.out.println("---------------");
        new CodeBlock().sayHello();;
    }
}