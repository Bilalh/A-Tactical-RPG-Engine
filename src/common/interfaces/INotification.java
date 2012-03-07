package common.interfaces;

/**
 * A Notification has a object E that handle the Notification 
 * @author Bilal Hussain
 */
public interface INotification<E> {

	void process(E obj);
	
}
