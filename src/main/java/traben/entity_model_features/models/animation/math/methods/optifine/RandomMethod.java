package traben.entity_model_features.models.animation.math.methods.optifine;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

import java.util.List;

public class RandomMethod extends MathMethod {

    protected final boolean hasSeed;

    public RandomMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        hasSeed = args.size() == 1 && !args.get(0).isBlank();
        if (hasSeed) {
            var arg = parsedArgs.get(0);
            setSupplierAndOptimize(() -> nextValue(arg.getResult()), arg);
        } else {
            //true random
            setSupplierAndOptimize(RandomMethod::nextValueBasic);
        }
    }

    public static float nextValue(float seed) {
        int hash = optifineIntHash(Float.floatToIntBits(seed));
        return (float)Math.abs(hash) / 2.14748365E9F;
    }

    public static float nextValueBasic() {
        return (float) Math.random();
    }

    public static int optifineIntHash(int x) {
        x = x ^ 61 ^ x >> 16;
        x += x << 3;
        x ^= x >> 4;
        x *= 668265261;
        return x ^ x >> 15;
    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return hasSeed;
    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        if (hasSeed) {
            vars.scopeFloat();
            parsedArgs.get(0).asmVisit(mv, vars);
            vars.scopePop();
            ASMHelper.visitStaticFunctionASM(mv, "nextValue", RandomMethod.class);
        } else {
            ASMHelper.visitStaticFunctionASM(mv, "nextValueBasic", RandomMethod.class);
        }
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 1 || argCount == 0;
    }

}
