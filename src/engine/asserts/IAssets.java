package engine.asserts;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import common.interfaces.IWeapon;
import common.interfaces.Identifiable;
import config.IPreference;

/**
 * Stores assets by thier id.
 * @author Bilal Hussain
 */
public interface IAssets<E extends Identifiable> extends IPreference {

	E put(E e);

	void putAll(Map<? extends UUID, ? extends E> e);

	E remove(UUID id);

	void clear();

	boolean containsKey(UUID id);

	Collection<E> values();

	Map<UUID, E> getMap();
	
	int size();

	boolean isEmpty();


}