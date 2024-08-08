package traben.entity_model_features.models.animation.math.methods.emf;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;

public class KeyframeloopMethod extends MathMethod {


    public KeyframeloopMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var parsedArgs = parseAllArgs(args, calculationInstance);

        MathComponent delta = parsedArgs.get(0);
        List<MathComponent> frames = new ArrayList<>(parsedArgs);
        frames.remove(0);

        MathComponent[] frameArray = frames.toArray(new MathComponent[0]);
        int frameCount = frameArray.length;

        ResultSupplier supplier = () -> {
            float deltaRaw = delta.getResult();
            int deltaFloor = Mth.floor(deltaRaw);
//@formatter:off
            int baseFrameTime =     deltaFloor       % frameCount;
            int beforeFrameTime =   (deltaFloor - 1) % frameCount;
            int nextFrameTime =     (deltaFloor + 1) % frameCount;
            int afterFrameTime =    (deltaFloor + 2) % frameCount;

            MathComponent baseFrame =   frameArray[baseFrameTime    < 0 ? frameCount + baseFrameTime    : baseFrameTime];
            MathComponent beforeFrame = frameArray[beforeFrameTime  < 0 ? frameCount + beforeFrameTime  : beforeFrameTime];
            MathComponent nextFrame =   frameArray[nextFrameTime    < 0 ? frameCount + nextFrameTime    : nextFrameTime];
            MathComponent afterFrame =  frameArray[afterFrameTime   < 0 ? frameCount + afterFrameTime   : afterFrameTime];
//@formatter:on
            float individualFrameDelta = Mth.frac(deltaRaw);

            return Mth.catmullrom(individualFrameDelta,
                    beforeFrame.getResult(),
                    baseFrame.getResult(),
                    nextFrame.getResult(),
                    afterFrame.getResult());
        };


        if (delta.isConstant()) {
            float deltaRaw = delta.getResult();
            int deltaFloor = Mth.floor(deltaRaw);
//@formatter:off
            int baseFrameTime =     deltaFloor       % frameCount;
            int beforeFrameTime =   (deltaFloor - 1) % frameCount;
            int nextFrameTime =     (deltaFloor + 1) % frameCount;
            int afterFrameTime =    (deltaFloor + 2) % frameCount;

            MathComponent baseFrame =   frameArray[baseFrameTime    < 0 ? frameCount + baseFrameTime    : baseFrameTime];
            MathComponent beforeFrame = frameArray[beforeFrameTime  < 0 ? frameCount + beforeFrameTime  : beforeFrameTime];
            MathComponent nextFrame =   frameArray[nextFrameTime    < 0 ? frameCount + nextFrameTime    : nextFrameTime];
            MathComponent afterFrame =  frameArray[afterFrameTime   < 0 ? frameCount + afterFrameTime   : afterFrameTime];
//@formatter:on
            float individualFrameDelta = Mth.frac(deltaRaw);
            setOptimizedAlternativeToThis(() -> Mth.catmullrom(individualFrameDelta,
                    beforeFrame.getResult(),
                    baseFrame.getResult(),
                    nextFrame.getResult(),
                    afterFrame.getResult()));
        }

        setSupplierAndOptimize(supplier, parsedArgs);
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 3;
    }

}
