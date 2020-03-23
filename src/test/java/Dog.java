import lombok.Data;

/**
 * @author ldb
 * @date 2020-01-30 16:37
 * @dsscription
 */
@Data
public class Dog {
    private final    String name;

    public static void main(String[] args) {
        Dog d = new Dog("abc");
        System.out.println(d.name);

    }
}
