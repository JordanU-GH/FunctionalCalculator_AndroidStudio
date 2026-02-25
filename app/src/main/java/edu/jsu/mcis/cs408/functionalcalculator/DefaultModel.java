package edu.jsu.mcis.cs408.functionalcalculator;

import android.os.Build;
import android.util.Log;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class DefaultModel extends AbstractModel {
    public static final String TAG = "DefaultModel";
    private String DisplayText;
    private String LeftOperand;
    private String RightOperand;
    private Operator CurrentOperator;
    private CalculatorState CurrentState;
    private Boolean period;
    private Boolean sqrt;
    public enum CalculatorState{
        CLEAR{ @Override public CalculatorState nextState(){ return LHS; } },
        LHS{ @Override public CalculatorState nextState(){ return OP_SCHEDULED; } },
        OP_SCHEDULED{ @Override public CalculatorState nextState(){ return RHS; } },
        RHS{ @Override public CalculatorState nextState(){ return OP_SCHEDULED; } },
        RESULT{ @Override public CalculatorState nextState(){ return LHS; } },
        ERROR{ @Override public CalculatorState nextState(){ return LHS; } };
        public abstract CalculatorState nextState();
    }
    public enum Operator{
        ADD{@Override public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.add(R, MathContext.DECIMAL32); } },
        SUB{@Override public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.subtract(R, MathContext.DECIMAL32); } },
        MULT{@Override public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.multiply(R, MathContext.DECIMAL32); } },
        DIV{@Override public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.divide(R, MathContext.DECIMAL32); } },
        NEG{@Override public BigDecimal compute(BigDecimal operand, BigDecimal empty){ return operand.negate(); } },
        SQRT{@Override public BigDecimal compute(BigDecimal operand, BigDecimal empty){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return operand.sqrt(MathContext.DECIMAL32);
            }
            else{
                System.out.println("Error: Could not compute the square root because of an outdated Build Version");
                return new BigDecimal("0");
            }
        } },
        PERC{@Override public BigDecimal compute(BigDecimal L, BigDecimal R){ return L.multiply(R).divide(new BigDecimal( 100), MathContext.DECIMAL32); } },
        NONE{@Override public BigDecimal compute(BigDecimal operand, BigDecimal empty){ return operand; }};
        public abstract BigDecimal compute(BigDecimal L, BigDecimal R);
    }
    /*
     * Initialize the model elements to known default values.  We use the setter
     * methods instead of changing the values directly so that these changes are
     * properly announced to the Controller, and so that the Views can be updated
     * accordingly.
     */

    public void initDefault() {

        this.setCurrentOperator(Operator.NONE);
        this.setCurrentState(CalculatorState.CLEAR);
        this.setLeftOperand("0");
        this.setRightOperand("0");
        this.setDisplayText("0");
        this.setPeriod(false);
        this.setSqrt(false);

    }

    /*
     * Simple getter methods for text1 and text2
     */

    public String getDisplayText() {
        return this.DisplayText;
    }
    public Operator getCurrentOperator(){ return this.CurrentOperator; }
    public CalculatorState getCurrentState(){ return this.CurrentState; }
    public String getLeftOperand(){ return this.LeftOperand; }
    public String getRightOperand(){ return this.RightOperand; }
    public Boolean getPeriod(){ return this.period; }
    public Boolean getSqrt(){ return this.sqrt; }

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

        // Log statement for troubleshooting
        //Log.i(TAG, "DisplayText Change: From " + oldVal + " to " + newVal);

        firePropertyChange(DefaultController.ELEMENT_DISPLAY_PROPERTY, oldVal, newVal);

    }

    public void setCurrentOperator(Operator newOp) { this.CurrentOperator = newOp; }
    public void setCurrentState(CalculatorState newState){
     this.CurrentState = newState;
    }
    public void setRightOperand(String newVal){
        this.RightOperand = newVal;
    }
    public void setLeftOperand(String newVal){
        this.LeftOperand = newVal;
    }
    public void setPeriod(Boolean newVal){ this.period = newVal; }
    public void setSqrt(Boolean newVal){ this.sqrt = newVal; }

    @Override
    public String toString(){
        HashMap<String, Object> dict = new HashMap<>();
        dict.put("Operator", this.getCurrentOperator());
        dict.put("State", this.getCurrentState());
        dict.put("LeftOperand", this.getLeftOperand());
        dict.put("RightOperand", this.getRightOperand());
        dict.put("Period", this.getPeriod());
        dict.put("Sqrt", this.getSqrt());
        return dict.toString();
    }
}
