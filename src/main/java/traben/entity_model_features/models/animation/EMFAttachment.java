package traben.entity_model_features.models.animation;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Arrays;

public class EMFAttachment {

    public PoseStack.Pose pose = null;
    private final float x;
    private final float y;
    private final float z;
    public final Type type;

    public EMFAttachment(float x, float y, float z, Type type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public void translate(PoseStack entry) {
        type.translateForType(entry, this);
    }

    /**
     * Split into types so that I can easily implement separate changes in the future,
     * - see sulfur_cubes
     * - see OptiFine format ones possibly changing in future
     */
    public enum Type {
        // OptiFine
        RIGHT_HAND("right_handheld_item"), // Mimics OptiFine implementation
        LEFT_HAND("left_handheld_item"), // Mimics OptiFine implementation

        // EMF
        HEAD("head_item"),  // Overrides the 'root > head' position copying part of the vanilla positioning
        SULFUR_CUBE("sulfur_cube_block") { // If attached to the 'cube' part at 0,0,0 then will match it 1 for 1
            @Override
            void translateForType(PoseStack pose, EMFAttachment attachment) {
                pose.translate(0, -0.5f, 0); // Make it so pack makers can treat 0,0,0 as origin

                super.translateForType(pose, attachment);
            }
        },
        ENDERMAN("enderman_block"), // Applies an extra transform alongside the vanilla one
        VILLAGER("villager_item"),  // Overrides the 'arm' position copying part of the vanilla positioning
        WITCH("witch_item"), // Overrides the 'root > head > nose' position copying part of the vanilla positioning
        PANDA("panda_item"), // Applies an extra transform alongside the vanilla one
        DOLPHIN("dolphin_item"), // Applies an extra transform alongside the vanilla one
        FOX("fox_item") // Overrides the 'head' position copying part of the vanilla positioning
        ;

        public final String id;

        Type(String id) {
            this.id = id;
        }

        void translateForType(PoseStack pose, EMFAttachment attachment) {
            pose.translate(attachment.x / 16, attachment.y / 16, attachment.z / 16);
        }

        public boolean isHumanoidHand() {
            return this == RIGHT_HAND || this == LEFT_HAND;
        }

        public static Type of(String id) {
            for (Type t : values()) {
                if (t.id.equals(id)) {
                    return t;
                }
            }
            return null;
        }

        public static final Type[] NON_HANDS = Arrays.stream(values())
                .filter(t -> t != RIGHT_HAND && t != LEFT_HAND)
                .toArray(Type[]::new);
    }
}
