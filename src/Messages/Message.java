/**
 * 
 */
package Messages;

import java.io.Serializable;

/**
 * @author Harjap Gill
 * @author Jason Gao
 *
 */
public abstract class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public abstract MessageTypes getMessageType();
}
