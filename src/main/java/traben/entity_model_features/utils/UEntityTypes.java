package traben.entity_model_features.utils;

//#if MC >= 26.2
//$$ import net.minecraft.world.entity.EntityTypes;
//$$ import net.minecraft.world.level.block.entity.BlockEntityTypes;
//#endif

//#if MC < 12002
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//#endif

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
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

    public static String getSpecifiedModel(BlockEntityType<?> type){
        // TODO DONT FORGET TO REPLICATE CHANGES IN SPECIAL RENDERERS
        if (UEntityTypes.ENCHANTING_TABLE.equals(type))
            return "enchanting_book";
        else if (UEntityTypes.LECTERN.equals(type))
            return "lectern_book";
        else if (UEntityTypes.CHEST.equals(type))
            return "chest";
        else if (UEntityTypes.ENDER_CHEST.equals(type))
            return "ender_chest";
        else if (UEntityTypes.TRAPPED_CHEST.equals(type))
            return "trapped_chest";
        //#if MC >= 12002
        //todo did deprecation start in 1.21.2?
        else if (type.builtInRegistryHolder() != null && type.builtInRegistryHolder().unwrapKey().isPresent()) {
            ResourceLocation id = type.builtInRegistryHolder().unwrapKey().get().location();
        //#else
        //$$ else if (BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(type).isPresent()) {
        //$$     ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(type).get().location();
        //#endif
            if (id.getNamespace().equals("minecraft")) {
                return id.getPath();
            } else {
                return id.getNamespace() + ":" + id.getPath();
            }
        }
        return "";
    }

    public static String getSpecifiedModel(EntityType<?> type){
        //#if MC >= 1.21.2
        //#if MC == 1.21.3
        //$$ if (type.equals(EntityType.CREAKING_TRANSIENT)) {
        //$$     return "creaking_transient";
        //$$ } else
        //#endif
        if(type.equals(UEntityTypes.SPECTRAL_ARROW)) {
            return "spectral_arrow";
        }else if (type.equals(UEntityTypes.BREEZE_WIND_CHARGE)) {
            return "breeze_wind_charge";
        }else if (type
                //#if MC >= 26.1
                //$$ .builtInRegistryHolder()
                //#endif
                .is(EntityTypeTags.BOAT)) {
            //entity.minecraft.dark_oak_boat
            return "emf$boat$" //key to not override
                    + type.getDescriptionId()
                    .replaceAll("entity.minecraft.","")
                    .replaceAll("(_boat|_raft|_chest_boat)$","");
        }
        //#if MC >= 1.21.11
        //$$  else if (type.equals(UEntityTypes.CAMEL_HUSK)) {
        //$$           return "camel_husk";
        //$$       }
        //#endif
        //#endif
        return "";
    }
}
