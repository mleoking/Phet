/**
 * Class: AddModelElementCommand
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.bernoulli.model.command.commands;

import edu.colorado.phet.common.bernoulli.model.BaseModel;
import edu.colorado.phet.common.bernoulli.model.ModelElement;
import edu.colorado.phet.common.bernoulli.model.command.Command;

public class AddModelElementCommand implements Command {
    ModelElement me;
    BaseModel m;

    public AddModelElementCommand(BaseModel m, ModelElement me) {
        this.m = m;
        this.me = me;
    }

    public void doItLater() {
        m.execute(this);
    }

    public void doIt() {
        m.addModelElement(me);
    }
}
