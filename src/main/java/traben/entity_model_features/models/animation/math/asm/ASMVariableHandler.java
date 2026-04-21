package traben.entity_model_features.models.animation.math.asm;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.math.EMFMathException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BALOAD;
import static org.objectweb.asm.Opcodes.FALOAD;
import static org.objectweb.asm.Opcodes.FNEG;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IXOR;

public class ASMVariableHandler {

    Stack<Boolean> booleanScopeStack = new Stack<>();
    List<String> floatVarList = new ArrayList<>();
    List<String> boolVarList = new ArrayList<>();
    Set<String> readVarNames = new HashSet<>();

    int localVarIndex = 1;

    public int getLocalVarIndex() {
        return ++localVarIndex;
    }
    public void popLocalVarIndex(int was) {
        if (localVarIndex != was) throw new IllegalStateException("popLocalVarIndex was not called in correct order");
        --localVarIndex;
    }

    public List<String> getFloatVarList() {
        return floatVarList;
    }

    public List<String> getBoolVarList() {
        return boolVarList;
    }

    public void scopeFloat() {
        booleanScopeStack.push(false);
    }
    public void scopeBool() {
        booleanScopeStack.push(true);
    }
    public void scopePop() {
        booleanScopeStack.pop();
    }

    public void verifyEndOfParse() throws EMFMathException {
        if (!booleanScopeStack.isEmpty())
            throw new EMFMathException("ASMVariableHandler verifyEndOfParse issue: type stack is not empty");

        if (localVarIndex != 1)
            throw new EMFMathException("ASMVariableHandler verifyEndOfParse issue: local variable index is not reset");

        if (floatVarList.stream().anyMatch(it-> boolVarList.contains(it)))
            throw new EMFMathException("ASMVariableHandler verifyEndOfParse issue: has variable names that are both in float and bool lists");
    }

    public boolean isScopeBool() {
        return booleanScopeStack.peek();
    }
    public boolean isScopeFloat() {
        return !isScopeBool();
    }

    public boolean isReadVarName(String varName) {
        return readVarNames.contains(varName);
    }

    public int getVarIndexFromOutsideParse(String varName, boolean reading, boolean isBoolean) {
        booleanScopeStack.push(isBoolean);
        var v = getAndAssignVarIndex(varName, reading);
        booleanScopeStack.pop();
        return v;
    }

    public int getAndAssignVarIndex(String varName, boolean reading) {
        if (reading) readVarNames.add(varName);

        var list = (booleanScopeStack.peek() ? boolVarList : floatVarList);
        if (list.contains(varName)) {
            return list.indexOf(varName);
        } else {
            list.add(varName);
            return list.size() - 1;
        }
    }

    // Counter is useful enough to be wanted commonly in other stuff e.g. print functions
    public void asmVisitFrameCounter(MethodVisitor mv) {
        scopeFloat();
        asmVisitVar(mv, "frame_counter");
        scopePop();
    }

    public void asmVisitVar(MethodVisitor mv, String varName) {
        int index = getAndAssignVarIndex(varName, true);

        boolean isBoolean = booleanScopeStack.peek();

        mv.visitVarInsn(ALOAD, isBoolean ? 1 : 0);
        mv.visitLdcInsn(index);
        mv.visitInsn(isBoolean ? BALOAD : FALOAD);
    }

    public void asmInvertBoolean(MethodVisitor mv) {
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IXOR);
    }

    public void asmNegateFloat(MethodVisitor mv) {
        mv.visitInsn(FNEG);
    }

    public record AnimVars(float[] floats, boolean[] bools) { }
}
