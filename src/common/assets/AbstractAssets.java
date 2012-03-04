package common.assets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import common.interfaces.IWeapon;
import common.interfaces.Identifiable;

import config.IPreference;

/**
 * Stores assets by their id. Default implemnation of IAssets
 * 
 * @param E  The type of the Identifiable assert. 
 * 
 * @author Bilal Hussain
 */
public abstract class AbstractAssets<E extends Identifiable> implements  IAssets<E>  {
	
	@XStreamImplicit
	private Map<UUID, E> assets = new HashMap<UUID, E>();

	public AbstractAssets() {
		super();
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

}