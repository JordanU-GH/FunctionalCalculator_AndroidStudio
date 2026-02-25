package edu.jsu.mcis.cs408.functionalcalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.functionalcalculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements AbstractView {
    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private DefaultController controller;
    private int DisplayID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /* Create Controller and Model */
        controller = new DefaultController();
        DefaultModel model = new DefaultModel();

        /* Register Activity View and Model with Controller */
        controller.addView(this);
        controller.setView_zero(this);
        controller.addModel(model);
        controller.setModel_zero(model);

        /* Initialize the Layout of the Calculator*/
        initLayout();

        /* Initialize Model to Default Values */
        model.initDefault();
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {
        /*
         * This method is called by the "propertyChange()" method of AbstractController
         * when a change is made to an element of a Model.  It identifies the element that
         * was changed and updates the View accordingly.
         */
        String propertyName = evt.getPropertyName();
        String propertyValue = evt.getNewValue().toString();

        // Log statement to for testing changes to the display
        //Log.i(TAG, "New " + propertyName + " Value from Model: " + propertyValue);

        if ( propertyName.equals(DefaultController.ELEMENT_DISPLAY_PROPERTY) ) {
            TextView display = (TextView) binding.getRoot().getViewById(DisplayID);
            display.setText(propertyValue);
        }
    }

    // Method to handle button presses
    class CalculatorClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String tag = view.getTag().toString();
            // Toasts to show button inputs for testing
            //Toast toast = Toast.makeText(binding.getRoot().getContext(), tag, Toast.LENGTH_SHORT);
            //toast.show();
            controller.handleButtonLogic(tag);
        }
    }

    // Method to create the applications' views (buttons,labels,etc.) programmatically
    private void initLayout(){
        // variables that are used to define the number of buttons on the calculator
        final int BUTTON_COLUMNS = 5;
        final int BUTTON_ROWS = 4;
        // Constants used for attributes
        final int BUTTON_TEXT_SIZE = 24;
        final int DISPLAY_TEXT_SIZE = 48;
        final int BUTTON_LEFT_CONSTRAINT_MARGIN = 8;
        final int BUTTON_TOP_CONSTRAINT_MARGIN = 8;

        // obtain a reference to the ConstraintLayout being used by the application
        ConstraintLayout layout = binding.main;
        // Obtain id's for our guidelines
        int northID = binding.guideNorth.getId();
        int eastID = binding.guideEast.getId();
        int southID = binding.guideSouth.getId();
        int westID = binding.guideWest.getId();
        // Create 2D arrays to define ordering of buttons by view id
        int [][] horizontals = new int [BUTTON_ROWS][BUTTON_COLUMNS];
        int [][] verticals = new int [BUTTON_COLUMNS][BUTTON_ROWS];

        // Create a new constraint set to be configured to our needs
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        // Create the calculator's display, set its attributes, and add it to the layout
        String displayName = getResources().getString(R.string.display_name);
        String displayText = getResources().getString(R.string.display_default_value);
        int displayID = View.generateViewId();
        TextView display = new TextView(this);
        this.DisplayID = displayID;
        customizeDisplay(display, displayID, displayName, displayText, DISPLAY_TEXT_SIZE);
        layout.addView(display);

        // add constraints to our display
        set.constrainWidth(displayID, ConstraintSet.MATCH_CONSTRAINT);
        set.constrainHeight(displayID, ConstraintSet.WRAP_CONTENT);
        set.connect(displayID, ConstraintSet.TOP, northID, ConstraintSet.BOTTOM, 0);
        set.connect(displayID, ConstraintSet.LEFT, westID, ConstraintSet.RIGHT, 0);
        set.connect(displayID, ConstraintSet.RIGHT, eastID, ConstraintSet.LEFT, 0);

        // Create a new clickhandler for button logic
        CalculatorClickHandler click = new CalculatorClickHandler();
        // Get references to our string-array resources for our buttons' names and faces
        String [] names = getResources().getStringArray(R.array.btn_names);
        String [] faces = getResources().getStringArray(R.array.btn_faces);
        // nested for-loops to create buttons, set their attributes, add them to the layout, add their constraints, and populate our 2D arrays
        for (int row = 0; row < BUTTON_ROWS; row++){
            for (int col = 0; col < BUTTON_COLUMNS; col++){
                int index = (BUTTON_COLUMNS * row) + col;
                int id = View.generateViewId();
                Button btn = new Button(this);
                customizeBtn(btn, id, index, names, faces, BUTTON_TEXT_SIZE);
                layout.addView(btn);
                // Set an onClickListener for each button
                btn.setOnClickListener(click);
                horizontals[row][col] = id;
                verticals[col][row] = id;
                // Adding constraints
                // Horizontal constraints
                if (col == 0) {
                    set.connect(id, ConstraintSet.LEFT, westID, ConstraintSet.LEFT, 0);
                }
                else if (col == BUTTON_COLUMNS - 1){
                    set.connect(id, ConstraintSet.RIGHT, eastID, ConstraintSet.RIGHT, 0);
                    set.connect(id, ConstraintSet.LEFT, horizontals[row][col-1], ConstraintSet.LEFT, BUTTON_LEFT_CONSTRAINT_MARGIN);
                }
                else{
                    set.connect(id, ConstraintSet.LEFT, horizontals[row][col-1], ConstraintSet.LEFT, BUTTON_LEFT_CONSTRAINT_MARGIN);
                }
                // Vertical constraints
                if (row == 0) {
                    set.connect(id, ConstraintSet.TOP, displayID, ConstraintSet.BOTTOM, 0);
                }
                else if (col == BUTTON_ROWS - 1){
                    set.connect(id, ConstraintSet.BOTTOM, southID, ConstraintSet.BOTTOM, 0);
                    set.connect(id, ConstraintSet.TOP, horizontals[row-1][col], ConstraintSet.TOP, BUTTON_TOP_CONSTRAINT_MARGIN);
                }
                else{
                    set.connect(id, ConstraintSet.TOP, horizontals[row-1][col], ConstraintSet.TOP, BUTTON_TOP_CONSTRAINT_MARGIN);
                }
            }
        }

        // Iterate through our 2D arrays to build chains to arrange the buttons
        for (int [] viewIds : horizontals) {
            set.createHorizontalChain(westID, ConstraintSet.LEFT, eastID, ConstraintSet.RIGHT, viewIds, null, ConstraintSet.CHAIN_PACKED);
        }
        for (int [] viewIds : verticals){
            set.createVerticalChain(displayID, ConstraintSet.BOTTOM, southID, ConstraintSet.TOP, viewIds, null, ConstraintSet.CHAIN_PACKED);
        }

        // apply our ConstraintSet to the Layout
        set.applyTo(layout);
    }

    private void customizeDisplay(TextView display, int id, String name, String text, int txtSize){
        display.setId(id);
        display.setTag(name);
        display.setText(text);
        display.setTextSize(txtSize);
        display.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        display.setMaxLines(1);
    }
    private void customizeBtn(Button btn, int id, int index, String [] names, String [] faces, int txtSize){
        btn.setId(id);
        btn.setTag(names[index]);
        btn.setText(faces[index]);
        btn.setTextSize(txtSize);
        //btn.setBackgroundColor(getResources().getColor(R.color.blue));
        //btn.setClicked(getResources().getColor(R.color.darker_blue));
    }
    public String getButtonSymbol(String tag){
        String name = "btn" + tag;
        String[] buttons = getResources().getStringArray(R.array.btn_names);
        for (int i = 0; i < buttons.length; i++){
            if (buttons[i].equals(name)){
                return getResources().getStringArray(R.array.btn_faces)[i];
            }
        }
        return null;
    }
    public Boolean isDisplayFull(String newText){
        TextView display = (TextView) binding.getRoot().getViewById(DisplayID);
        int displayWidth = display.getWidth();
        String padding = " X";
        String textWithPadding = newText + padding;
        return display.getPaint().measureText(textWithPadding) > displayWidth;
    }
    public void enableOperatorBtns(Boolean flag){
        ConstraintLayout parent = binding.getRoot();
        for (int i = 0; i < parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            if (child instanceof Button){
                String tag = child.getTag().toString();
                if (!tag.replace("btn", "").matches("\\d")) {
                    child.setEnabled(flag || tag.equals("btnClear"));
                }
            }
        }
    }
}