package traben.entity_model_features.models.animation.math;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;

import static org.objectweb.asm.Opcodes.FCMPL;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FNEG;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IXOR;


public class MathVariable extends MathValue implements MathComponent {


    private final ResultSupplier resultSupplier;
    private final String name;

    public MathVariable(String variableName, boolean isNegative, ResultSupplier supplier) {
        super(isNegative);
        resultSupplier = supplier;
        name = variableName;
    }

    public MathVariable(String variableName, ResultSupplier supplier) {
        resultSupplier = supplier;
        name = variableName;
    }


    static MathComponent getOptimizedVariable(String variableName, boolean isNegative, AnimSetupContext context) {
        if (variableName.startsWith("-")) {//catch mistake of double negative
            return VariableRegistry.getInstance().getVariable(variableName.substring(1), true, context);
        }
        return VariableRegistry.getInstance().getVariable(variableName, isNegative, context);
    }

    @Override
    ResultSupplier getResultSupplier() {
        return resultSupplier;
    }

    @Override
    public String toString() {
        return "variable[" + name + "]=" + getResult();
    }


    @Override
    public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) {

        boolean inverted = name.startsWith("!");

        boolean negativeAgain = name.startsWith("-"); // .. TODO something is surely wrong but this works

        var name = inverted || negativeAgain ? this.name.substring(1) : this.name;

        vars.asmVisitVar(mv, name);

        if (inverted) vars.asmInvertBoolean(mv);
        if (isNegative || negativeAgain) vars.asmNegateFloat(mv); // TODO test "-(age)" works from "negativeAgain"
    }
}
