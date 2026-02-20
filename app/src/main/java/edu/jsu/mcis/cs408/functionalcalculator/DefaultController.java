package edu.jsu.mcis.cs408.functionalcalculator;

public class DefaultController extends AbstractController
{

    /*
     * This static property name is used as an identifier for the element
     * of the Model and View which may need to be updated.  These updates can
     * be a result of changes to the Model which must be reflected in the View,
     * or a result of changes to the View (in response to user input) which must
     * be reflected in the Model.
     */

    public static final String ELEMENT_DISPLAY_PROPERTY = "DisplayText";

    /*
     * This is the change method which corresponds to ELEMENT_DISPLAY_PROPERTY.
     * It receives the new data for the Model, and invokes "setModelProperty()"
     * (inherited from AbstractController) so that the proper Model can be found
     * and updated properly.
     */

    public void changeElementDisplay(String newVal) {
        setModelProperty(ELEMENT_DISPLAY_PROPERTY, newVal);
    }

}
