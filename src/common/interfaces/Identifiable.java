package common.interfaces;

import java.util.UUID;

/**
 * An object that has a unique identifier. They should be made using <code>UUID.randomUUID<code> 
 * when the object is created.
 * @author Bilal Hussain
 */
public interface Identifiable {

	UUID getUuid();

}