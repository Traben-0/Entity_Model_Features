package traben.entity_model_features;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class EntityVariantProperty extends StringArrayOrRegexProperty {

    private final boolean doPrint;

    protected EntityVariantProperty(String string) throws RandomPropertyException {
        super(string.replace("print:", ""));
        doPrint = string.startsWith("print:");
    }

    public static EntityVariantProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new EntityVariantProperty(readPropertiesOrThrow(properties, propertyNum, "variant", "variants"));
        } catch (RandomProperty.RandomPropertyException var3) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }

    @Override
    protected @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        String value = getValueFromEntityInternal(etfEntity);
        if (doPrint) {
            EMFUtils.log("[variant property print] = " + (value == null ? "//VARIANT CHECK FAILED AND WILL RETURN FALSE//" : value));
        }
        return value;
    }

    private @Nullable String getValueFromEntityInternal(ETFEntity etfEntity) {
        if (etfEntity instanceof Entity) {
            //if (etfEntity instanceof VariantHolder<?> variableEntity) {
//                if (variableEntity.getVariant() instanceof StringIdentifiable stringIdentifiable) {
//                    return stringIdentifiable.asString();
//                }

                if (etfEntity instanceof CatEntity cat) {
                    return Registry.CAT_VARIANT.getKey(
                            cat.getVariant()).map(catVariantRegistryKey -> catVariantRegistryKey.getValue().getPath()
                    ).orElse(null);
                }
                if (etfEntity instanceof FrogEntity frog) {
                    return Registry.FROG_VARIANT.getKey(
                            frog.getVariant()).map(frogVariantRegistryKey -> frogVariantRegistryKey.getValue().getPath()
                    ).orElse(null);
                }
                //e.g. painting entity
                if (etfEntity instanceof PaintingEntity painting) {
                    return painting.getVariant().getKey().isPresent() ? painting.getVariant().getKey().get().getValue().getPath() : null;
                }

                if (etfEntity instanceof VillagerDataContainer villager) {
                    return villager.getVillagerData().getType().toString();
                }
               // return variableEntity.getVariant().toString();
            //}
            return Registry.ENTITY_TYPE.getKey(((Entity) etfEntity).getType()).map(key -> key.getValue().getPath()).orElse(null);

        } else if (etfEntity instanceof BlockEntity) {
            if (etfEntity instanceof SignBlockEntity signBlockEntity
                    && signBlockEntity.getCachedState().getBlock() instanceof AbstractSignBlock abstractSignBlock) {
                return abstractSignBlock.getSignType().getName();
            }
            //todo move colors to color property in etf maybe?
            //it is actually useless in etf though, as they can already derive colour
            if (etfEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity
                    && shulkerBoxBlockEntity.getCachedState().getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock) {
                return String.valueOf(shulkerBoxBlock.getColor());
            }
            if (etfEntity instanceof BedBlockEntity bedBlockEntity
                    && bedBlockEntity.getCachedState().getBlock() instanceof BedBlock bedBlock) {
                return String.valueOf(bedBlock.getColor());
            }
//            if (etfEntity instanceof DecoratedPotBlockEntity pot) {
//                DecoratedPotBlockEntity.Sherds sherds = pot.getSherds();
//                return sherds.back().getTranslationKey() + "," +
//                        sherds.left().getTranslationKey() + "," +
//                        sherds.right().getTranslationKey() + "," +
//                        sherds.front().getTranslationKey();
//            }

            return Registry.BLOCK_ENTITY_TYPE.getKey(((BlockEntity) etfEntity).getType()).map(key -> key.getValue().getPath()).orElse(null);
        }
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return false;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"variant", "variants"};
    }
}
