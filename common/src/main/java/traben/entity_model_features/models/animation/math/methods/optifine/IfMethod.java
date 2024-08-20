package traben.entity_model_features.models.animation.math.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.MathValue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Tuple;

public class IfMethod extends MathMethod {


    public IfMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var parsedArgs = parseAllArgs(args, calculationInstance);

        if (parsedArgs.size() == 3) {
            //simple if statement
            var bool = parsedArgs.get(0);
            var tru = parsedArgs.get(1);
            var fals = parsedArgs.get(2);

            //if the condition is constant, we can optimize to only use the correct branch
            if (bool.isConstant()) {
                setOptimizedAlternativeToThis(MathValue.toBoolean(bool.getResult()) ? tru : fals);
            }

            setSupplierAndOptimize(() -> MathValue.toBoolean(bool.getResult()) ? tru.getResult() : fals.getResult()
            , parsedArgs);
        } else {

            List<Tuple<MathComponent, MathComponent>> ifSets = new ArrayList<>();
            var lastElse = parsedArgs.get(parsedArgs.size() - 1);

            for (int i = 0; i < parsedArgs.size() - 1; i += 2) {
                MathComponent condition = parsedArgs.get(i);
                MathComponent result = parsedArgs.get(i + 1);

                if (!condition.isConstant()) {
                    MathValue.toBoolean(condition.getResult()); // Validate boolean
                    ifSets.add(new Tuple<>(condition, result));
                } else if (MathValue.toBoolean(condition.getResult())) {
                    lastElse = result;
                    break;
                }
            }
            if (ifSets.isEmpty()) {//due to all conditions being false constants
                setOptimizedAlternativeToThis(lastElse);
            }

            final MathComponent finalElse = lastElse;
            setSupplierAndOptimize(() -> {
                for (Tuple<MathComponent, MathComponent> ifSet : ifSets) {
                    if (MathValue.toBoolean(ifSet.getA().getResult())) {
                        return ifSet.getB().getResult();
                    }
                }
                return finalElse.getResult();
            }, parsedArgs);
        }
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 3 && argCount % 2 == 1;
    }

}
