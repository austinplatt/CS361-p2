package fa.nfa;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import fa.State;

public abstract class NFA implements NFAInterface{

    private Set<NFAState> states;
    private Set<Character> alphabet;
    private NFAState startState;
    private Set<NFAState> finalStates;
    private Map<NFAState, Map<Character, Set<NFAState>>> transitions;

    public NFA() {
        states = new HashSet<>();
        alphabet = new HashSet<>();
        finalStates = new HashSet<>();
        transitions = new HashMap<>();
    }

    private NFAState getStateByName(String stateName) {
        for (NFAState state : states) {
            if (state.getName().equals(stateName)) {
                return state;
            }
        }
        return null; // Return null if state not found
    }

    /**
	 * Adds the transition to the NFA's delta data structure
	 * @param fromState is the label of the state where the transition starts
	 * @param toState is the set of labels of the states where the transition ends
	 * @param onSymb is the symbol from the NFA's alphabet.
	 * @return true if successful and false if one of the states don't exist or the symbol in not in the alphabet
	 */
    @Override
    public boolean addTransition(String fromState, Set<String> toStates, char onSymb) {
        if (!states.contains(fromState)) return false;
        for (String stateName : toStates) {
            if (!states.contains(stateName)) return false;
        }

        alphabet.add(onSymb);

        NFAState from = getStateByName(fromState);
        Map<Character, Set<NFAState>> transitionMap = transitions.getOrDefault(from, new HashMap<>());
        Set<NFAState> toStateSet = transitionMap.getOrDefault(onSymb, new HashSet<>());

        for (String stateName : toStates) {
            toStateSet.add(getStateByName(stateName));
        }

        transitionMap.put(onSymb, toStateSet);
        transitions.put(from, transitionMap);
        return true;
    }

    @Override
    public boolean isDFA() {
        for (NFAState state : states) {
            for (char symbol : alphabet) {
                if (!transitions.containsKey(state) || !transitions.get(state).containsKey(symbol)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
	 * Return delta entries
	 * @param from - the source state
	 * @param onSymb - the label of the transition
	 * @return a set of sink states
	 */
    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        return null;
        // Implement the logic to get the set of destination states for a transition
    }

    /**
	 * Traverses all epsilon transitions and determine
	 * what states can be reached from s through e
	 * @param s
	 * @return set of states that can be reached from s on epsilon trans.
	 */
    @Override
    public Set<NFAState> eClosure(NFAState s) {
        Set<NFAState> eClosure = new HashSet<>();
        Stack<NFAState> stack = new Stack<>();
        stack.push(s);
    
        while (!stack.isEmpty()) {
            NFAState state = stack.pop();
            eClosure.add(state);
    
            if (transitions.containsKey(state) && transitions.get(state).containsKey('e')) {
                Set<NFAState> epsilonTransitions = transitions.get(state).get('e');
                for (NFAState epsilonState : epsilonTransitions) {
                    if (!eClosure.contains(epsilonState)) {
                        stack.push(epsilonState);
                    }
                }
            }
        }
    
        return eClosure;
        
    }

    /**
	 * Determines the maximum number of NFA copies
	 * created when processing string s
	 * @param s - the input string
	 * @return - the maximum number of NFA copies created.
	 */
    @Override
    public int maxCopies(String s) {
        int maxCopies = 0;

        NFAState currentState = startState;
        Set<NFAState> currentStates = new HashSet<>();
        currentStates.add(currentState);

        for (char symbol : s.toCharArray()) {
            Set<NFAState> nextStates = new HashSet<>();
            for (NFAState state : currentStates) {
                Set<NFAState> transitions = getToState(state, symbol);
                if (transitions != null) {
                    nextStates.addAll(transitions);
                }
            }
            currentStates = nextStates;
            maxCopies = Math.max(maxCopies, currentStates.size());
        }

        return maxCopies;
        
    }


    @Override
    public Set<Character> getSigma() {
        return alphabet;
        
    }

    @Override
    public boolean addState(String name) {
        NFAState newState = new NFAState(name);
        if (states.contains(newState)) return false;
        states.add(newState);
        return true;
    }

    @Override
    public boolean setFinal(String name) {
        NFAState state = getStateByName(name);
        if (state == null) return false;
        finalStates.add(state);
        return true;
    }

    @Override
    public boolean setStart(String name) {
        NFAState state = getStateByName(name);
        if (state == null) return false;
        startState = state;
        return true;
    }

    @Override
    public void addSigma(char symbol) {
        alphabet.add(symbol);
    }

    @Override
    public boolean accepts(String s) {
        Set<NFAState> currentStates = eClosure(startState);

        for (char symbol : s.toCharArray()) {
            Set<NFAState> nextStateSet = new HashSet<>();
    
            for (NFAState state : currentStates) {
                if (transitions.containsKey(state) && transitions.get(state).containsKey(symbol)) {
                    nextStateSet.addAll(transitions.get(state).get(symbol));
                }
            }
    
            currentStates = new HashSet<>();
            for (NFAState nextState : nextStateSet) {
                currentStates.addAll(eClosure(nextState));
            }
        }
    
        // Check if any of the current states are final states
        for (NFAState state : currentStates) {
            if (finalStates.contains(state)) {
                return true;
            }
        }
    
        return false;
    
    }

    @Override
    public State getState(String name) {
        return getStateByName(name);
    }

    @Override
    public boolean isFinal(String name) {
        NFAState state = getStateByName(name);
        return finalStates.contains(state);
    }

    @Override
    public boolean isStart(String name) {
        NFAState state = getStateByName(name);
        return state.equals(startState);
    }


}
