package traben.entity_model_features.models.animation.animation_math_parser;

public class MathConstant extends MathValue implements MathComponent {


    public static MathConstant ZERO = new MathConstant(0) {
        @Override
        public void makeNegative(boolean become) {
        }
    };
    public static MathConstant ONE = new MathConstant(1) {
        @Override
        public void makeNegative(boolean become) {
        }
    };
    public static MathConstant PI_CONSTANT = new MathConstant((float) Math.PI) {
        @Override
        public void makeNegative(boolean become) {
        }
    };
    public static MathConstant PI_CONSTANT_NEGATIVE = new MathConstant((float) Math.PI, true) {
        @Override
        public void makeNegative(boolean become) {
        }
    };


    float hardCodedValue;

    public MathConstant(float number, boolean isNegative) {
        hardCodedValue = isNegative ? -number : number;
    }


    public MathConstant(float number) {
        hardCodedValue = number;
    }

    @Override
    public ValueSupplier getSupplier() {
        System.out.println("EMF math constant: this shouldn't happen!");
        return () -> hardCodedValue;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public void makeNegative(boolean become) {
        if (become) hardCodedValue = -hardCodedValue;
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }

    @Override // make fastest return
    public float get() {
        return hardCodedValue;
    }
}
