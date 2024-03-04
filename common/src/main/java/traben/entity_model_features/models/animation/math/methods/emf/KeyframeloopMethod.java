package traben.entity_model_features.models.animation.math.methods.emf;

import net.minecraft.util.math.MathHelper;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.ArrayList;
import java.util.List;

public class KeyframeloopMethod extends MathMethod {


    public KeyframeloopMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var parsedArgs = parseAllArgs(args, calculationInstance);

        MathComponent delta = parsedArgs.get(0);
        List<MathComponent> frames = new ArrayList<>(parsedArgs);
        frames.remove(0);
        frames.add(frames.get(1));//add the first frame to the end to make it loop

        int max = frames.size() - 1;

        ResultSupplier supplier = () -> {
            float deltaVal1 = delta.getResult() % max;
            float deltaVal = deltaVal1 < 0 ? deltaVal1 + max : deltaVal1;

            int frameIndex = (int) deltaVal;

            float frameDelta = deltaVal - frameIndex;
            var lastFrame = frames.get(frameIndex);
            var nextFrame = frames.get(frameIndex + 1);

            return MathHelper.lerp(frameDelta, lastFrame.getResult(), nextFrame.getResult());
        };

        if (delta.isConstant()) {
            float deltaVal1 = delta.getResult() % max;
            float deltaVal = deltaVal1 < 0 ? deltaVal1 + max : deltaVal1;

            int frameIndex = (int) deltaVal;
            float frameDelta = deltaVal - frameIndex;
            var lastFrame = frames.get(frameIndex);
            var nextFrame = frames.get(frameIndex + 1);

            setOptimizedAlternativeToThis(() -> MathHelper.lerp(frameDelta, lastFrame.getResult(), nextFrame.getResult()));
        }

        setSupplierAndOptimize(supplier, parsedArgs);
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 3;
    }

}
