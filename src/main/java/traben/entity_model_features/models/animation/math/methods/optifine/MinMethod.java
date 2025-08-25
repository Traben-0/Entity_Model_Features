package traben.entity_model_features.models.animation.math.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.ArrayList;
import java.util.List;

public class MinMethod extends MathMethod {


    public MinMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var parsedArgs = parseAllArgs(args, calculationInstance);
        var initial = parsedArgs.get(0);
        var theRest = new ArrayList<>(parsedArgs);
        theRest.remove(0);

        setSupplierAndOptimize(() -> {
            float min = initial.getResult();
            for (MathComponent parsedArg : theRest) {
                float val = parsedArg.getResult();
                if (val < min) {
                    min = val;
                }
            }
            return min;
        }, parsedArgs);
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 2;
    }

}
