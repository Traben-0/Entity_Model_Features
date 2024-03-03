package traben.entity_model_features.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class EMFConfigScreenMain extends ETFConfigScreen {


    private final Random rand = new Random();
    public EMFConfig tempConfig;
    private long timer = 0;
    private LivingEntity livingEntity = null;

    public EMFConfigScreenMain(Screen parent) {
        super(Text.translatable("entity_model_features.title"), parent);
        // this.parent = parent;
        tempConfig = EMFConfig.copyFrom(EMFConfig.getConfig());
    }

    @Override
    protected void init() {
        super.init();


        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.done"),
                (button) -> {
                    if (!tempConfig.equals(EMFConfig.getConfig())) {
                        EMFConfig.setConfig(tempConfig);
                        EMFConfig.EMF_saveConfig();
                        MinecraftClient.getInstance().reloadResources();
                    }
                    Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    tempConfig = new EMFConfig();
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.CANCEL,
                (button) -> {
                    tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());


        this.addDrawableChild(getETFButton((int) (this.width * 0.6), (int) (this.height * 0.2), (int) (this.width * 0.3), 20,
                Text.translatable("entity_model_features.config.options"),
                (button) -> Objects.requireNonNull(client).setScreen(new EMFConfigScreenOptions(this)),
                Text.translatable("entity_model_features.config.options.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.6), (int) (this.height * 0.3), (int) (this.width * 0.3), 20,
                Text.translatable("entity_model_features.config.tools"),
                (button) -> Objects.requireNonNull(client).setScreen(new EMFConfigScreenTools(this)),
                Text.translatable("entity_model_features.config.tools.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.6), (int) (this.height * 0.4), (int) (this.width * 0.3), 20,
                Text.translatable("entity_model_features.config.debug"),
                (button) -> Objects.requireNonNull(client).setScreen(new EMFConfigScreenDebugLogOptions(this)),
                Text.translatable("entity_model_features.config.debug.tooltip")
        ));

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (timer + 5000 < System.currentTimeMillis() && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getWorld() != null) {
            List<Entity> entityList = MinecraftClient.getInstance().player.getWorld().getOtherEntities(null, MinecraftClient.getInstance().player.getBoundingBox().expand(128));
            Entity entity = null;
            for (int i = 0; i < Math.min(entityList.size(), 24); i++) {
                entity = entityList.get(rand.nextInt(entityList.size()));
                if (entity instanceof LivingEntity) break;
            }
            if (entity instanceof LivingEntity) {
                livingEntity = (LivingEntity) entity;
                timer = System.currentTimeMillis();
            }
        }
        if (livingEntity != null && !livingEntity.isRemoved()) {
            int y = (int) (this.height * 0.75);
            if (livingEntity.getHeight() < 0.7) y -= (int) (this.height * 0.15);
            int x = (int) (this.width * 0.33);
            //float f = (float)Math.atan((double)(-mouseX / 40.0F));
            float g = (float) Math.atan(((-mouseY + this.height / 2f) / 40.0F));
            Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F).rotateY((float) (System.currentTimeMillis() / 1000d % (2 * Math.PI)));
            Quaternionf quaternionf2 = (new Quaternionf()).rotateX(-(g * 20.0F * 0.017453292F));
            quaternionf.mul(quaternionf2);
            double scale;
            double autoScale = (this.height * 0.4) / ((Math.max(1, Math.max(livingEntity.getHeight(), livingEntity.getWidth()))));
            if (livingEntity instanceof SquidEntity) {
                y -= (int) (this.height * 0.15);
                scale = autoScale * 0.5;
            } else if (livingEntity instanceof GuardianEntity || livingEntity instanceof SnifferEntity) {
                y -= (int) (this.height * 0.1);
                scale = autoScale * 0.7;
            } else if (livingEntity instanceof EnderDragonEntity) {
                y -= (int) (this.height * 0.15);
                scale = autoScale * 1.5;
            } else {
                scale = autoScale;
            }

            double scaleModify = Math.sin((System.currentTimeMillis() - timer) / 5000d * Math.PI) * 6;
            double scaleModify2 = Math.max(Math.min(Math.abs(scaleModify), 1d), 0);//clamp
            int modelHeight = (int) Math.min(scale * scaleModify2, (this.height * 0.4));

            context.getMatrices().push();
            //context.getMatrices().translate(0, 0, 100);
            InventoryScreen.drawEntity(context, x, y, modelHeight, new Vector3f(0, 0, 10), quaternionf, quaternionf2, livingEntity);
            context.getMatrices().pop();
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.of("Load a world and nearby entities will appear here."), this.width / 3, this.height / 2, Color.GRAY.getRGB());
        }
    }

    @Override
    public void renderInGameBackground(DrawContext context) {
    }

    @Override
    public void renderBackgroundTexture(DrawContext context) {
        super.renderBackgroundTexture(context);
    }
}
