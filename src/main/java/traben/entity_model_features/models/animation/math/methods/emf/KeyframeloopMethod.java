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

import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.FASTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.T_FLOAT;

public class KeyframeloopMethod extends MathMethod {


    public KeyframeloopMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);


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
            setOptimizedAlternativeToThis(new MathConstant(Mth.catmullrom(individualFrameDelta,
                    beforeFrame.getResult(),
                    baseFrame.getResult(),
                    nextFrame.getResult(),
                    afterFrame.getResult())));
        }

        setSupplierAndOptimize(supplier, parsedArgs);
    }

    @SuppressWarnings("unused")
    public static float keyFrameLoopStatic(float deltaRaw, float... frameArray) {
        int deltaFloor = Mth.floor(deltaRaw);
        int frameCount = frameArray.length;

        float baseFrame = frameArray[(deltaFloor % frameCount + frameCount) % frameCount];
        float beforeFrame = frameArray[((deltaFloor - 1) % frameCount + frameCount) % frameCount];
        float nextFrame = frameArray[((deltaFloor + 1) % frameCount + frameCount) % frameCount];
        float afterFrame = frameArray[((deltaFloor + 2) % frameCount + frameCount) % frameCount];

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
                "traben/entity_model_features/models/animation/math/methods/emf/KeyframeLoopMethod",
                "keyFrameLoopStatic",
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
