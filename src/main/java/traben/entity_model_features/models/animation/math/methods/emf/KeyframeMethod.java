package traben.entity_model_features.models.animation.math.methods.emf;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;

public class KeyframeMethod extends MathMethod {


    public KeyframeMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var parsedArgs = parseAllArgs(args, calculationInstance);

        MathComponent delta = parsedArgs.get(0);
        List<MathComponent> frames = new ArrayList<>(parsedArgs.subList(1, parsedArgs.size()));

        MathComponent[] frameArray = frames.toArray(new MathComponent[0]);
        int frameEnd = frameArray.length - 1;

        ResultSupplier supplier = () -> {
            float deltaRaw = delta.getResult();
            int deltaFloor = Mth.floor(deltaRaw);

            if(deltaFloor >= frameEnd){
                return frameArray[frameEnd].getResult();
            }else if(deltaFloor <= 0){
                return frameArray[0].getResult();
            }

            MathComponent baseFrame =   frameArray[Mth.clamp(deltaFloor,       0, frameEnd)];
            MathComponent beforeFrame = frameArray[Mth.clamp((deltaFloor - 1), 0, frameEnd)];
            MathComponent nextFrame =   frameArray[Mth.clamp((deltaFloor + 1), 0, frameEnd)];
            MathComponent afterFrame =  frameArray[Mth.clamp((deltaFloor + 2), 0, frameEnd)];

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

            if(deltaFloor >= frameEnd){
                setOptimizedAlternativeToThis(frameArray[frameEnd]);
            }else if(deltaFloor <= 0){
                setOptimizedAlternativeToThis(frameArray[0]);
            }else {

                MathComponent baseFrame =   frameArray[Mth.clamp(deltaFloor,       0, frameEnd)];
                MathComponent beforeFrame = frameArray[Mth.clamp((deltaFloor - 1), 0, frameEnd)];
                MathComponent nextFrame =   frameArray[Mth.clamp((deltaFloor + 1), 0, frameEnd)];
                MathComponent afterFrame =  frameArray[Mth.clamp((deltaFloor + 2), 0, frameEnd)];

                float individualFrameDelta = Mth.frac(deltaRaw);
                setOptimizedAlternativeToThis(() -> Mth.catmullrom(individualFrameDelta,
                        beforeFrame.getResult(),
                        baseFrame.getResult(),
                        nextFrame.getResult(),
                        afterFrame.getResult()));
            }
        }

        setSupplierAndOptimize(supplier, parsedArgs);
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 3;
    }

}
