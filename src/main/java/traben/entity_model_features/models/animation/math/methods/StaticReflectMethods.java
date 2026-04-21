package traben.entity_model_features.models.animation.math.methods;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.asm.ASMVisitable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class StaticReflectMethods extends MathMethod {

    private final Method staticMethod;


    final @Nullable ASMVisitable asmCompiler;

    protected StaticReflectMethods(List<String> args,
                                   boolean isNegative,
                                   AnimSetupContext context,
                                   Method staticMethod,
                                   @Nullable ASMVisitable asmCompiler
    ) throws EMFMathException {
        super(isNegative, context, args, getParameterCount(staticMethod));
        this.staticMethod = staticMethod;
        boolean isBoolean = staticMethod.getReturnType().equals(boolean.class) || staticMethod.getReturnType().equals(Boolean.class);
        this.asmCompiler = asmCompiler;


        // pre-fill with constant strings
        Object[] computedArgs = new Object[getParameterCount(staticMethod)];
        for (int i = 0; i < parsedArgs.size(); i++) {
            var arg = parsedArgs.get(i);
            if (arg == null) computedArgs[i] = rawArgs.get(i); // String
        }
        setSupplierAndOptimize(() -> {
            try {
                // Fill variable args
                for (int i = 0; i < parsedArgs.size(); i++) {
                    var arg = parsedArgs.get(i);
                    if (arg != null) computedArgs[i] = arg.getResult();
                }

                if (isBoolean) return MathValue.fromBoolean((boolean) staticMethod.invoke(null, computedArgs));
                else return (float) staticMethod.invoke(null, computedArgs);
            } catch (Exception e) {
                e.printStackTrace();
                return Float.NaN;
            }
        }, parsedArgs);
    }

    public static MethodRegistry.MethodFactory makeFactory(String methodName, Method staticMethod, @Nullable ASMVisitable asmCompiler) {
        return (args, isNegative, calculationInstance) -> {
            try {
                if (!Modifier.isStatic(staticMethod.getModifiers()))
                    throw new EMFMathException(staticMethod.getName() + " is not static");
                if (!Modifier.isPublic(staticMethod.getModifiers()))
                    throw new EMFMathException(staticMethod.getName() + " is not public");
                if (staticMethod.getReturnType() != boolean.class && staticMethod.getReturnType() != float.class)
                    throw new EMFMathException(staticMethod.getName() + " does not return either a float or boolean primitive type");
                if (Arrays.stream(staticMethod.getParameterTypes()).anyMatch((it)->
                    !(it == boolean.class || it == float.class || it == String.class)))
                    throw new EMFMathException(staticMethod.getName() + " has a parameter that is not a float or boolean primitive type, or a String Object");

                return new StaticReflectMethods(args, isNegative, calculationInstance, staticMethod, asmCompiler);
            } catch (Exception e) {
                throw new EMFMathException("Failed to create " + methodName + "() method, because: " + e);
            }
        };

    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        var params = staticMethod.getParameterTypes();
        for (int i = 0; i < parsedArgs.size(); i++) {
            var arg = parsedArgs.get(i);
            if (arg == null) {
                mv.visitLdcInsn(rawArgs.get(i)); // String
            } else {
                if (params[i] == boolean.class) {
                    vars.scopeBool();
                } else {
                    vars.scopeFloat();
                }
                arg.asmVisit(mv, vars);
                vars.scopePop();
            }
        }

        if (asmCompiler != null) {
            asmCompiler.asmVisit(mv, vars);
            return;
        }

        ASMHelper.visitStaticFunctionASM(mv, staticMethod);
    }

    private static int getParameterCount(Method method) {
        return method.getParameterCount();
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return true;
    }

}
