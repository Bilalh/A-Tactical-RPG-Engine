package common.interfaces;

/**
 * A Notification has a object E that handle the Notification 
 * @author Bilal Hussain
 */
public interface INotification<E> {

	/**
	 * Process the notification.
	 *
	 * @param obj the object to handle the notification/
	 */
	void process(E obj);
	
}
