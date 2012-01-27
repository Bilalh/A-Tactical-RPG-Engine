import java.util.ArrayList;
import java.util.List;

import common.interfaces.INotification;

import view.notifications.ChooseUnitsNotifications;
import view.notifications.PlayersTurnNotification;


/**
 * @author Bilal Hussain
 * @param <E>
 */
public class G<E> {

	interface Observer<T extends Observer<T,O>, O extends Observable<O,T>> {
	    void update(O observable);
	}

		
	static class Observable<T extends Observable<T,O>,O extends Observer<O,T>> {
	    public void addObserver(O observer) { }
	    public void notifyObservers() { }
	}

	interface ObserverA extends Observer<ObserverA, ObservableA> {
	}
		
	static class ObservableA extends Observable<ObservableA, ObserverA > {
	}
	
	INotification<?> n;
	
	public void send(){
		c(new  PlayersTurnNotification());
	}

	<E extends INotification<?>> void c(E n){
		b(n);
	}
	
	void b(INotification n){
		
	}
	
	void b(PlayersTurnNotification n){
		System.out.println("PlayersTurnNotification");
	}
	
	void a(INotification<?> n){
		System.out.println("INotification");
	}
	
	void a(PlayersTurnNotification n){
		System.out.println("PlayersTurnNotification");
	}
	
	public static void main(String[] args) {
		G g = new G<PlayersTurnNotification>();
		g.send();
	}
	
}
