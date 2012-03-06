package common.assets;

import java.util.*;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import common.interfaces.IWeapon;
import common.interfaces.Identifiable;

import config.Config;
import config.IPreference;
import engine.unit.IMutableUnit;

/**
 * Stores assets by their id. Default implemnation of IAssets.
 * It is also Iterable over the values of type E 
 * 
 * @param E  The type of the Identifiable assert. 
 * 
 * @author Bilal Hussain
 */
public abstract class AbstractAssets<E extends Identifiable> implements  IAssets<E>, Iterable<E>  {
	
	@XStreamImplicit
	private Map<UUID, E> assets = new HashMap<UUID, E>();

	public AbstractAssets() {
		super();
	}

	// to give default values
	private Object readResolve() {
		if (assets == null){
			assets = new HashMap<UUID, E>();
		}
		return this;
	}
	
	@Override
	public E get(UUID uuid) {
		return assets.get(uuid);
	}
	
	@Override
	public E put(E e) {
		return assets.put(e.getUuid(), e);
	}

	@Override
	public void putAll(Map<? extends UUID, ? extends E> map) {
		assets.putAll(map);
	}

	@Override
	public E remove(UUID id) {
		return assets.remove(id);
	}

	@Override
	public void clear() {
		assets.clear();
	}

	@Override
	public boolean containsKey(UUID id) {
		return assets.containsKey(id);
	}

	@Override
	public Collection<E> values() {
		return assets.values();
	}

	@Override
	public Map<UUID, E> getMap() {
		return assets;
	}

	@Override
	public int size() {
		return assets.size();
	}

	@Override
	public boolean isEmpty() {
		return assets.isEmpty();
	}

	@Override
	public String toString() {
		return String.format("AbstractAssets [assets=%s]", assets);
	}

	@Override
	public Iterator<E> iterator() {
		return assets.values().iterator();
	}
	
}