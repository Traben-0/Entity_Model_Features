package traben.entity_model_features.propeties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class GlobalVariableFloatProperty extends RandomProperty {

    private final List<FloatRangeFromStringArrayProperty> VARIABLES;

    protected GlobalVariableFloatProperty(Properties properties, int propertyNum) throws RandomPropertyException {
//        super(readPropertiesOrThrow(properties, propertyNum, "var"));
        String keyPrefix = "global_var." + propertyNum + ".";
        this.VARIABLES = new ArrayList<>();
        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            String key = objectObjectEntry.getKey().toString();
            Object value = objectObjectEntry.getValue();
            if (key != null && key.startsWith(keyPrefix)) {
                String instruction = ((String) value).trim();
                String variableKey = "global_var." + key.replaceAll(keyPrefix, "");
                if (!variableKey.isBlank() && !instruction.isBlank()) {
                    var tester = new InnerTester(instruction, (emfEntity) -> GlobalVariableFactory.getGlobalVariable(variableKey), variableKey);
                    this.VARIABLES.add(tester);
                }
            }
        }
        if (this.VARIABLES.isEmpty()) {
            throw new RandomPropertyException("Global Variable float failed");
        }
    }

    public static GlobalVariableFloatProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new GlobalVariableFloatProperty(properties, propertyNum);
        } catch (RandomPropertyException var3) {
            return null;
        }
    }


    @Override
    protected boolean testEntityInternal(final ETFEntityRenderState etfEntity) {
        for (FloatRangeFromStringArrayProperty variable : VARIABLES) {
            if (!variable.testEntityInternal(etfEntity)) {
                return false;
            }
        }
        return true;
    }

    public @NotNull String[] getPropertyIds() {
        return new String[]{"var"};
    }

    @Override
    protected String getPrintableRuleInfo() {
        return null;
    }

    private static class InnerTester extends FloatRangeFromStringArrayProperty {

        private final Function<EMFEntity, Float> getter;
        private final String id;

        protected InnerTester(final String string, Function<EMFEntity, Float> getter, String id) throws RandomPropertyException {
            super(string);
            this.getter = getter;
            this.id = id;
        }

        @Override
        protected @Nullable Float getRangeValueFromEntity(final ETFEntityRenderState etfEntity) {
            if (etfEntity != null && etfEntity.entity() instanceof EMFEntity IEMFEntity)
                return getter.apply(IEMFEntity);
            return null;
        }

        @Override
        public @NotNull String[] getPropertyIds() {
            return new String[]{id};
        }
    }
}
