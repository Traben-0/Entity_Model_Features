package traben.entity_model_features.models.animation.math.methods.emf;

import org.objectweb.asm.MethodVisitor;
import oshi.util.tuples.Pair;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

import java.util.ArrayList;
import java.util.List;

public class IfBMethod extends MathMethod {


    public IfBMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);


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

            List<Pair<MathComponent, MathComponent>> ifSets = new ArrayList<>();
            var lastElse = parsedArgs.get(parsedArgs.size() - 1);

            for (int i = 0; i < parsedArgs.size() - 1; i += 2) {
                MathComponent condition = parsedArgs.get(i);
                MathComponent result = parsedArgs.get(i + 1);

                if (!condition.isConstant()) {
                    MathValue.toBoolean(condition.getResult()); // Validate boolean
                    ifSets.add(new Pair<>(condition, result));
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
                for (Pair<MathComponent, MathComponent> ifSet : ifSets) {
                    if (MathValue.toBoolean(ifSet.getA().getResult())) {
                        return ifSet.getB().getResult();
                    }
                }
                return finalElse.getResult();
            }, parsedArgs);
        }

        var current = supplier;
        //validate output is boolean
        supplier = () -> MathValue.validateBoolean(current.get());
        if (optimizedAlternativeToThis != null) {
            var current2 = optimizedAlternativeToThis;
            optimizedAlternativeToThis = MathComponent.validateBooleanDelegate(current2);
        }
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 3 && argCount % 2 == 1;
    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        vars.scopeBool();
        if (parsedArgs.size() == 3) {
            parsedArgs.get(0).asmVisit(mv, vars);
            ASMHelper.asmIf(mv,
                    ()-> parsedArgs.get(1).asmVisit(mv, vars),
                    ()-> parsedArgs.get(2).asmVisit(mv, vars)
            );
        } else {
            parsedArgs.get(0).asmVisit(mv, vars);
            ASMHelper.asmIf(mv,
                    ()-> parsedArgs.get(1).asmVisit(mv, vars),
                    ()-> deepIf(2, mv, vars)
            );
        }
        vars.scopePop();
    }

    private void deepIf(int startFrom, MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException  {
        parsedArgs.get(startFrom).asmVisit(mv, varNames);

        if (startFrom == parsedArgs.size() - 1) return;

        ASMHelper.asmIf(mv,
                ()-> parsedArgs.get(startFrom + 1).asmVisit(mv, varNames),
                ()-> deepIf(startFrom + 2, mv, varNames)
        );
    }

}
