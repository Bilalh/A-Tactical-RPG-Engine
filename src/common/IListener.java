package common;

/**
 * @author Bilal Hussain
 */
public interface IListener<E> {

	void notifyChanged(E newValue);
}
