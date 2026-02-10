package edu.jsu.mcis.cs408.functionalcalculator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class AbstractController implements PropertyChangeListener {

    private ArrayList<AbstractView> views;
    private ArrayList<AbstractModel> models;

    public AbstractController() {

        /* Initialize View and Model Lists */

        views = new ArrayList<>();
        models = new ArrayList<>();

    }

    public void addModel(AbstractModel model) {

        /*
         * Add a new Model to the list, and register this controller as the listener for
         * PropertyChange events
         */

        models.add(model);
        model.addPropertyChangeListener(this);

    }

    public void removeModel(AbstractModel model) {

        /*
         * Remove a Model from the list, and un-register this controller as the listener for
         * PropertyChange events
         */

        models.remove(model);
        model.removePropertyChangeListener(this);

    }

    public void addView(AbstractView view) {

        /* Add a new View to the list */

        views.add(view);

    }

    public void removeView(AbstractView view) {

        /* Remove a View from the list */

        views.remove(view);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        /*
         * This method is called automatically by "firePropertyChange()" when an
         * element of a Model is changed.  It informs all registered Views of
         * the change so that they can update themselves accordingly.
         */

        for (AbstractView view : views) {
            view.modelPropertyChange(evt);
        }

    }

    protected void setModelProperty(String propertyName, Object newValue) {

        /*
         * This method is called by an AbstractController subclass when the View
         * informs it of a user interaction which requires a change to a Model.
         * Using the property name, it identifies which of the registered Models
         * has the corresponding setter method (using reflection), and then invokes
         * this method so that the Model(s) can be updated properly.
         */

        for (AbstractModel model : models) {

            try {

                Method method = model.getClass().getMethod("set" + propertyName, newValue.getClass());
                method.invoke(model, newValue);

            }

            catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
