package traben.entity_model_features.models.animation.math.methods.optifine;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Tuple;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

public class IfMethod extends MathMethod {

    public IfMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
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

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        if (parsedArgs.size() == 3) {
            vars.scopeBool();
            parsedArgs.get(0).asmVisit(mv, vars);
            vars.scopePop();
            vars.scopeFloat();
            ASMHelper.asmIf(mv,
                    ()-> parsedArgs.get(1).asmVisit(mv, vars),
                    ()-> parsedArgs.get(2).asmVisit(mv, vars)
                    );
            vars.scopePop();
        } else {
            vars.scopeBool();
            parsedArgs.get(0).asmVisit(mv, vars);
            vars.scopePop();
            vars.scopeFloat();
            ASMHelper.asmIf(mv,
                    ()-> parsedArgs.get(1).asmVisit(mv, vars),
                    ()-> deepIf(2, mv, vars)
            );
            vars.scopePop();
        }
    }

    private void deepIf(int startFrom, MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException  {
        // always scope float here
        if (startFrom == parsedArgs.size() - 1) {
            parsedArgs.get(startFrom).asmVisit(mv, vars);
            return;
        }

        vars.scopeBool();
        parsedArgs.get(startFrom).asmVisit(mv, vars);
        vars.scopePop();

        ASMHelper.asmIf(mv,
                ()-> parsedArgs.get(startFrom + 1).asmVisit(mv, vars),
                ()-> deepIf(startFrom + 2, mv, vars)
        );
    }
}
