package traben.entity_model_features.models.animation;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.parts.EMFModelPart;

import java.util.List;
import java.util.Objects;

public abstract class EMFAnimationHandler {

    private final List<AnimLineData> animLineDataList;
    public final String modelName;

    public EMFAnimationHandler(String modelName, List<AnimLineData> animLineDataList) {
        this.modelName = modelName;
        this.animLineDataList = animLineDataList;
    }

    public void addAnimLineData(AnimLineData animLineData) {
        animLineDataList.add(animLineData);
        animLineData.lineIndex = animLineDataList.size() - 1;
    }

    public List<AnimLineData> lines() {
        return animLineDataList;
    }

    public AnimLineData getLine(String animKey) {
        for (AnimLineData animLineData : animLineDataList) {
            if (animLineData.animKey.equals(animKey)) return animLineData;
        }
        return null;
    }

    private boolean blockFutureAnimation = false;

    public final void animate(ModelPart[] pausedParts) throws Throwable {
        if (blockFutureAnimation) return;
        try {
            animateInner(pausedParts);
        } catch (Throwable e) {
            blockFutureAnimation = true;
            throw e;
        }
    }
    protected abstract void animateInner(ModelPart[] pausedParts) throws Throwable;

    public abstract boolean finishAndValidate();

    @Override
    public String toString() {
        return "EMFAnimationHandler for " + modelName + ", type is " + getClass().getName();
    }

    public static final class AnimLineData {
        public final String animKey;
        public final String expression;
        public final @Nullable EMFModelPart partToApplyTo;
        public final @Nullable EMFModelOrRenderVariable applier;
        public final boolean isBoolean;
        public final boolean isVar;
        public final boolean isVarGlobal;

        public int asmIndex = -1;

        private int lineIndex = -1;

        public AnimLineData(
                String animKey,
                String expression,
                @Nullable EMFModelPart partToApplyTo,
                @Nullable EMFModelOrRenderVariable applier
        ) {
            this.animKey = animKey;
            this.expression = expression;
            this.partToApplyTo = partToApplyTo;
            this.applier = applier;

            boolean animKeyIsBoolean = animKey.startsWith("varb.") || animKey.startsWith("global_varb.");
            isBoolean = animKeyIsBoolean || (applier != null && applier.isBoolean());
            isVar = animKeyIsBoolean || animKey.startsWith("var.") || animKey.startsWith("global_var.");
            isVarGlobal = isVar && animKey.startsWith("global_var");
        }

        @Override
        public int hashCode() {
            return Objects.hash(lineIndex);
        }

        @Override
        public String toString() {
            return "AnimLineData{" +
                    "animKey='" + animKey + '\'' +
                    ", expression='" + expression + '\'' +
                    ", partToApplyTo?=" + (partToApplyTo != null) +
                    ", applier?=" + (applier != null) +
                    ", isBoolean=" + isBoolean +
                    ", isVar=" + isVar +
                    ", isVarGlobal=" + isVarGlobal +
                    ", asmIndex=" + asmIndex +
                    '}';
        }
    }
}
