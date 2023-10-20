package fa.nfa;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fa.State;

public class NFAState extends State {
    private Map<Character, Set<NFAState>> transitions;

    public NFAState(String name) {
        super(name);
        transitions = new HashMap<>();
    }

    // Add a transition for a given symbol
    public void addTransition(char symbol, NFAState nextState) {
        transitions.computeIfAbsent(symbol, k -> new HashSet<>()).add(nextState);
    }

    // Get the set of states reachable by a given symbol
    public Set<NFAState> getTransitions(char symbol) {
        return transitions.getOrDefault(symbol, new HashSet<>());
    }
}
