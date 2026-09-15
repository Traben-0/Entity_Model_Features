package traben.entity_model_features.models.animation.math.asm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.*;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFException;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.OldEMFAnimationHandler;
import traben.entity_model_features.utils.EMFUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings("CallToPrintStackTrace")
public class ASMParser {

    private static final AtomicLong id = new AtomicLong();

    public interface ASMExecutor {
        void execute(float[] floats, boolean[] bools) throws Throwable;
    }

    /**
     * @return may be either an ASMAnimationHandler or a MultiASMAnimationHandler, or null if the compilation failed
     */
    public static EMFAnimationHandler getOrNull(OldEMFAnimationHandler oldAnimationHandler, AnimSetupContext context) {
        try {
            var varHandler = new ASMVariableHandler();
            var executor = ASMParser.compileOrNull(oldAnimationHandler.oldAnimLines, varHandler);
            if (executor == null) {
                return null;
            }
            // Vast majority of animations should be this
            return new ASMAnimationHandler(executor, context, -1).complete(varHandler, context);
        } catch (NeedToSplitAnimationsException e) {
            // These animations are simply too large to be compiled into one method, we will recursively split them into smaller line counts and compile those instead
            var list = splitAndGetOrNull(context, oldAnimationHandler.oldAnimLines, oldAnimationHandler.modelName, 2);
            if (list == null) return null;
            return new MultiASMAnimationHandler(oldAnimationHandler.modelName, oldAnimationHandler.lines(), list.toArray(new ASMAnimationHandler[0]));
        }
    }

    private static @Nullable List<ASMAnimationHandler> splitAndGetOrNull(AnimSetupContext context, LinkedHashMap<EMFAnimationHandler.AnimLineData, MathComponent> lines, String name, int splits) {
        if (lines.size() < splits) {
            //TODO I could consider a solution to this but tbh, anyone that wrote a single line this long and complex
            //     should be taken out back and executed. Just split up your lines
            EMFUtils.logError("ASMAnimationHandler failed to compile animation for " + name +
                    " due to the lines being extremely long, Please consider breaking up your animation lines more");
            return null;
        }

        var splitSize = (int) Math.ceil((double) lines.size() / splits);
        List<LinkedHashMap<EMFAnimationHandler.AnimLineData, MathComponent>> splitList = new ArrayList<>(splits);

        var iterator = lines.entrySet().iterator();
        while (iterator.hasNext()) {
            LinkedHashMap<EMFAnimationHandler.AnimLineData, MathComponent> map = new LinkedHashMap<>();
            for (int i = 0; i < splitSize && iterator.hasNext(); i++) {
                var entry = iterator.next();
                map.put(entry.getKey(), entry.getValue());
            }
            if (!map.isEmpty()) splitList.add(map);
        }

        try {
            // Share varHandler across them all.
            // This is un-optimal but if we are to make a separate var handler for each chunk then the optimizations within each
            // ASMAnimationHandler would need to account for values that are marked unused actually being used by another.
            // Doing this is simpler and should represent the better run-time optimization outcome.
            var varHandler = new ASMVariableHandler();

            var list = new ArrayList<ASMAnimationHandler>(splitList.size());
            int index = 0;
            for (var split : splitList) {
                if (split.isEmpty()) continue;

                var executor = ASMParser.compileOrNull(split, varHandler);
                if (executor == null) {
                    return null;
                }
                list.add(new ASMAnimationHandler(executor, context, index++));
            }
            for (var handler : list) {
                handler.complete(varHandler, context);
            }
            return list.isEmpty() ? null : list;
        } catch (NeedToSplitAnimationsException e) {
            if (splits > 32) { // Should be overkill
                EMFUtils.logError("ASMAnimationHandler failed to compile animation for " + name + " due to too being extremely large");
                return null;
            }
            return splitAndGetOrNull(context, lines, name, splits * 2);
        }
    }

    private static ASMExecutor compileOrNull(LinkedHashMap<EMFAnimationHandler.AnimLineData, MathComponent> lines, ASMVariableHandler varNames) throws NeedToSplitAnimationsException {
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

            for (var entry : lines.entrySet()) {
                var line = entry.getKey();
                var oldAnim = entry.getValue();
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
                    EMFUtils.logError(" Math error: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            };
        } catch (Throwable e) {
            handleParseException(lines, e);
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

    private static void handleParseException(LinkedHashMap<EMFAnimationHandler.AnimLineData, MathComponent> lines, Throwable e) throws NeedToSplitAnimationsException {
        if (e instanceof MethodTooLargeException) {
            // Probably redundant but I can picture possibly needing other reasons to run the split algorithm
            throw new NeedToSplitAnimationsException();
        }

        EMFUtils.logError("Failure parsing ASM:");

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
        }
        var text = sw.toString();

        boolean hadSimpleReason = true;

        // TODO expand with further known failure reasons
        // TODO if this gets too long look into simplifying these checks
        if (P_BASTORE_FLOAT_TO_INT.matcher(text).find()) {
            EMFUtils.logWarn(" - expected a boolean but found a number at the end of the expression?!");
        } else if (P_FASTORE_INT_TO_FLOAT.matcher(text).find()) {
            EMFUtils.logWarn(" - expected a number but found a boolean at the end of the expression?!");
        } else if (P_FLOAT_TO_INT.matcher(text).find()) {
            EMFUtils.logWarn(" - expected a boolean but found a number within the expression?!");
        } else if (P_INT_TO_FLOAT.matcher(text).find()) {
            EMFUtils.logWarn(" - expected a number but found a boolean within the expression?!");
        } else {
            hadSimpleReason = false;
        }

        if (EMF.config().getConfig().logModelCreationData) {
            StringBuilder sb = new StringBuilder("Animations:\n");
            for (var it : lines.keySet()) {
                sb.append("  - ").append(it.animKey).append(" : ").append(it.expression).append('\n');
            }
            EMFUtils.logWarn(sb.toString());
        }

        if (!hadSimpleReason || EMF.config().getConfig().logModelCreationData)
            e.printStackTrace();
    }

    private static final Pattern P_BASTORE_FLOAT_TO_INT =
            Pattern.compile("bastore.*Reason:.*Type float .* is not assignable to integer", Pattern.DOTALL);
    private static final Pattern P_FASTORE_INT_TO_FLOAT =
            Pattern.compile("fastore.*Reason:.*Type integer .* is not assignable to float", Pattern.DOTALL);
    private static final Pattern P_FLOAT_TO_INT =
            Pattern.compile("Reason:.*Type float .* is not assignable to integer", Pattern.DOTALL);
    private static final Pattern P_INT_TO_FLOAT =
            Pattern.compile("Reason:.*Type integer .* is not assignable to float", Pattern.DOTALL);

    public static class NeedToSplitAnimationsException extends EMFException {
        public NeedToSplitAnimationsException() {
            super("ASMParser: ASM code is too large to be valid. We will split up the anims and try again.");
        }
    }
}
