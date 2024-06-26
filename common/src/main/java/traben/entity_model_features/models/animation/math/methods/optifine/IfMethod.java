package traben.entity_model_features.models.animation.math.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.MathValue;

import java.util.ArrayList;
import java.util.Iterator;
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

            setSupplierAndOptimize(() -> {
                float condition = bool.getResult();
                if (MathValue.toBoolean(condition)) {
                    return tru.getResult();
                } else {
                    return fals.getResult();
                }
            }, parsedArgs);
        } else {

            List<Tuple<MathComponent, MathComponent>> ifSets = new ArrayList<>();
            var lastElse = parsedArgs.get(parsedArgs.size() - 1);

            var iterator = parsedArgs.iterator();
            while (iterator.hasNext()) {
                var next = iterator.next();
                if (iterator.hasNext()) {
                    if (!next.isConstant()) {
                        //test is valid boolean won't throw exception
                        //noinspection ResultOfMethodCallIgnored
                        MathValue.toBoolean(next.getResult());

                        ifSets.add(new Tuple<>(next, iterator.next()));
                    } else if (MathValue.toBoolean(next.getResult())) {
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
