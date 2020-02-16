import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V11;


public class AsmApp {
    public static void main(String[] args) throws Exception {
        var fullNameType = "info/manxi/ATest";

        var cw = new ClassWriter(0);
        cw.visit(V11, ACC_PUBLIC, fullNameType, null, "java/lang/Object", null);
        MethodVisitor constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();
        FieldVisitor fv = cw.visitField(ACC_PRIVATE, "name", "Ljava/lang/String;", null, null);
        fv.visitEnd();
        MethodVisitor setName = cw.visitMethod(ACC_PUBLIC, "setName", "(Ljava/lang/String;)V", null, null);
        setName.visitCode();
        //this参数
        setName.visitVarInsn(ALOAD, 0);
        //传入的name参数
        setName.visitVarInsn(ALOAD, 1);
        setName.visitFieldInsn(PUTFIELD, fullNameType, "name", "Ljava/lang/String;");
        setName.visitMaxs(2, 2);
        setName.visitInsn(RETURN);
        setName.visitEnd();

        cw.visitField(ACC_PRIVATE, "a", "I", null, null).visitEnd();
        cw.visitField(ACC_PRIVATE, "b", "I", null, null).visitEnd();

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "sum", "()I", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, fullNameType, "a", "I");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, fullNameType, "b", "I");
        mv.visitInsn(IADD);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();

        MethodVisitor mvc = cw.visitMethod(ACC_PUBLIC, "<init>", "(II)V", null, null);
        mvc.visitVarInsn(ALOAD, 0);
        mvc.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
//        mvc.visitCode();
        mvc.visitVarInsn(ALOAD, 0);
        mvc.visitVarInsn(ILOAD, 1);
        mvc.visitFieldInsn(PUTFIELD, fullNameType, "a", "I");
        mvc.visitVarInsn(ALOAD, 0);
        mvc.visitVarInsn(ILOAD, 2);
        mvc.visitFieldInsn(PUTFIELD, fullNameType, "b", "I");
        mvc.visitInsn(RETURN);
        mvc.visitMaxs(4, 4);
        mvc.visitEnd();


        MethodVisitor getName = cw.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
        getName.visitCode();
        getName.visitVarInsn(ALOAD, 0);
        getName.visitFieldInsn(GETFIELD, fullNameType, "name", "Ljava/lang/String;");
        getName.visitMaxs(1, 1);
        getName.visitInsn(ARETURN);
        getName.visitEnd();
        //完成
        cw.visitEnd();
        byte[] code = cw.toByteArray();
        //可以将其生成class文件保存在磁盘上,或者直接通过classLoad加载
        var fos = new FileOutputStream(new File("./ATest.class"));
        fos.write(code);
        fos.close();

        //自定义ClassLoader
        var classLoader = new MyClassLoader();
        Class<?> cls = classLoader.defineClassPublic(fullNameType.replace("/", "."), code, 0, code.length);
        Object o = cls.newInstance();
        Method setNameMethod = cls.getMethod("setName", String.class);
        setNameMethod.invoke(o, "manxi hello");
        Method getNameMethod = cls.getMethod("getName");
        Object name = getNameMethod.invoke(o);
        System.out.println(name);

        Constructor<?> iic = cls.getConstructor(int.class, int.class);
        Object o1 = iic.newInstance(123, 456);
        Method sum = o1.getClass().getMethod("sum");
        System.out.println(sum.invoke(o1));
    }

    static class MyClassLoader extends ClassLoader {

        public Class<?> defineClassPublic(String name, byte[] b, int off, int len) throws ClassFormatError {
            return super.defineClass(name, b, off, len);
        }
    }
}
