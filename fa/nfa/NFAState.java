/**
 * 
 */
package fa.nfa;

import java.util.Map;
import java.util.Set;

import fa.State;

/**
 * NFA State representation
 * @author Jered Fennell, Austin Platt
 * @version 1.0
 */
public class NFAState extends State {
	
	Map<Character, Set<NFAState>> transitions;

	/**
	 * 
	 */
	public NFAState() {
		super();
		transitions = null;
	}

	/**
	 * @param name
	 */
	public NFAState(String name) {
		super(name);
		transitions = null;
	}
	
	public void setTransitions(Map<Character, Set<NFAState>> set) {
		transitions = set;
	}

	public Set<NFAState> toStates(char c) {
		return transitions.get(c);
	}

}
