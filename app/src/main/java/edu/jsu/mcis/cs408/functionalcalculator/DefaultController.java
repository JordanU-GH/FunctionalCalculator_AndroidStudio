package edu.jsu.mcis.cs408.functionalcalculator;

import android.util.Log;

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
    private MainActivity view_zero;
    public static final String ELEMENT_DISPLAY_PROPERTY = "DisplayText";
    public static final String ELEMENT_LEFT_OPERAND_PROPERTY = "LeftOperand";
    public static final String ELEMENT_RIGHT_OPERAND_PROPERTY = "RightOperand";
    public static final String ELEMENT_CURRENT_OPERATOR_PROPERTY = "CurrentOperator";
    public static final String ELEMENT_CURRENT_STATE_PROPERTY = "CurrentState";
    public static final String ELEMENT_PERIOD_PROPERTY = "Period";
    public static final String ELEMENT_SQRT_PROPERTY = "Sqrt";


    // Method to set one model as our primary one
    public void setModel_zero(DefaultModel model){
        this.model_zero = model;
    }
    public void setView_zero(MainActivity view) { this.view_zero = view; }

    /*
     * These are the change methods which corresponds to ELEMENT_PROPERTIES.
     * They receive new data for the Model, and invokes "setModelProperty()"
     * (inherited from AbstractController) so that the proper Model can be found
     * and updated properly.
     */

    public void changeElementDisplay(String newVal) {
        this.setModelProperty(ELEMENT_DISPLAY_PROPERTY, newVal);
    }
    public void changeElementLeftOperand(String newVal) {
        this.setModelProperty(ELEMENT_LEFT_OPERAND_PROPERTY, newVal);
    }
    public void changeElementRightOperand(String newVal) {
        this.setModelProperty(ELEMENT_RIGHT_OPERAND_PROPERTY, newVal);
    }
    public void changeElementCurrentOperator(DefaultModel.Operator newVal) {
        this.setModelProperty(ELEMENT_CURRENT_OPERATOR_PROPERTY, newVal);
    }
    public void changeElementCurrentState(DefaultModel.CalculatorState newVal) {
        this.setModelProperty(ELEMENT_CURRENT_STATE_PROPERTY, newVal);
    }
    public void changeElementPeriod(Boolean newVal){
        this.setModelProperty(ELEMENT_PERIOD_PROPERTY, newVal);
    }
    public void changeElementSqrt(Boolean newVal){
        this.setModelProperty(ELEMENT_SQRT_PROPERTY, newVal);
    }

    // Method to determine button functionality
    public void handleButtonLogic(String tag){
        String shortTag = tag.replace("btn","");
        DefaultModel.CalculatorState state = model_zero.getCurrentState();
        // *******************************
        // code for debugging
        System.out.println("\n");
        System.out.println("-------------- Before Switch Statements ---------------");
        System.out.println(model_zero.toString());
        System.out.println("Pressed: " + shortTag);
        System.out.println("-----------------------------");
        // *******************************
        // Switch statements for determining logic
        switch (shortTag) {
            // First switch statement
            // These cases are for inputs that operate regardless of current state or can have multiple interactions
            // depending on the current state and/or operator
            case "Clear":
                resetValues();
                changeElementCurrentState(DefaultModel.CalculatorState.CLEAR);
                break;
            case "Equals":
                handleEquals(state);
                break;
            case "Neg":
                handleNeg(state);
                break;
            case "Sqrt":
                handleSQRT(state);
                break;
            case "Perc":
                handlePercent(state);
                break;
            default:
                // Second switch statement
                // These cases are for standard state transitions which operate
                // depending on the current state only
                switch (state) {
                    case CLEAR:
                        handleClear(shortTag);
                        break;
                    case LHS:
                        handleLHS(shortTag);
                        break;
                    case OP_SCHEDULED:
                        handleOperator(shortTag);
                        break;
                    case RHS:
                        handleRHS(shortTag);
                        break;
                    case RESULT:
                        handleResult(shortTag);
                        break;
                    case ERROR:
                        handleError(shortTag);
                        break;
                    default:
                        Log.i("DefaultController: ", "Failed to determine current state");
                        break;
                }
                break;
        }
        // *******************************
        // code for debugging
        System.out.println("------------ After Switch Statements -----------------");
        System.out.println(model_zero.toString());
        System.out.println("Pressed: " + shortTag);
        System.out.println("-----------------------------");
        System.out.println("\n");
        // *******************************
    }

    private void handleClear(String input){
        if (isNum(input)){
            changeElementCurrentState(DefaultModel.CalculatorState.CLEAR.nextState());
            handleLHS(input);
        }
        else if (input.equals("Period")){
            handlePeriod(model_zero.getLeftOperand());
        }else {
            handleOperator(input);
        }
    }
    private void handleLHS(String input){
        if (isNum(input)){
            try {
                int value = Integer.parseInt(input);
                buildLeftOperand(value);
            } catch (NumberFormatException e){
                System.out.println("Notification: Left Side Number Cannot Be Larger");
            }
        }
        else if (input.equals("Period")){
            handlePeriod(model_zero.getLeftOperand());
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
            changeElementSqrt(false);
        }
        else if (input.equals("Period")){
            handlePeriod(model_zero.getRightOperand());
        }
        else{
            for (DefaultModel.Operator op : DefaultModel.Operator.values()){
                if (op.name().equals(input.toUpperCase())) {
                    changeElementRightOperand("0");
                    changeElementCurrentOperator(op);
                    String operatorSymbol = view_zero.getButtonSymbol(input);
                    changeElementDisplay(model_zero.getLeftOperand() + " " + operatorSymbol);
                    changeElementPeriod(false);
                }
            }
        }
    }
    private void handleRHS(String input){
        if (isNum(input)){
            try {
                int value = Integer.parseInt(input);
                buildRightOperand(value);
            } catch (NumberFormatException e){
                System.out.println("Notification: Right Side Number Cannot Be Larger");
            }
        }
        else if (input.equals("Period")){
            handlePeriod(model_zero.getRightOperand());
        }
        else {
            changeElementCurrentState(DefaultModel.CalculatorState.RHS.nextState());
            computeResult();
            handleOperator(input);
        }
    }
    private void handleResult(String input){
        if (isNum(input)){
            changeElementCurrentState(DefaultModel.CalculatorState.RESULT.nextState());
            changeElementLeftOperand("0");
            handleLHS(input);
        }
        else if (input.equals("Period")){
            changeElementLeftOperand("0");
            handlePeriod(model_zero.getLeftOperand());
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
    private void handleEquals(DefaultModel.CalculatorState state){
        if (state.equals(DefaultModel.CalculatorState.OP_SCHEDULED)) {
            if (model_zero.getRightOperand().equals("0")) {
                model_zero.setRightOperand(model_zero.getLeftOperand());
            }
        }
        try {
            computeResult();
            changeElementCurrentState(DefaultModel.CalculatorState.RESULT);
        } catch (ArithmeticException e){
            System.out.println("Error: Issue With Performing Operation");
            e.printStackTrace();
            changeElementCurrentState(DefaultModel.CalculatorState.ERROR);
            changeElementDisplay(e.getMessage());
            view_zero.enableOperatorBtns(false);
        }
        changeElementPeriod(false);
    }
    private void handleSQRT(DefaultModel.CalculatorState state){
        DefaultModel.Operator currentOp = model_zero.getCurrentOperator();
        changeElementSqrt(true);
        if (state.equals(DefaultModel.CalculatorState.RHS)){
            BigDecimal newVal = DefaultModel.Operator.SQRT.compute(new BigDecimal(model_zero.getRightOperand()), new BigDecimal(0));
            changeElementDisplay(newVal.toString());
            changeElementRightOperand(newVal.toString());
        }
        else if (state.equals(DefaultModel.CalculatorState.OP_SCHEDULED)) {
            BigDecimal newVal = DefaultModel.Operator.SQRT.compute(new BigDecimal(model_zero.getLeftOperand()), new BigDecimal(0));
            changeElementDisplay(newVal.toString());
            changeElementRightOperand(newVal.toString());
        }
        else {
            changeElementCurrentOperator(DefaultModel.Operator.SQRT);
            computeResult();
            changeElementCurrentOperator(currentOp);
        }
    }
    private void handlePeriod(String currentOperand) {
        if (!model_zero.getPeriod()) {
            changeElementPeriod(true);
            String newOperand = "Period Placement Error";
            if (model_zero.getSqrt()){
                newOperand = "0.";
            }else{
                newOperand = currentOperand + ".";
            }
            if (model_zero.getCurrentState() == DefaultModel.CalculatorState.OP_SCHEDULED){
                changeElementRightOperand(newOperand);
                changeElementCurrentState(DefaultModel.CalculatorState.RHS);
            }
            else if (model_zero.getCurrentState() == DefaultModel.CalculatorState.RHS){
                changeElementRightOperand(newOperand);
            }
            else{
                changeElementLeftOperand(newOperand);
                changeElementCurrentState(DefaultModel.CalculatorState.LHS);
            }
            changeElementDisplay(newOperand);
            changeElementSqrt(false);
        }
    }
    private void handleNeg(DefaultModel.CalculatorState state){
        DefaultModel.Operator currentOp = model_zero.getCurrentOperator();
        if (state.equals(DefaultModel.CalculatorState.RHS)){
            BigDecimal newVal = DefaultModel.Operator.NEG.compute(new BigDecimal(model_zero.getRightOperand()), new BigDecimal(0));
            changeElementDisplay(newVal.toString());
            changeElementRightOperand(newVal.toString());
        }
        else if (state.equals(DefaultModel.CalculatorState.OP_SCHEDULED)) {
            BigDecimal newVal = DefaultModel.Operator.NEG.compute(new BigDecimal(model_zero.getLeftOperand()), new BigDecimal(0));
            changeElementDisplay(newVal.toString());
            changeElementRightOperand(newVal.toString());
        }
        else {
            changeElementCurrentOperator(DefaultModel.Operator.NEG);
            computeResult();
            changeElementCurrentOperator(currentOp);
            changeElementCurrentState(DefaultModel.CalculatorState.RESULT);
        }
    }
    private void handlePercent(DefaultModel.CalculatorState state){
        String newVal = "Percent Value Error";
        DefaultModel.Operator currentOp = model_zero.getCurrentOperator();
        boolean addSub = currentOp.equals(DefaultModel.Operator.ADD) || currentOp.equals(DefaultModel.Operator.SUB);
        boolean multDiv = currentOp.equals(DefaultModel.Operator.MULT) || currentOp.equals(DefaultModel.Operator.DIV);
        if (state.equals(DefaultModel.CalculatorState.RHS)){
            if (addSub){
                newVal = DefaultModel.Operator.PERC.compute(new BigDecimal(model_zero.getLeftOperand()), new BigDecimal(model_zero.getRightOperand())).toString();
            }else if (multDiv){
                newVal = DefaultModel.Operator.PERC.compute(new BigDecimal(1), new BigDecimal(model_zero.getRightOperand())).toString();
            }
            changeElementDisplay(newVal);
            changeElementRightOperand(newVal);
        }
        else if (state.equals(DefaultModel.CalculatorState.OP_SCHEDULED)) {
            if (addSub){
                newVal = DefaultModel.Operator.PERC.compute(new BigDecimal(model_zero.getLeftOperand()), new BigDecimal(model_zero.getLeftOperand())).toString();
            }else if (multDiv){
                newVal = DefaultModel.Operator.PERC.compute(new BigDecimal(1), new BigDecimal(model_zero.getLeftOperand())).toString();
            }
            changeElementDisplay(newVal);
            changeElementRightOperand(newVal);
        }
        else {
            changeElementCurrentOperator(DefaultModel.Operator.PERC);
            computeResult();
            changeElementCurrentOperator(currentOp);
            changeElementCurrentState(DefaultModel.CalculatorState.RESULT);
        }
    }
    private void buildLeftOperand(int digit){
        String left = model_zero.getLeftOperand();
        StringBuilder builder = new StringBuilder();
        if (!left.equals("0") && !model_zero.getSqrt()) {
            builder.append(left);
        }
        builder.append(digit);
        if (!view_zero.isDisplayFull(builder.toString())) {
            changeElementLeftOperand(builder.toString());
            changeElementDisplay(builder.toString());
        }
        else{
            System.out.println("Notification: Left Side Max Characters Reached");
        }
    }
    private void buildRightOperand(int digit) {
        String right = model_zero.getRightOperand();
        StringBuilder builder = new StringBuilder();
        if (!right.equals("0") && !model_zero.getSqrt()) {
            builder.append(right);
        }
        builder.append(digit);
        if (!view_zero.isDisplayFull(builder.toString())) {
            changeElementRightOperand(builder.toString());
            changeElementDisplay(builder.toString());
        }
        else{
            System.out.println("Notification: Right Side Max Characters Reached");
        }
    }
    private boolean isNum(String digit){
        return digit.matches("\\d");
    }
    private void computeResult(){
        DefaultModel.Operator op = model_zero.getCurrentOperator();
        BigDecimal result = op.compute(new BigDecimal(model_zero.getLeftOperand()), new BigDecimal(model_zero.getRightOperand()));
        changeElementDisplay(result.toString());
        changeElementLeftOperand(result.toString());
    }
    private void resetValues(){
        changeElementPeriod(false);
        changeElementLeftOperand("0");
        changeElementRightOperand("0");
        changeElementDisplay("0");
        changeElementCurrentOperator(DefaultModel.Operator.NONE);
        view_zero.enableOperatorBtns(true);
        changeElementSqrt(false);
    }
}
