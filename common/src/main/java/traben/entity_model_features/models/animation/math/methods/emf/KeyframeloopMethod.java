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
        List<MathComponent> frames = new ArrayList<>(parsedArgs.subList(1, parsedArgs.size()));

        MathComponent[] frameArray = frames.toArray(new MathComponent[0]);
        int frameCount = frameArray.length;

        ResultSupplier supplier = () -> {
            float deltaRaw = delta.getResult();
            int deltaFloor = Mth.floor(deltaRaw);

            MathComponent baseFrame = frameArray[(deltaFloor % frameCount + frameCount) % frameCount];
            MathComponent beforeFrame = frameArray[((deltaFloor - 1) % frameCount + frameCount) % frameCount];
            MathComponent nextFrame = frameArray[((deltaFloor + 1) % frameCount + frameCount) % frameCount];
            MathComponent afterFrame = frameArray[((deltaFloor + 2) % frameCount + frameCount) % frameCount];

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

            MathComponent baseFrame = frameArray[(deltaFloor % frameCount + frameCount) % frameCount];
            MathComponent beforeFrame = frameArray[((deltaFloor - 1) % frameCount + frameCount) % frameCount];
            MathComponent nextFrame = frameArray[((deltaFloor + 1) % frameCount + frameCount) % frameCount];
            MathComponent afterFrame = frameArray[((deltaFloor + 2) % frameCount + frameCount) % frameCount];

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
