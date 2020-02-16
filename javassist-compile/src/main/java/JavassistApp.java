import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class JavassistApp {

    public static void main(String[] args) throws Exception {
        ClassPool cp = ClassPool.getDefault();
        CtClass clazz = cp.makeClass("MyTest");
        CtField field = new CtField(cp.get("java.lang.String"), "prop", clazz);
        field.setModifiers(Modifier.PRIVATE);
        clazz.addMethod(CtNewMethod.getter("getProp", field));
        clazz.addMethod(CtNewMethod.setter("setProp", field));
        clazz.addField(field, CtField.Initializer.constant("manxi hello"));

        CtField intF = new CtField(CtClass.intType, "a", clazz);
        intF.setModifiers(Modifier.PRIVATE);
        clazz.addField(intF);
        CtField intF2 = new CtField(CtClass.intType, "b", clazz);
        intF2.setModifiers(Modifier.PRIVATE);
        clazz.addField(intF2);

        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, clazz);
        StringBuffer body = new StringBuffer("{}");
        ctConstructor.setBody(body.toString());
        clazz.addConstructor(ctConstructor);

        CtConstructor ctC2 = new CtConstructor(new CtClass[]{CtClass.intType, CtClass.intType}, clazz);
        ctC2.setBody("{this.a = $1; this.b=$2;}");
        clazz.addConstructor(ctC2);

        CtMethod execute = new CtMethod(CtClass.voidType, "execute", new CtClass[]{}, clazz);
        execute.setModifiers(Modifier.PUBLIC);
        body = new StringBuffer();
        body.append("{\n System.out.println(\"execute():\" + this.prop);");
        body.append("\n}");
        execute.setBody(body.toString());
        clazz.addMethod(execute);

        CtMethod sum = new CtMethod(CtClass.intType, "sum", new CtClass[]{}, clazz);
        sum.setModifiers(Modifier.PUBLIC);
        sum.setBody("{return a + b;}");
        clazz.addMethod(sum);

        clazz.writeFile("./");
        Class<?> c = clazz.toClass();

        Object o = c.newInstance();
        Method execute1 = o.getClass().getMethod("execute");
        execute1.invoke(o);

        Constructor<?> constructor = o.getClass().getConstructor(int.class, int.class);
        Object o1 = constructor.newInstance(12, 5);
        Method sum1 = o.getClass().getMethod("sum");
        Object invoke = sum1.invoke(o1);
        System.out.println(invoke);
    }
}
