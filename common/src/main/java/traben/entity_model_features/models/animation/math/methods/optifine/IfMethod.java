package traben.entity_model_features.models.animation.math.methods.optifine;

import net.minecraft.util.Pair;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.ArrayList;
import java.util.List;

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
                setOptimizedAlternativeToThis(bool.getResult() == 1 ? tru : fals);
            }

            setSupplierAndOptimize(() -> {
                float condition = bool.getResult();
                if (condition == 1) {
                    return tru.getResult();
                } else {
                    return fals.getResult();
                }
            }, parsedArgs);
        } else {

            List<Pair<MathComponent, MathComponent>> ifSets = new ArrayList<>();
            var lastElse = parsedArgs.get(parsedArgs.size() - 1);

            var iterator = parsedArgs.iterator();
            while (iterator.hasNext()) {
                var next = iterator.next();
                if (iterator.hasNext()) {
                    if (!next.isConstant()) {
                        ifSets.add(new Pair<>(next, iterator.next()));
                    } else if (next.getResult() == 1) {
                        //if next is a true constant then end here
                        lastElse = iterator.next();
                        break;
                    }//if next is a false constant then skip entirely
                }
            }
            if (ifSets.isEmpty()) {//due to all conditions being false constants
                setOptimizedAlternativeToThis(lastElse);
            }

            final MathComponent finalElse = lastElse;
            setSupplierAndOptimize(() -> {
                for (Pair<MathComponent, MathComponent> ifSet : ifSets) {
                    if (ifSet.getLeft().getResult() == 1) {
                        return ifSet.getRight().getResult();
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
