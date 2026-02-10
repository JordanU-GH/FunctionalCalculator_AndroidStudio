package edu.jsu.mcis.cs408.functionalcalculator;

import java.beans.PropertyChangeEvent;

public interface AbstractView {

    /*
     * This class is an interface instead of an abstract class because, in this
     * example, the Views already extend one of the Android View classes, and
     * multiple inheritance is not permitted in Java.  This interface is simple:
     * it specifies an abstract method called "modelPropertyChange()", which
     * receives a PropertyChange event from a Controller and updates the View(s)
     * accordingly.  Each View object which implements this interface must
     * implement "modelPropertyChange()" in its own way, since each View may
     * render or handle the corresponding property of the Model differently.
     */

    public abstract void modelPropertyChange(final PropertyChangeEvent evt);

}
