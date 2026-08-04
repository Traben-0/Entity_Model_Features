package traben.entity_model_features.utils;

//#if MC >= 26.2
//$$ import net.minecraft.world.entity.EntityTypes;
//$$ import net.minecraft.world.level.block.entity.BlockEntityTypes;
//#endif

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class UEntityTypes {
    //#if MC >= 26.2
    //$$ public static BlockEntityType<?> CHEST = BlockEntityTypes.CHEST;
    //$$ public static BlockEntityType<?> TRAPPED_CHEST = BlockEntityTypes.TRAPPED_CHEST;
    //$$ public static BlockEntityType<?> ENDER_CHEST = BlockEntityTypes.ENDER_CHEST;
    //$$ public static BlockEntityType<?> SHULKER_BOX = BlockEntityTypes.SHULKER_BOX;
    //$$ public static BlockEntityType<?> BELL = BlockEntityTypes.BELL;
    //$$ public static BlockEntityType<?> SIGN = BlockEntityTypes.SIGN;
    //$$ public static BlockEntityType<?> DECORATED_POT = BlockEntityTypes.DECORATED_POT;
    //$$ public static BlockEntityType<?> ENCHANTING_TABLE = BlockEntityTypes.ENCHANTING_TABLE;
    //$$ public static BlockEntityType<?> LECTERN = BlockEntityTypes.LECTERN;
    //$$
    //$$ public static EntityType<?> SPECTRAL_ARROW = EntityTypes.SPECTRAL_ARROW;
    //$$ public static EntityType<?> BREEZE_WIND_CHARGE = EntityTypes.BREEZE_WIND_CHARGE;
    //$$ public static EntityType<?> VILLAGER = EntityTypes.VILLAGER;
    //$$ public static EntityType<?> HORSE = EntityTypes.HORSE;
    //$$ public static EntityType<?> BLAZE = EntityTypes.BLAZE;
    //$$ public static EntityType<?> CAMEL_HUSK = EntityTypes.CAMEL_HUSK;
    //#else
    public static BlockEntityType<?> BED = BlockEntityType.BED;
    public static BlockEntityType<?> CHEST = BlockEntityType.CHEST;
    public static BlockEntityType<?> TRAPPED_CHEST = BlockEntityType.TRAPPED_CHEST;
    public static BlockEntityType<?> ENDER_CHEST = BlockEntityType.ENDER_CHEST;
    public static BlockEntityType<?> SHULKER_BOX = BlockEntityType.SHULKER_BOX;
    public static BlockEntityType<?> BELL = BlockEntityType.BELL;
    public static BlockEntityType<?> SIGN = BlockEntityType.SIGN;
    public static BlockEntityType<?> DECORATED_POT = BlockEntityType.DECORATED_POT;
    public static BlockEntityType<?> ENCHANTING_TABLE = BlockEntityType.ENCHANTING_TABLE;
    public static BlockEntityType<?> LECTERN = BlockEntityType.LECTERN;

    public static EntityType<?> SPECTRAL_ARROW = EntityType.SPECTRAL_ARROW;
    //#if MC > 1.20.1
    public static EntityType<?> BREEZE_WIND_CHARGE = EntityType.BREEZE_WIND_CHARGE;
    //#endif
    public static EntityType<?> VILLAGER = EntityType.VILLAGER;
    public static EntityType<?> HORSE = EntityType.HORSE;
    public static EntityType<?> BLAZE = EntityType.BLAZE;
    //#if MC >= 1.21.11
    //$$ public static EntityType<?> CAMEL_HUSK = EntityType.CAMEL_HUSK;
    //#endif
    //#endif
}
