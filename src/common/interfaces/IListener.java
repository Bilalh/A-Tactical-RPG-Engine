package common.interfaces;

/**
 * Interface for the listeners.
 * @author Bilal Hussain
 */
public interface IListener<E> {

	void notifyChanged(E newValue);
}
