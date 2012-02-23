package common.interfaces;

/**
 * @author Bilal Hussain
 */
public interface IListener<E> {

	void notifyChanged(E newValue);
}
