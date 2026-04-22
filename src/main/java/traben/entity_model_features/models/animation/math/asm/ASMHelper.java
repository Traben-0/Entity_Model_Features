package traben.entity_model_features.models.animation.math.asm;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.math.EMFMathException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public abstract class ASMHelper {
    public static void visitHelperFunctionASM(MethodVisitor mv, String name) throws EMFMathException {
        visitStaticFunctionASM(mv, name, ASMHelper.class);
    }

    public static Method getHelperMethod(String name) throws EMFMathException {
        var method = Arrays.stream(ASMHelper.class.getMethods())
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
        if (method.isPresent()) {
            return method.get();
        }
        throw new EMFMathException("Method [" + name + "] not found in ASMHelper");
    }

    public static @Nullable ASMVisitable getHelperMethodCompiler(String name) {
        var method = Arrays.stream(ASMHelper.class.getMethods())
                .filter(m -> m.getName().equalsIgnoreCase(name + "_ASM"))
                .findFirst();
        if (method.isPresent()) {
            try {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandle target = lookup.unreflect(method.get());
                return MethodHandleProxies.asInterfaceInstance(ASMVisitable.class, target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Method getStaticMethod(String name, Class<?> clazz) throws EMFMathException {
        var method = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
        if (method.isPresent()) {
            return method.get();
        }
        throw new EMFMathException("Method [" + name + "] not found in " + clazz.getName());
    }



    public static void visitStaticFunctionASM(MethodVisitor mv, String name, Class<?> clazz) throws EMFMathException {
        var func = functionName(name, clazz);
        mv.visitMethodInsn(
                org.objectweb.asm.Opcodes.INVOKESTATIC,
                func[0],
                func[1],
                func[2],
                false
        );
    }

    public static void visitStaticFunctionASM(MethodVisitor mv, Method method) {
        var func = fromMethod(method.getDeclaringClass(), method);
        mv.visitMethodInsn(
                org.objectweb.asm.Opcodes.INVOKESTATIC,
                func[0],
                func[1],
                func[2],
                false
        );
    }

    public static void asmIfZeroOrGreater(MethodVisitor mv, ThrowingRunnable trueBlock, ThrowingRunnable falseBlock) throws EMFMathException {
        var labelTrue = new Label();
        var end = new Label();
        mv.visitInsn(DUP);
        mv.visitInsn(FCONST_0);
        mv.visitInsn(FCMPG);
        mv.visitJumpInsn(IFGE, labelTrue);
        falseBlock.run();
        mv.visitJumpInsn(GOTO, end);

        mv.visitLabel(labelTrue);
        trueBlock.run();

        mv.visitLabel(end);
    }

    public static void asmIf(MethodVisitor mv, ThrowingRunnable trueBlock, ThrowingRunnable falseBlock) throws EMFMathException {
        var labelTrue = new Label();
        var end = new Label();

        mv.visitJumpInsn(IFNE, labelTrue);
        falseBlock.run();
        mv.visitJumpInsn(GOTO, end);

        mv.visitLabel(labelTrue);
        trueBlock.run();

        mv.visitLabel(end);
    }

    public interface ThrowingRunnable {
        void run() throws EMFMathException;
    }

    //region STATIC HELPER FUNCS

    // Cannot use minecraft classes in ASM , so will use these, they should get jit compiled away in runtime anyway
    // also just lazily using these to perform float casts for all the Math methods that don't return float, again jit will clean these up nicely
    public static float sin(float f) { return Mth.sin(f); }
    public static float cos(float f) { return Mth.cos(f); }
    public static float asin(float f) { return (float) Math.asin(f); }
    public static float acos(float f) { return (float) Math.acos(f); }
    public static float tan(float f) { return (float) Math.tan(f); }
    public static float atan(float f) { return (float) Math.atan(f); }
    public static float abs(float f) { return Mth.abs(f); }
    public static float floor(float f) { return (float) Mth.floor(f); }
    public static float ceil(float f) { return (float) Mth.ceil(f); }
    public static float round(float f) { return (float) Math.round(f); }
    public static float log(float f) { return f < 0 && EMFManager.getInstance().isAnimationValidationPhase ? 0 : (float) Math.log(f); }
    public static float _log(float f) { return (float) Math.log(f); }
    public static void log_ASM(MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException {
        // remove validation check
        asmIfZeroOrGreater(mv,
                ()-> visitHelperFunctionASM(mv, "_log"),
                ()-> {
                        mv.visitInsn(POP);
                        mv.visitInsn(FCONST_0);
                }

        );
    }
    public static float exp(float f) { return (float) Math.exp(f); }
    public static float torad(float f) { return f * Mth.DEG_TO_RAD; }
    public static void torad_ASM(MethodVisitor mv, ASMVariableHandler varNames) {
        // easy
        mv.visitLdcInsn(Mth.DEG_TO_RAD);
        mv.visitInsn(FMUL);
    }
    public static float todeg(float f) { return f * Mth.RAD_TO_DEG; }
    public static void todeg_ASM(MethodVisitor mv, ASMVariableHandler varNames) {
        // easy
        mv.visitLdcInsn(Mth.RAD_TO_DEG);
        mv.visitInsn(FMUL);
    }
    public static float frac(float f) { return Mth.frac(f); }
    public static float signum(float f) { return Math.signum(f); }
    public static float sqrt(float f) { return f < 0 && EMFManager.getInstance().isAnimationValidationPhase ? 0 : Mth.sqrt(f); }
    public static float _sqrt(float f) { return Mth.sqrt(f); }
    public static void sqrt_ASM(MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException {
        // remove validation check
        asmIfZeroOrGreater(mv,
                ()-> visitHelperFunctionASM(mv, "_sqrt"),
                ()-> {
                    mv.visitInsn(POP);
                    mv.visitInsn(FCONST_0);
                }

        );
    }
    public static float fmod(float f, float f2) { return (float) Math.floorMod((int) f, (int) f2); }
    public static float pow(float f, float f2) { return (float) Math.pow(f, f2); }
    public static float atan2(float f, float f2) { return (float) Mth.atan2(f, f2); }
    public static float clamp(float f, float f2, float f3) { return Mth.clamp(f, f2, f3); }
    public static float lerp(float f, float f2, float f3) { return Mth.lerp(f, f2, f3); }


    public static float wrapdeg(float f) { return Mth.wrapDegrees(f); }
    public static float wraprad(float f) { return torad(Mth.wrapDegrees(todeg(f))); }
    public static float degdiff(float f, float f2) { return Mth.degreesDifferenceAbs(f, f2); }
    public static float raddiff(float f, float f2) { return torad(Mth.degreesDifferenceAbs(todeg(f), todeg(f2))); }


    public static float catmullrom(float f, float f2, float f3, float f4, float f5) { return Mth.catmullrom(f, f2, f3, f4, f5); }

    public static boolean between(float a, float b, float c) { return !(a > c) && (!(a < b)); }
    public static boolean equals(float x, float y, float epsilon) { return Math.abs(y - x) <= epsilon; }

    public static boolean notPausedNotValidation() { return !Minecraft.getInstance().isPaused() /* && !EMFManager.getInstance().isAnimationValidationPhase */; }


    public static float quadraticBezier(float t, float p0, float p1, float p2) {
        float oneMinusT = 1f - t;
        return oneMinusT * oneMinusT * p0 + 2f * oneMinusT * t * p1 + t * t * p2;
    }

    public static float cubicBezier(float t, float p0, float p1, float p2, float p3) {
        float oneMinusT = 1f - t;
        float oneMinusTSquared = oneMinusT * oneMinusT;
        float tSquared = t * t;
        return oneMinusTSquared * oneMinusT * p0 + 3f * oneMinusTSquared * t * p1 + 3f * oneMinusT * tSquared * p2 + tSquared * t * p3;
    }

    public static float hermiteInterpolation(float t, float p0, float p1, float m0, float m1) {
        float tSquared = t * t;
        float tCubed = tSquared * t;

        float h00 = 2 * tCubed - 3 * tSquared + 1;
        float h10 = tCubed - 2 * tSquared + t;
        float h01 = -2 * tCubed + 3 * tSquared;
        float h11 = tCubed - tSquared;

        return h00 * p0 + h10 * m0 + h01 * p1 + h11 * m1;
    }


    //region easings
    public static float easeInQuad(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t;
    }

    public static float easeOutQuad(float t, float start, float end) {
        float delta = end - start;
        return start + delta * -t * (t - 2);
    }

    public static float easeInOutQuad(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * (2 * t * t);
        } else {
            return start + delta * (-2 * t * (t - 2) - 1);
        }
    }

    public static float easeInCubic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t * t;
    }

    public static float easeOutCubic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t = t - 1) * t * t + 1;
    }

    public static float easeInOutCubic(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * 4 * t * t * t;
        } else {
            return start + delta * (t = t - 1) * (2 * t * t + 2) + 1;
        }
    }

    public static float easeInQuart(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t * t * t;
    }

    public static float easeOutQuart(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t = t - 1) * t * t * t + 1;
    }

    public static float easeInOutQuart(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * 8 * t * t * t * t;
        } else {
            return start + delta * (t = t - 1) * (8 * t * t * t + 1) + 1;
        }
    }

    public static float easeInQuint(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t * t * t * t;
    }

    public static float easeOutQuint(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t = t - 1) * t * t * t * t + 1;
    }

    public static float easeInOutQuint(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * 16 * t * t * t * t * t;
        } else {
            return start + delta * (t = t - 1) * (16 * t * t * t * t + 1) + 1;
        }
    }

    public static float easeInSine(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (1 - (float) Math.cos(t * Math.PI / 2));
    }

    public static float easeOutSine(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) Math.sin(t * Math.PI / 2);
    }

    public static float easeInOutSine(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (-0.5 * (Math.cos(Math.PI * t) - 1));
    }

    public static float easeInExpo(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) Math.pow(2, 10 * (t - 1));
    }

    public static float easeOutExpo(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (-Math.pow(2, -10 * t) + 1);
    }

    public static float easeInOutExpo(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 1) {
            return start + delta * (float) (0.5 * Math.pow(2, 10 * (t - 1)));
        } else {
            return start + delta * (float) (0.5 * (-Math.pow(2, -10 * --t) + 2));
        }
    }

    public static float easeInCirc(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) -(Math.sqrt(1 - t * t) - 1);
    }

    public static float easeOutCirc(float t, float start, float end) {
        float delta = end - start;
        float tMinus1 = t - 1;
        return start + delta * (float) Math.sqrt(1 - tMinus1 * tMinus1);
    }

    public static float easeInOutCirc(float t, float start, float end) {
        float delta = end - start;
        float tTimes2 = t * 2;
        if (tTimes2 < 1) {
            return start + delta * (float) (-0.5 * (Math.sqrt(1 - tTimes2 * tTimes2) - 1));
        } else {
            float tTimes2Minus2 = tTimes2 - 2;
            return start + delta * (float) (0.5 * (Math.sqrt(1 - tTimes2Minus2 * tTimes2Minus2) + 1));
        }
    }

    public static float easeInElastic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (-Math.pow(2, 10 * (t -= 1)) * Math.sin((t - 0.3 / 4) * (2 * Math.PI) / 0.3));
    }

    public static float easeOutElastic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (Math.pow(2, -10 * t) * Math.sin((t - 0.3 / 4) * (2 * Math.PI) / 0.3) + 1);
    }

    public static float easeInOutElastic(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * (float) (-0.5 * Math.pow(2, 10 * (t -= 1)) * Math.sin((t - 0.225 / 4) * (2 * Math.PI) / 0.45));
        } else {
            return start + delta * (float) (0.5 * Math.pow(2, -10 * (t -= 1)) * Math.sin((t - 0.225 / 4) * (2 * Math.PI) / 0.45) + 1);
        }
    }

    public static float easeInBounce(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (1 - easeOutBounce(1 - t, 0, 1));
    }

    public static float easeOutBounce(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < (1 / 2.75)) {
            return start + delta * (7.5625f * t * t);
        } else if (t < (2 / 2.75)) {
            return (float) (start + delta * (7.5625f * (t -= (float) (1.5 / 2.75)) * t + .75));
        } else if (t < (2.5 / 2.75)) {
            return (float) (start + delta * (7.5625f * (t -= (float) (2.25 / 2.75)) * t + .9375));
        } else {
            return (float) (start + delta * (7.5625f * (t -= (float) (2.625 / 2.75)) * t + .984375));
        }
    }

    public static float easeInOutBounce(float t, float start, float end) {
        float delta = end - start;
        if (t < 0.5) {
            return start + delta * (0.5f * easeInBounce(t * 2, 0, 1));
        } else {
            return start + delta * (0.5f * easeOutBounce(t * 2 - 1, 0, 1) + 0.5f);
        }
    }

    public static float easeInBack(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t * t * (2.70158f * t - 1.70158f));
    }

    public static float easeOutBack(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (--t * t * (2.70158f * t + 1.70158f) + 1);
    }

    public static float easeInOutBack(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * (t * t * (7 * t - 2.5f) * 2);
        } else {
            return start + delta * ((--t * t * (7 * t + 2.5f) + 2) * 2);
        }
    }
    //endregion



    //region method reflect boilerplate

    private static String[] functionName(String name, Class<?> clazz) throws EMFMathException {
        var method = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst();
        if (method.isPresent()) {
            return fromMethod(clazz, method.get());
        }
        throw new EMFMathException("Method [" + name + "] not found in ASMHelper");
    }

    private static String @NotNull [] fromMethod(Class<?> clazz, Method method) {
        return new String[]{
                clazz.getName().replace('.', '/'),
                method.getName(),
                methodDescriptor(method)
        };
    }

    private static String methodDescriptor(Method m) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Class<?> p : m.getParameterTypes()) {
            sb.append(typeDescriptor(p));
        }
        sb.append(')');
        sb.append(typeDescriptor(m.getReturnType()));
        return sb.toString();
    }

    private static String typeDescriptor(Class<?> c) {
        if (c.isPrimitive()) {
            if (c == void.class) return "V";
            else if (c == int.class) return "I";
            else if (c == boolean.class) return "Z";
            else if (c == byte.class) return "B";
            else if (c == char.class) return "C";
            else if (c == short.class) return "S";
            else if (c == long.class) return "J";
            else if (c == float.class) return "F";
            else if (c == double.class) return "D";
            throw new AssertionError();
        }
        if (c.isArray()) {
            return c.getName().replace('.', '/');
        }
        return "L" + c.getName().replace('.', '/') + ";";
    }

    //endregion
}
