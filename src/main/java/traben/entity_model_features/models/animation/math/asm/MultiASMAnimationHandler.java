package traben.entity_model_features.models.animation.math.asm;

import net.minecraft.client.model.geom.ModelPart;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.utils.EMFLODHandler;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;

public class MultiASMAnimationHandler extends EMFAnimationHandler {

    private final boolean logsASM = EMF.config().getConfig().logASM;
    private final boolean lod = EMF.config().getConfig().animationLODDistance != 0;
    private final ASMAnimationHandler[] delegates;

    public MultiASMAnimationHandler(String modelName, List<AnimLineData> animLineDataList, ASMAnimationHandler... delegates) {
        super(modelName, animLineDataList);
        this.delegates = delegates;
    }

    @Override
    protected void animateInner(ModelPart[] pausedParts) throws Throwable {

        var state = EMFState.state();

        if (lod && EMFLODHandler.isLODSkippingThisFrame(modelName)) {
            if (state != null) {
                boolean canLODPause = true;
                for (ASMAnimationHandler delegate : delegates) {
                    if (!delegate.canLODPause(state)) {
                        canLODPause = false;
                        break;
                    }
                }
                if (canLODPause) {
                    for (ASMAnimationHandler delegate : delegates) {
                        delegate.animateInnerLOD(state);
                    }
                    return;
                }
            }
        }

        if (logsASM) {
            EMFUtils.log(this + "\n - Animating " + delegates.length + " delegates");
        }

        for (ASMAnimationHandler delegate : delegates) {
            if (logsASM) {
                EMFUtils.log(" - Animating delegate: " + delegate.delegateIndex);
            }
            delegate.animateInnerNoLOD(state);
        }

    }

    @Override
    public boolean finishAndValidate() {
        boolean allValid = true;
        if (logsASM) {
            EMFUtils.log(this + "\n - Validating " + delegates.length + " delegates");
        }
        for (ASMAnimationHandler delegate : delegates) {
            if (logsASM) {
                EMFUtils.log(" - Validating delegate: " + delegate.delegateIndex);
            }
            allValid &= delegate.finishAndValidate();
        }
        return allValid;
    }
}
