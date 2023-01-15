package traben.entity_model_features.models.features;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.EMF_EntityModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class EMFArmorFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    private final EMF_EntityModel<?> innerModel;
    private final EMF_EntityModel<?> outerModel;

    public EMFArmorFeatureRenderer(FeatureRendererContext<T, M> context, EMF_EntityModel<?> innerModel, EMF_EntityModel<?> outerModel) {
        super(context);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, this.getModel(EquipmentSlot.CHEST));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, this.getModel(EquipmentSlot.LEGS));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, this.getModel(EquipmentSlot.FEET));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, this.getModel(EquipmentSlot.HEAD));
    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, EMF_EntityModel<?> model) {
        ItemStack itemStack = entity.getEquippedStack(armorSlot);
        if (itemStack.getItem() instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)itemStack.getItem();
            if (armorItem.getSlotType() == armorSlot) {
                ((EMFCustomModel<?>)this.getContextModel()).getThisEMFModel().copyStateToEMF(model);
                this.setVisible(model, armorSlot);
                boolean bl = this.usesInnerModel(armorSlot);
                boolean bl2 = itemStack.hasGlint();
                if (armorItem instanceof DyeableArmorItem) {
                    int i = ((DyeableArmorItem)armorItem).getColor(itemStack);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float g = (float)(i >> 8 & 255) / 255.0F;
                    float h = (float)(i & 255) / 255.0F;
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, f, g, h, (String)null);
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, "overlay");
                } else {
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, (String)null);
                }

            }
        }
    }

    protected void setVisible(EMF_EntityModel<?> model, EquipmentSlot slot) {
        model.setVisibleToplvl(false);
        Set<String> visibles = new HashSet<>();
        switch (slot) {
            case HEAD -> {
                visibles.add("head");
                visibles.add("headwear");
                visibles.add("hat");
            }
            case CHEST -> {
                visibles.add("body");
                visibles.add("right_arm");
                visibles.add("left_arm");
            }
            case LEGS -> {
                visibles.add("body");
                visibles.add("right_leg");
                visibles.add("left_leg");
            }
            case FEET -> {
                visibles.add("right_leg");
                visibles.add("left_leg");
            }
        }
        model.setVisibleToplvl(visibles,true);
    }

    private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean glint, EMF_EntityModel<?> model, boolean secondTextureLayer, float red, float green, float blue, @Nullable String overlay) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, secondTextureLayer, overlay)), false, glint);
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }

    private EMF_EntityModel<?> getModel(EquipmentSlot slot) {
        return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    private Identifier getArmorTexture(ArmorItem item, boolean secondLayer, @Nullable String overlay) {
        String var10000 = item.getMaterial().getName();
        String string = "textures/models/armor/" + var10000 + "_layer_" + (secondLayer ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
        return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
    }
}
