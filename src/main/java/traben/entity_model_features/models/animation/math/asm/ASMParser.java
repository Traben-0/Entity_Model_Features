package traben.entity_model_features.models.animation.math.asm;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import traben.entity_model_features.models.animation.math.expression_tree.OldEMFAnimationHandler;
import traben.entity_model_features.utils.EMFUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.atomic.AtomicLong;

import static org.objectweb.asm.Opcodes.*;

public class ASMParser {

    private static final AtomicLong id = new AtomicLong();

    public interface ASMExecutor {
        void execute(float[] floats, boolean[] bools) throws Throwable;
    }

    public static ASMExecutor compileOrNull(OldEMFAnimationHandler animHandler, ASMVariableHandler varNames) {
        try {
            var className = "traben.asm_generated.EMF_ASM_Parsed_" + id.incrementAndGet();
            var cw = setupClass(className);

            var mv = cw.visitMethod(
                    ACC_PUBLIC | ACC_STATIC,
                    "eval",
                    "([F[Z)V",
                    null,
                    null
            );

            mv.visitCode();

            for (int i = 0; i < animHandler.lines().size(); i++) {
                var line = animHandler.lines().get(i);
                var oldAnim = animHandler.oldAnimLines.get(line.animKey);

                assert oldAnim != null;

                varNames.scope(line.isBoolean);
                oldAnim.asmVisit(mv, varNames); // Leaves result on stack
                line.asmIndex = varNames.asmStoreVar(mv, line.animKey); // Store result and empty stack
                varNames.scopePop();
                varNames.verifyEndOfParse();
            }

            mv.visitInsn(RETURN);

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
                    MethodType.methodType(void.class, float[].class, boolean[].class)
            );

            return (f, b) -> {
                try {
                    mh.invokeExact(f, b);
                } catch (Throwable e) {
                    EMFUtils.logError(" Math error: " + animHandler + " = " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            };
        } catch (Throwable e) {
            handleParseException(animHandler.toString(), e);
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

    private static void handleParseException(String expression, Throwable e) {
        EMFUtils.logError("Parsing ASM for expression: " + expression);

//        var text = Arrays.toString(e.getStackTrace());

//        if (text.matches(".*ireturn.*Reason:.*Type float .* is not assignable to integer.*"))
//            EMFUtils.logError(" - expression did not result in a boolean!");
//
//        if (text.matches(".*freturn.*Reason:.*Type integer .* is not assignable to float.*"))
//            EMFUtils.logError(" - expression did not result in a number!");


        e.printStackTrace();
    }
}
