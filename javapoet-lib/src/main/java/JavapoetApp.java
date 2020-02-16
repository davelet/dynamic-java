import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.joor.Reflect;

import javax.lang.model.element.Modifier;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collections;

public class JavapoetApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        MethodSpec sum = MethodSpec.methodBuilder("sum")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addStatement("return a+b")
                .build();

        MethodSpec con = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.INT, "a")
                .addParameter(TypeName.INT, "b")
                .addStatement("this.a=a;this.b=b;")
                .build();
        String name = "Test";
        TypeSpec helloWorld = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addField(TypeName.INT, "a", Modifier.PRIVATE)
                .addField(TypeName.INT, "b", Modifier.PRIVATE)
                .addMethod(con)
                .addMethod(sum)
                .build();

        String packageName = "info.manxi";
        JavaFile javaFile = JavaFile.builder(packageName, helloWorld).build();
        javaFile.writeTo(System.out);
        String content = javaFile.toString();

        System.out.println(Reflect.compile(packageName + "." + name, content).create(12, 45).call("sum"));

//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null);
//        JavaFileObject jfo = new StringJavaObject(name, content);
//        JavaCompiler.CompilationTask task = compiler.getTask(null, fm, null, null, null, Collections.singletonList(jfo));
//        Boolean call = task.call();
//        if (call) {
//            Class<? extends Object> cls = Class.forName(packageName + "." + name);
//            Constructor<?> iic = cls.getConstructor(int.class, int.class);
//            Object o1 = iic.newInstance(1, 456);
//            System.out.println(o1.getClass().getMethod("sum").invoke(o1));
//        }
    }

    static class StringJavaObject extends SimpleJavaFileObject {
        //源代码
        private String content;

        //遵循Java规范的类名及文件
        public StringJavaObject(String _javaFileName, String _content) {
            super(_createStringJavaObjectUri(_javaFileName), Kind.SOURCE);
            content = _content;
        }

        //产生一个URL资源路径
        private static URI _createStringJavaObjectUri(String name) {
            //注意此处没有设置包名
            return URI.create("String:///" + name + Kind.SOURCE.extension);
        }

        //文本文件代码
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }
}
