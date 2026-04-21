package traben.entity_model_features.models.animation.math.methods.emf;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.methods.optifine.RandomMethod;

import java.util.List;

public class RandomBMethod extends MathMethod {

    protected final boolean hasSeed;

    public RandomBMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        hasSeed = args.size() == 1 && !args.get(0).isBlank();
        if (hasSeed) {
            var arg = parsedArgs.get(0);
            setSupplierAndOptimize(() -> MathValue.fromBoolean(nextValue(arg.getResult())), arg);
        } else {
            //true random
            setSupplierAndOptimize(()-> MathValue.fromBoolean(RandomBMethod.nextValueBasic()));
        }
    }

    public static boolean nextValue(float seed) {
        return RandomMethod.nextValue(seed) >= 0.5f;
    }

    public static boolean nextValueBasic() {
        return RandomMethod.nextValueBasic() >= 0.5f;
    }


    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        if (hasSeed) {
            vars.scopeFloat();
            parsedArgs.get(0).asmVisit(mv, vars);
            vars.scopePop();
            ASMHelper.visitStaticFunctionASM(mv, "nextValue", RandomBMethod.class);
        } else {
            ASMHelper.visitStaticFunctionASM(mv, "nextValueBasic", RandomBMethod.class);
        }
    }

    @Override
    protected boolean hasCorrectArgCount(int argCount) {
        return argCount == 1 || argCount == 0;
    }
}
