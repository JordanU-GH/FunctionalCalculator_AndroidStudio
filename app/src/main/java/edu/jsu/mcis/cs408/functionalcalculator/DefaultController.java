package edu.jsu.mcis.cs408.functionalcalculator;

import android.util.Log;
import android.view.View;

import java.math.BigDecimal;

public class DefaultController extends AbstractController
{

    /*
     * These static property names are used as identifiers for the elements
     * of the Model and View which may need to be updated.  These updates can
     * be a result of changes to the Model which must be reflected in the View,
     * or a result of changes to the View (in response to user input) which must
     * be reflected in the Model.
     */
    private DefaultModel model_zero;
    public static final String ELEMENT_DISPLAY_PROPERTY = "DisplayText";
    public static final String ELEMENT_LEFT_OPERAND_PROPERTY = "LeftOperand";
    public static final String ELEMENT_RIGHT_OPERAND_PROPERTY = "RightOperand";
    public static final String ELEMENT_CURRENT_OPERATOR_PROPERTY = "CurrentOperator";
    public static final String ELEMENT_CURRENT_STATE_PROPERTY = "CurrentState";


    // Method to set one model as our primary one
    public void setModel_zero(DefaultModel model){
        this.model_zero = model;
    }

    /*
     * These are the change methods which corresponds to ELEMENT_PROPERTIES.
     * They receive new data for the Model, and invokes "setModelProperty()"
     * (inherited from AbstractController) so that the proper Model can be found
     * and updated properly.
     */

    public void changeElementDisplay(String newVal) {
        this.setModelProperty(ELEMENT_DISPLAY_PROPERTY, newVal);
    }
    public void changeElementLeftOperand(BigDecimal newVal) {
        this.setModelProperty(ELEMENT_LEFT_OPERAND_PROPERTY, newVal);
    }
    public void changeElementRightOperand(BigDecimal newVal) {
        this.setModelProperty(ELEMENT_RIGHT_OPERAND_PROPERTY, newVal);
    }
    public void changeElementCurrentOperator(DefaultModel.Operator newVal) {
        this.setModelProperty(ELEMENT_CURRENT_OPERATOR_PROPERTY, newVal);
    }
    public void changeElementCurrentState(DefaultModel.CalculatorState newVal) {
        this.setModelProperty(ELEMENT_CURRENT_STATE_PROPERTY, newVal);
    }

    // Method to determine button functionality
    public void handleButtonLogic(View view){
        String tag = view.getTag().toString().replace("btn","");
        DefaultModel.CalculatorState state = model_zero.getCurrentState();
        // *******************************
        // code for debugging
        System.out.println("--------------Before Switch---------------");
        System.out.println(model_zero.toString());
        System.out.println("Pressed: " + tag);
        System.out.println("-----------------------------");
        // *******************************
        // determine the current state and what to do based on it
        if (tag.equals("Clear")) {
            resetValues();
            changeElementDisplay("0");
            changeElementCurrentState(DefaultModel.CalculatorState.CLEAR);
        }
        else if (tag.equals("Equals")){
            computeResult();
            changeElementCurrentState(DefaultModel.CalculatorState.RESULT);
        }
        else{
            switch (state) {
                case CLEAR:
                    handleClear(tag);
                    break;
                case LHS:
                    handleLHS(tag);
                    break;
                case OP_SCHEDULED:
                    handleOperator(tag);
                    break;
                case RHS:
                    handleRHS(tag);
                    break;
                case RESULT:
                    handleResult(tag);
                    break;
                case ERROR:
                    handleError(tag);
                    break;
                default:
                    Log.i("DefaultController: ", "Failed to determine current state");
                    break;
            }
        }
        // *******************************
        // code for debugging
        System.out.println("------------After Switch-----------------");
        System.out.println(model_zero.toString());
        System.out.println("Pressed: " + tag);
        System.out.println("-----------------------------");
        // *******************************
    }

    private void handleClear(String input){
        if (isNum(input)){
            changeElementCurrentState(DefaultModel.CalculatorState.CLEAR.nextState());
            handleLHS(input);
        } else {
            changeElementCurrentState(DefaultModel.CalculatorState.OP_SCHEDULED);
            handleOperator(input);
        }
    }
    private void handleLHS(String input){
        if (isNum(input)){
            buildLeftOperand(Integer.parseInt(input));
        }
        else {
            changeElementCurrentState(DefaultModel.CalculatorState.LHS.nextState());
            handleOperator(input);
        }
    }
    private void handleOperator(String input){
        if (isNum(input)){
            changeElementCurrentState(DefaultModel.CalculatorState.OP_SCHEDULED.nextState());
            handleRHS(input);
        }
        else{
            for (DefaultModel.Operator op : DefaultModel.Operator.values()){
                if (op.name().equals(input.toUpperCase())) {
                    changeElementCurrentOperator(op);
                }
            }
        }
    }
    private void handleRHS(String input){
        if (isNum(input)){
            buildRightOperand(Integer.parseInt(input));
        }
        else {
            changeElementCurrentState(DefaultModel.CalculatorState.RHS.nextState());
            computeResult();
        }
    }
    private void handleResult(String input){
        if (isNum(input)){
            changeElementCurrentState(DefaultModel.CalculatorState.RESULT.nextState());
            //changeElementLeftOperand(new BigDecimal(0));
            handleLHS(input);
        }
        else {
            changeElementCurrentState(DefaultModel.CalculatorState.OP_SCHEDULED);
            handleOperator(input);
        }
    }
    private void handleError(String input){
        resetValues();
        if (isNum(input)){
            changeElementCurrentState(DefaultModel.CalculatorState.ERROR.nextState());
            handleLHS(input);
        }
    }
    private void buildLeftOperand(int digit){
        BigDecimal left = model_zero.getLeftOperand();
        StringBuilder builder = new StringBuilder();
        if (!left.equals(new BigDecimal(0))) {
            builder.append(left);
        }
        builder.append(digit);
        left = BigDecimal.valueOf(Integer.parseInt(builder.toString()));
        changeElementLeftOperand(left);
        changeElementDisplay(builder.toString());
    }
    private void buildRightOperand(int digit){
        BigDecimal right = model_zero.getRightOperand();
        StringBuilder builder = new StringBuilder();
        if (!right.equals(new BigDecimal(0))) {
            builder.append(right);
        }
        builder.append(digit);
        right = BigDecimal.valueOf(Integer.parseInt(builder.toString()));
        changeElementRightOperand(right);
        changeElementDisplay(builder.toString());
    }
    private boolean isNum(String digit){
        return digit.matches("\\d");
    }
    private void computeResult(){
        DefaultModel.Operator op = model_zero.getCurrentOperator();
        BigDecimal result = op.compute(model_zero.getLeftOperand(), model_zero.getRightOperand());
        changeElementDisplay(result.toString());
        changeElementLeftOperand(result);
    }
    private void resetValues(){
        changeElementLeftOperand(new BigDecimal(0));
        changeElementRightOperand(new BigDecimal(0));
        changeElementCurrentOperator(DefaultModel.Operator.NONE);
    }
}
