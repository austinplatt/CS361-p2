package fa.nfa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.LinkedList;

import fa.State;



/**
 * A Class to represent a non-deterministic finite automata
 * @author Jered Fennell, Austin Platt
 * @version 1.0
 */
public class NFA implements NFAInterface {
	
	private Set<NFAState> states;
    private Set<Character> alphabet;
    private NFAState startState;
    private Set<NFAState> finalStates;
    private LinkedHashMap<NFAState, Map<Character, Set<NFAState>>> transitions;

	/**
	 * 
	 */
	public NFA() {
		states = new LinkedHashSet<NFAState>();
        alphabet = new LinkedHashSet<Character>();
        finalStates = new LinkedHashSet<NFAState>();
        transitions = new LinkedHashMap<NFAState, Map<Character, Set<NFAState>>>();
        startState = null;
	}

	@Override
	public boolean addState(String name) {
		NFAState newState = new NFAState(name);
        
        if (getState(name) != null) {
            return false; 
        }
        boolean retval = states.add(newState);
        if (startState == null) {
            startState = newState; 
        }
        return retval;
	}

	@Override
	public boolean setFinal(String name) {
        NFAState state = (NFAState) getState(name); 
        if (state != null) {
            finalStates.add(state);
            return true;
        }
        return false;
	}

	@Override
	public boolean setStart(String name) {
		NFAState state = (NFAState) getState(name); 
        if (state != null) {
            startState = state;
            return true;
        }
        return false;
	}

	@Override
	public void addSigma(char symbol) {
		alphabet.add(symbol);
	}

	@Override
	public boolean accepts(String s) {
		Queue<NFAState> currentStates = new LinkedList<NFAState>();
		currentStates.add(startState);
		
		Queue<NFAState> newStates = new LinkedList<NFAState>();
        if (s != "e") {
			for (char symbol : s.toCharArray()) {
				if (!alphabet.contains(symbol)) {
					return false;
				}

				while (!currentStates.isEmpty()) {
					Set<NFAState> eClose = eClosure(currentStates.peek());
					eClose.removeAll(currentStates);
					currentStates.addAll(eClose);
					newStates.addAll(getToState(currentStates.remove(), symbol));
				}
				currentStates.addAll(newStates);
			} 
		}
		for (NFAState state: currentStates) {
			Set<NFAState> eClose = eClosure(state);
			eClose.removeAll(newStates);
			newStates.addAll(eClose);
			
		}
		
        newStates.removeAll(currentStates);
        currentStates.addAll(newStates);
        
        boolean retval = false;
        
        for (NFAState finalState : finalStates) {
        	if (currentStates.contains(finalState)) retval = true;
        }

        return retval;
	}

	@Override
	public Set<Character> getSigma() {
		return alphabet;
	}

	@Override
	public NFAState getState(String name) {
		NFAState retval = null;
    	for(NFAState state : states) {
    		if (state.getName() == name){
    			retval = state;
    		}
    	}
    	return retval;
	}

	@Override
	public boolean isFinal(String name) {
		for(NFAState state : finalStates) {
    		if (state.getName() == name){
    			return true;
    		}
    	}
    	return false;
	}

	@Override
	public boolean isStart(String name) {
		return startState.getName().equals(name);
	}

	@Override
	public int maxCopies(String s) {
		Queue<NFAState> currentStates = new LinkedList<NFAState>();
		currentStates.add(startState);
		int retval = 0;
		
		Queue<NFAState> newStates = new LinkedList<NFAState>();
        if (s != "e") {
			for (char symbol : s.toCharArray()) {
				if (!alphabet.contains(symbol)) {
					return 0;
				}

				while (!currentStates.isEmpty()) {
					Set<NFAState> eClose = eClosure(currentStates.peek());
					eClose.remove(currentStates.peek());
					currentStates.addAll(eClose);
					retval = Math.max(retval, currentStates.size());
					newStates.addAll(getToState(currentStates.remove(), symbol));
				}
				currentStates.addAll(newStates);
			}
        }
			for (NFAState state : currentStates) {
				Set<NFAState> eClose = eClosure(state);
				eClose.removeAll(newStates);
				newStates.addAll(eClose);

			} 
		newStates.removeAll(currentStates);
        currentStates.addAll(newStates);
        retval = Math.max(retval, currentStates.size());
        
        
//        for (NFAState finalState : finalStates) {
//        	if (currentStates.contains(finalState)) retval = 0;
//        }

        return retval;
	}

	@Override
	public boolean addTransition(String fromState, Set<String> toStates, char onSymb) {
		NFAState from = (NFAState) getState(fromState);
        Set<NFAState> to = new LinkedHashSet<NFAState>();
        for (String toString : toStates) {
        	if(getState(toString) == null) return false; //fast exit if state is not found in machine
        	to.add((NFAState) getState(toString));
		}

        if (from != null && !to.isEmpty() && (alphabet.contains(onSymb) || onSymb == 'e')) {
            if (!transitions.containsKey(from)) {
                transitions.put(from, new LinkedHashMap<>());
            }
            if(transitions.get(from).containsKey(onSymb)) {
            	transitions.get(from).get(onSymb).addAll(to);
            }else {
            	transitions.get(from).put(onSymb, to);
            }
            from.setTransitions(transitions.get(from));
            return true;
        }
        return false;
	}

	@Override
	public boolean isDFA() {
		for (NFAState state : states) {
			for(Entry<Character, Set<NFAState>> E : transitions.get(state).entrySet()) {
				if(E.getKey() == 'e' || E.getValue().size() > 1) return false;
			}
		}
		return true;
	}

    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        if (!states.contains(from)) {
            return null;
        }
        Map<Character, Set<NFAState>> thing = transitions.get(from);
        if (thing == null) {
            return new HashSet<>(); // Create an empty HashSet
        }
        Set<NFAState> retval = thing.get(onSymb);
        if (retval == null) {
            return new HashSet<>(); // Create an empty HashSet
        }
        return retval;
    }    

	@Override
	public Set<NFAState> eClosure(NFAState s) {
		if(!states.contains(s)) {
			return null;
		}
		Set<NFAState> retval = new LinkedHashSet<NFAState>();
		Stack<NFAState> stack = new Stack<NFAState>();
		stack.addAll(getToState(s, 'e'));
		retval.add(s);
		retval.addAll(stack);
//		for (NFAState state: stack) {
//			for (NFAState stackState : eClosure(state)) {
//				if(!retval.contains(stackState)) {
//					stack.add(stackState);
//					retval.add(stackState);
//				}
//			}
//			stack.pop();
//		}
		while(!stack.isEmpty()){
			for (NFAState stackState : eClosure(stack.pop())) {
				if(!retval.contains(stackState)) {
					stack.add(stackState);
					retval.add(stackState);
				}
			}
		}
		return retval;
	}
	

}
