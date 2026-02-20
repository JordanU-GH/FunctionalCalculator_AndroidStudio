package edu.jsu.mcis.cs408.functionalcalculator;

import android.util.Log;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class DefaultModel extends AbstractModel {
    public static final String TAG = "DefaultModel";
    private String DisplayText;
    private BigDecimal LeftOperand;
    private BigDecimal RightOperand;
    private Operator CurrentOperator;
    private CalculatorState CurrentState;
    public enum CalculatorState{
        CLEAR{ @Override public CalculatorState nextState(){ return LHS; } },
        LHS{ @Override public CalculatorState nextState(){ return OP_SCHEDULED; } },
        OP_SCHEDULED{ @Override public CalculatorState nextState(){ return RHS; } },
        RHS{ @Override public CalculatorState nextState(){ return RESULT; } },
        RESULT{ @Override public CalculatorState nextState(){ return LHS; } },
        ERROR{ @Override public CalculatorState nextState(){ return CLEAR; } };
        public abstract CalculatorState nextState();
    }
    public enum Operator{
        ADD{public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.add(R, MathContext.DECIMAL64); } },
        SUB{public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.subtract(R, MathContext.DECIMAL64); } },
        MULT{public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.multiply(R, MathContext.DECIMAL64); } },
        DIV{public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.divide(R, MathContext.DECIMAL64); } },
        NEG{public BigDecimal compute(BigDecimal operand){ return operand.negate(); } },
        SQRT{public BigDecimal compute(BigDecimal operand){ return operand.sqrt(MathContext.DECIMAL64); } },
        PERC{public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.multiply(R).divide(new BigDecimal( 100), MathContext.DECIMAL64); } },
        NONE;
    }
    /*
     * Initialize the model elements to known default values.  We use the setter
     * methods instead of changing the values directly so that these changes are
     * properly announced to the Controller, and so that the Views can be updated
     * accordingly.
     */

    public void initDefault() {

        setDisplayText(new BigDecimal(0).toString());
        setCurrentOperator(Operator.NONE);
        setCurrentState(CalculatorState.CLEAR);
        setLeftOperand(new BigDecimal(0));
        setRightOperand(null);
    }

    /*
     * Simple getter methods for text1 and text2
     */

    public String getDisplayText() {
        return this.DisplayText;
    }
    public Operator getCurrentOperator(){ return this.CurrentOperator; }
    public CalculatorState getCurrentState(){ return this.CurrentState; }
    public BigDecimal getLeftOperand(){ return this.LeftOperand; }
    public BigDecimal getRightOperand(){ return this.RightOperand; }

    /*
     * Setter for the display.  Notice that, in addition to changing the
     * values, these methods announce the change to the controller by firing a
     * PropertyChange event.  Any registered AbstractController subclasses will
     * receive this event, and will propagate it to all registered Views so that
     * they can update themselves accordingly.
     */

    public void setDisplayText(String newVal) {

        String oldVal = this.DisplayText;
        this.DisplayText = newVal;

        Log.i(TAG, "DisplayText Change: From " + oldVal + " to " + newVal);

        firePropertyChange(DefaultController.ELEMENT_DISPLAY_PROPERTY, oldVal, newVal);

    }

    public void setCurrentOperator(Operator newOp) {
        this.CurrentOperator = newOp;
    }
    public void setCurrentState(CalculatorState newState){
     this.CurrentState = newState;
    }
    public void setRightOperand(BigDecimal newVal){
        RightOperand = newVal;
    }
    public void setLeftOperand(BigDecimal newVal){
        LeftOperand = newVal;
    }
}
