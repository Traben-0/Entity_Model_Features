package traben.entity_model_features.models.animation.math.asm;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.utils.EMFUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static org.objectweb.asm.Opcodes.*;

public class ASMParser {

    private static final AtomicLong id = new AtomicLong();

    public static ASMExecutorFloat compileFloatOrNull(MathComponent root, ASMVariableHandler varNames, String expression){
        try {
            var className = "traben.asm_generated.EMF_ASM_Parsed_" + id.incrementAndGet();
            var cw = setupClass(className);

            var mv = cw.visitMethod(
                    ACC_PUBLIC | ACC_STATIC,
                    "eval",
                    "([F[Z)F",
                    null,
                    null
            );

            mv.visitCode();

            varNames.scopeFloat();
            root.asmVisit(mv, varNames);
            varNames.scopePop();
            varNames.verifyEndOfParse();

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();

            cw.visitEnd();

            var bytes = cw.toByteArray();

            var loader = new ClassLoader(ASMHelper.class.getClassLoader()) {
                public Class<?> define() {
                    return defineClass(className, bytes, 0, bytes.length);
                }
            };

            var clazz = loader.define();

            var lookup = MethodHandles.lookup();

            var mh = lookup.findStatic(
                    clazz,
                    "eval",
                    MethodType.methodType(float.class, float[].class, boolean[].class)
            );

            return (f, b) -> {
                try {
                    return (float) mh.invokeExact(f, b);
                } catch (Throwable e) {
                    EMFUtils.logError(" Math error: " + expression + " = " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            };
        } catch (Throwable e) {
            handleParseException(expression, e);
            return null;
        }

    }

    private static @NotNull ClassWriter setupClass(String className) {
        var internal = className.replace('.', '/');

        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        cw.visit(V17, ACC_PUBLIC | ACC_SUPER, internal, null, "java/lang/Object", null);

        // constructor
        var mv0 = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv0.visitCode();
        mv0.visitVarInsn(ALOAD, 0);
        mv0.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv0.visitInsn(RETURN);
        mv0.visitMaxs(1, 1);
        mv0.visitEnd();
        return cw;
    }

    public static ASMExecutorBool compileBoolOrNull(MathComponent root, ASMVariableHandler varNames, String expression){
        try {
            var className = "traben.asm_generated.EMF_ASM_Parsed_" + id.incrementAndGet();

            var cw = setupClass(className);

            var mv = cw.visitMethod(
                    ACC_PUBLIC | ACC_STATIC,
                    "eval",
                    "([F[Z)Z",
                    null,
                    null
            );

            mv.visitCode();

            varNames.scopeBool();
            root.asmVisit(mv, varNames);
            varNames.scopePop();
            varNames.verifyEndOfParse();

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();

            cw.visitEnd();

            var bytes = cw.toByteArray();

            var loader = new ClassLoader(ASMHelper.class.getClassLoader()) {
                public Class<?> define() {
                    return defineClass(className, bytes, 0, bytes.length);
                }
            };

            var clazz = loader.define();

            var lookup = MethodHandles.lookup();

            var mh = lookup.findStatic(
                    clazz,
                    "eval",
                    MethodType.methodType(boolean.class, float[].class, boolean[].class)
            );

            return (f, b) -> {
                try {
                    return (boolean) mh.invokeExact(f, b);
                } catch (Throwable e) {
                    EMFUtils.logError(" Math error: " + expression + " = " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            };
        } catch (Throwable e) {
            handleParseException(expression, e);
            return null;
        }

    }

    private static void handleParseException(String expression, Throwable e) {
        EMFUtils.logError("Parsing ASM for expression: " + expression);

        var text = Arrays.toString(e.getStackTrace());

        if (text.matches(".*ireturn.*Reason:.*Type float .* is not assignable to integer.*"))
            EMFUtils.logError(" - expression did not result in a boolean!");

        if (text.matches(".*freturn.*Reason:.*Type integer .* is not assignable to float.*"))
            EMFUtils.logError(" - expression did not result in a number!");


        e.printStackTrace();
    }

    public interface ASMExecutorBool {
        boolean execute(float[] floats, boolean[] bools) throws Throwable;
    }

    public interface ASMExecutorFloat {
        float execute(float[] floats, boolean[] bools) throws Throwable;
    }

}
