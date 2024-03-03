package traben.entity_model_features.models.animation.math.methods.emf;

import net.minecraft.util.math.MathHelper;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.ArrayList;
import java.util.List;

public class KeyframeMethod extends MathMethod {


    public KeyframeMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var parsedArgs = parseAllArgs(args, calculationInstance);

        MathComponent delta = parsedArgs.get(0);
        List<MathComponent> frames = new ArrayList<>(parsedArgs);
        frames.remove(0);

        int max = frames.size() - 1;

        ResultSupplier supplier = () -> {
            float deltaVal = delta.getResult();
            int frameIndex = (int) deltaVal;
            //no looping
            if (frameIndex >= max) {
                return frames.get(max).getResult();
            } else if (frameIndex < 0) {
                return frames.get(0).getResult();
            }
            //unknown mathematical complexity in each frame so optimize if possible
            float frameDelta = deltaVal - frameIndex;
            var lastFrame = frames.get(frameIndex);
            var nextFrame = frames.get(frameIndex + 1);

            return MathHelper.lerp(frameDelta, lastFrame.getResult(), nextFrame.getResult());
        };

        if (delta.isConstant()) {
            float deltaVal = delta.getResult();
            int frameIndex = (int) deltaVal;
            //no looping
            if (frameIndex >= max) {
                setOptimizedAlternativeToThis(() -> frames.get(max).getResult());
            } else if (frameIndex < 0) {
                setOptimizedAlternativeToThis(() -> frames.get(0).getResult());
            }
            //unknown mathematical complexity in each frame so optimize if possible
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
