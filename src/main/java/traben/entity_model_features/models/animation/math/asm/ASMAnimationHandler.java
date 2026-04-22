package traben.entity_model_features.models.animation.math.asm;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.utils.ETFLruCache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static traben.entity_model_features.models.animation.math.expression_tree.MathValue.FALSE;
import static traben.entity_model_features.models.animation.math.expression_tree.MathValue.TRUE;


public class ASMAnimationHandler extends EMFAnimationHandler {

    final ASMParser.ASMExecutor compiledAnimationExecutor;
    final ASMVariableHandler asmVariableHandler;
    final boolean logsASM = EMF.config().getConfig().logASM;
    Supplier<ASMVariableHandler.AnimVars> varSupplier = null;
    VarConsumer varConsumer = null;

    private interface VarConsumer {
        void accept(ASMVariableHandler.AnimVars vars, boolean saveToVars);
    }

    final boolean lod = EMF.config().getConfig().animationLODDistance != 0;
    public final ETFLruCache<UUID, ASMVariableHandler.AnimVars> lastResultsPerEntity = lod ? new ETFLruCache<>() : null;

    public ASMAnimationHandler(ASMParser.ASMExecutor compiledAnimationExecutor, ASMVariableHandler asmVariableHandler, AnimSetupContext animSetupContext) {
        super(animSetupContext.oldAnimationHandler.modelName, animSetupContext.oldAnimationHandler.lines());
        this.compiledAnimationExecutor = compiledAnimationExecutor;
        this.asmVariableHandler = asmVariableHandler;
        this.varSupplier = buildVarSupplier(asmVariableHandler, animSetupContext);
    }


    @Override
    protected void animateInner(ModelPart[] pausedParts) throws Throwable {
        var state = EMFAnimationEntityContext.getEmfState();
        if (lod && EMFAnimationEntityContext.isLODSkippingThisFrame(modelName)) {
            if (state != null) {
                ASMVariableHandler.AnimVars vars = lastResultsPerEntity.get(state.uuid());
                if (vars != null) {
                    varConsumer.accept(vars, false);
                    return;
                }
            }
        }

        ASMVariableHandler.AnimVars vars = varSupplier.get();
        if (logsASM) asmLog(asmVariableHandler, vars, "Start ASM anim with variable state:");

        compiledAnimationExecutor.execute(vars.floats(), vars.bools());

        varConsumer.accept(vars, true);
        if (lod && state != null) lastResultsPerEntity.put(state.uuid(), vars);

        if (logsASM) asmLog(asmVariableHandler, vars, "End ASM anim with variable state:");

    }

    private interface FloatConsumerAsm {
        void accept(float[] floats, boolean doVars);
    }
    private interface BoolConsumerAsm {
        void accept(boolean[] floats, boolean doVars);
    }

    @Override
    public boolean finishAndValidate() {
        List<FloatConsumerAsm> floats = buildFloatConsumers();
        List<BoolConsumerAsm> bools = buildBoolConsumers();

        if (bools.isEmpty() && floats.isEmpty()) {
            EMFUtils.logError("ASMAnimationHandler failed ASM variable setting validation for " + modelName);
            return false;
        }

        varConsumer = (animVars, doVars) -> {
            floats.forEach((f) -> f.accept(animVars.floats(), doVars));
            bools.forEach((f) -> f.accept(animVars.bools(), doVars));
        };

        return true;
    }

    private @NotNull List<FloatConsumerAsm> buildFloatConsumers() {
        List<FloatConsumerAsm> floats = new ArrayList<>();
        for (String varName : asmVariableHandler.getFloatVarList()) {
            if (!asmVariableHandler.isWriteVarName(varName))
                continue;
            var line = getLine(varName);
            int index = line.asmIndex;
            if (index == -1) continue;
            String key = line.animKey;
            FloatConsumerAsm consumer = null;
            if (line.isVar) {
                //TODO remove reliance on old variable system for setting
                if (line.isVarGlobal) {
                    consumer = (array, doVar) -> { if (doVar) GlobalVariableFactory.setGlobalVariable(key, array[index]);};
                } else {
                    consumer = (array, doVar) -> { if (doVar) EMFAnimationEntityContext.setEntityVariable(key, array[index]);};
                }
            } else if (line.applier != null) {
                var finApply = line.applier;
                @Nullable var finPart = line.partToApplyTo;
                consumer = (array, doVar) -> finApply.setValue(finPart, array[index]);
            }
            if (consumer != null) floats.add(consumer);
            else System.out.println("line didnt build consumer: " + line);

        }
        return floats;
    }

    private @NotNull List<BoolConsumerAsm> buildBoolConsumers() {
        List<BoolConsumerAsm> bools = new ArrayList<>();
        for (String varName : asmVariableHandler.getBoolVarList()) {
            if (!asmVariableHandler.isWriteVarName(varName))
                continue;
            var line = getLine(varName);
            int index = line.asmIndex;
            if (index == -1) continue;
            String key = line.animKey;
            BoolConsumerAsm consumer = null;
            if (line.isVar) {
                //TODO remove reliance on old variable system for setting
                if (line.isVarGlobal) {
                    consumer = (array, doVar) -> { if (doVar) GlobalVariableFactory.setGlobalVariable(key, array[index] ? TRUE : FALSE);};
                } else {
                    consumer = (array, doVar) -> { if (doVar) EMFAnimationEntityContext.setEntityVariable(key, array[index] ? TRUE : FALSE);};
                }
            } else if (line.applier != null) {
                var finApply = line.applier;
                @Nullable var finPart = line.partToApplyTo;
                consumer = (array, doVar) -> finApply.setValue(finPart, array[index] ? TRUE : FALSE);
            }
            if (consumer != null) bools.add(consumer);
            else System.out.println("line didnt build consumer: " + line);

        }
        return bools;
    }

    private  Supplier<ASMVariableHandler.AnimVars> buildVarSupplier(
            ASMVariableHandler asmVariableHandler,
            AnimSetupContext context
    ) {
        context.animKey = "prepopulateVarStates()";

        //TODO remove reliance on old variable system for getting
        List<@Nullable MathComponent> floatVars = new ArrayList<>();
        for (String varName : asmVariableHandler.getFloatVarList()) {
            if (!asmVariableHandler.isReadVarName(varName)) {
                floatVars.add(null);
            } else {
                floatVars.add(VariableRegistry.getInstance().getVariable(varName, false, context));
            }
        }

        List<@Nullable MathComponent> boolVars = new ArrayList<>();
        for (String varName : asmVariableHandler.getBoolVarList()) {
            if (!asmVariableHandler.isReadVarName(varName)) {
                boolVars.add(null);
            } else {
                boolVars.add(VariableRegistry.getInstance().getVariable(varName, false, context));
            }
        }


        final int fSize = floatVars.size();
        final int bSize = boolVars.size();
        return ()-> {
            float[] fArr = new float[fSize];
            for (int i = 0; i < fSize; i++) {
                MathComponent v = floatVars.get(i);
                if (v != null) {
                    float res = v.getResult();
                    fArr[i] = Float.isInfinite(res) ? (res > 0 ? 1 : 0) : res;
                }
            }

            boolean[] bArr = new boolean[bSize];
            for (int i = 0; i < bSize; i++) {
                MathComponent v = boolVars.get(i);
                if (v != null) {
                    float res = v.getResult();
                    bArr[i] = MathValue.toBoolean(res);
                }
            }

            return new ASMVariableHandler.AnimVars(fArr, bArr);
        };

    }

    private static void asmLog(ASMVariableHandler asmVariableHandler, ASMVariableHandler.AnimVars vars, String prefix) {
        var str = new StringBuilder(prefix);
        str.append("\nFloats:");
        for (int i = 0; i < vars.floats().length; i++) {
            str.append("\n - [")
                    .append(asmVariableHandler.getFloatVarList().get(i))
                    .append("] = ")
                    .append(vars.floats()[i]);
        }
        str.append("\nBooleans:");
        for (int i = 0; i < vars.bools().length; i++) {
            str.append("\n - [")
                    .append(asmVariableHandler.getBoolVarList().get(i))
                    .append("] = ")
                    .append(vars.bools()[i]);
        }
        EMFUtils.log(str.toString());
    }
}
