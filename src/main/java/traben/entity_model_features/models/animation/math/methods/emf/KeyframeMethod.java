package traben.entity_model_features.models.animation.math.methods.emf;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathConstant;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

import static org.objectweb.asm.Opcodes.*;

public class KeyframeMethod extends MathMethod {


    public KeyframeMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);


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
                setOptimizedAlternativeToThis(new MathConstant(Mth.catmullrom(individualFrameDelta,
                        beforeFrame.getResult(),
                        baseFrame.getResult(),
                        nextFrame.getResult(),
                        afterFrame.getResult())));
            }
        }

        setSupplierAndOptimize(supplier, parsedArgs);
    }

    @SuppressWarnings("unused")
    public static float keyFrameStatic(float deltaRaw, float... frameArray) {
        int deltaFloor = Mth.floor(deltaRaw);
        int frameEnd = frameArray.length - 1;

        if(deltaFloor >= frameEnd){
            return frameArray[frameEnd];
        }else if(deltaFloor <= 0){
            return frameArray[0];
        }

        float baseFrame =   frameArray[Mth.clamp(deltaFloor,       0, frameEnd)];
        float beforeFrame = frameArray[Mth.clamp((deltaFloor - 1), 0, frameEnd)];
        float nextFrame =   frameArray[Mth.clamp((deltaFloor + 1), 0, frameEnd)];
        float afterFrame =  frameArray[Mth.clamp((deltaFloor + 2), 0, frameEnd)];

        float individualFrameDelta = Mth.frac(deltaRaw);

        return Mth.catmullrom(individualFrameDelta,
                beforeFrame,
                baseFrame,
                nextFrame,
                afterFrame);
    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        vars.scopeFloat();

        int size = parsedArgs.size();

        // ---- push deltaRaw first ----
        parsedArgs.get(0).asmVisit(mv, vars); // -> float

        // ---- create float[size] ----
        mv.visitLdcInsn(size);
        mv.visitIntInsn(NEWARRAY, T_FLOAT);
        // stack: float[]

        // ---- fill array ----
        for (int i = 1; i < size; i++) {

            mv.visitInsn(DUP); // arrayref
            mv.visitLdcInsn(i); // index

            // push value
            parsedArgs.get(i).asmVisit(mv, vars); // float

            mv.visitInsn(FASTORE);
        }

        // ---- call method ----
        mv.visitMethodInsn(
                INVOKESTATIC,
                "traben/entity_model_features/models/animation/math/methods/emf/KeyframeMethod",
                "keyFrameStatic",
                "(F[F)F",
                false
        );
        vars.scopePop();
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 3;
    }

}
