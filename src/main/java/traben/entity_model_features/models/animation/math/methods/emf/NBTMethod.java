package traben.entity_model_features.models.animation.math.methods.emf;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.*;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NBTProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NBTMethod extends MathMethod {


    public NBTMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        String nbtKey = args.get(0);
        String nbtQuery = args.get(1);

        //use ETF property reading
        Properties properties = new Properties();
        properties.setProperty("nbt.1."+nbtKey, nbtQuery);
        NBTProperty propertyTester = NBTProperty.getPropertyOrNull(properties, 1);

        if (propertyTester == null){
            EMFUtils.logError("nbt() animation function did not parse: nbt("+nbtKey+" "+nbtQuery+"), please check your syntax.");
            throw new EMFMathException("nbt() animation function did not parse: nbt("+nbtKey+" "+nbtQuery+"), please check your syntax.");
        }
        setSupplierAndOptimize(() -> {
                    if (EMFAnimationEntityContext.getEMFEntity() == null) return MathValue.FALSE;
                    return MathValue.fromBoolean(propertyTester.testEntity(EMFAnimationEntityContext.getEmfState(), false));
                }
        );
    }

    public static Map<String, NBTProperty> CACHE = new HashMap<>();

    private static NBTProperty nullz() {
        Properties properties = new Properties();
        properties.setProperty("nbt.1.null" , "null");
        return NBTProperty.getPropertyOrNull(properties, 1);
    }

    private static final NBTProperty NULL = nullz();

    @SuppressWarnings("unused")
    public static boolean nbtMethodStatic(String nbtKey, String nbtQuery) throws EMFMathException {
        // not very efficient :(

        NBTProperty propertyTester = CACHE.computeIfAbsent(nbtKey+"_"+nbtQuery, (k)->{
            Properties properties = new Properties();
            properties.setProperty("nbt.1." + nbtKey, nbtQuery);
            var v = NBTProperty.getPropertyOrNull(properties, 1);
            if (v == null){
                EMFUtils.logError("nbt() animation function did not parse: nbt("+nbtKey+" "+nbtQuery+"), please check your syntax.");
                return NULL;
            }
            return v;
        });

        if (propertyTester == NULL || EMFAnimationEntityContext.getEMFEntity() == null) return false;

        var v = propertyTester.testEntity(EMFAnimationEntityContext.getEmfState(), false);
        return v;

    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        mv.visitLdcInsn(rawArgs.get(0)); // String
        mv.visitLdcInsn(rawArgs.get(1)); // String
        ASMHelper.visitStaticFunctionASM(mv, Arrays.stream(NBTMethod.class.getMethods())
                .filter(it->it.getName().equals("nbtMethodStatic"))
                .findFirst()
                .get()
        );
    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return false;
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 2;
    }

    @Override
    protected boolean isRawStringArg(int index) {
        return true;
    }
}
