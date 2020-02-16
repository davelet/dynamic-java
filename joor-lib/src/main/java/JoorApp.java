import org.joor.Reflect;

public class JoorApp {
    public static void main(String[] args) {
        System.out.println(Reflect.compile("T", "public class T {\n" +
                "    private int a, b;\n" +
                "\n" +
                "    public T(int a, int b) {\n" +
                "        this.a = a;\n" +
                "        this.b = b;\n" +
                "    }\n" +
                "\n" +
                "    public int sum() {\n" +
                "        return a + b;\n" +
                "    }\n" +
                "}").create(12, 45).call("sum"));

    }
}
