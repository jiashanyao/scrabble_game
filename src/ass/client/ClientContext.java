package ass.client;

import ass.communication.GameContext;
import org.apache.commons.beanutils.BeanUtils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

public class ClientContext extends GameContext {

    private PropertyChangeSupport changeSupport;

    public ClientContext(GameContext gameContext) throws InvocationTargetException, IllegalAccessException {
        super();
        BeanUtils.copyProperties(this, gameContext);
        this.changeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
