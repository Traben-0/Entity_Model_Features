package traben.entity_model_features.models.animation.math.methods.emf;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.*;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NBTProperty;

import java.util.List;
import java.util.Properties;

public class NBTMethod extends MathMethod {


    public NBTMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

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
                    return MathValue.fromBoolean(propertyTester.testEntity(EMFAnimationEntityContext.getEMFEntity(), false));
                }
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

}
