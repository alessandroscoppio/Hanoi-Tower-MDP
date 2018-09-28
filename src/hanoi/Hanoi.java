package hanoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Hanoi {

    private List<int[]> actions = new ArrayList<>();
    private State[] states;
    private List<List<State>> landingStates;
    private int[] rewards;
	private int[] originalRewards;
    private double epsilon = 2.220446049250313e-16;
    private double discountFactor = 0.9;

    public void prepare() {
        StateGenerator stateGenerator = new StateGenerator();
        stateGenerator.generateHardCodedStates();
        states = stateGenerator.getStateSpace();

        generateActions();
	    generateOriginalRewards();
	    generateLandingStates();
    }

    public void executePolicyIteration() {

    }

    public void executeValueIteration() {

        double[] previousUtility = new double[12];
        double[] nextUtility = new double[12];
        int[] policies = new int[12];
        double delta;

        do {
            delta = 0;
            for (int i = 0; i < states.length; i++) {
                List<Integer> possibleActions = getPossibleActions(states[i]);
                double maxUtility = getRewards(states[i], actions.get(possibleActions.get(0))) + discountFactor
                        * summaryOfProbabilitiesTimesUtility(states[i], actions.get(possibleActions.get(0)), previousUtility);
                int maxActionIndex = possibleActions.get(0);

                for (int j = 1; j < possibleActions.size(); j++) {

                    double currentutility = getRewards(states[i], actions.get(possibleActions.get(j))) + discountFactor
                            * summaryOfProbabilitiesTimesUtility(states[i], actions.get(possibleActions.get(j)), previousUtility);
                    if (currentutility > maxUtility) {
                        maxUtility = currentutility;
                        maxActionIndex = possibleActions.get(j);
                    }
                }
                policies[i] = maxActionIndex;
                nextUtility[i] = maxUtility;
                System.out.println("current delta: " + delta);

                if (Math.abs(nextUtility[i] - previousUtility[i]) > delta) {
                    delta = Math.abs(nextUtility[i] - previousUtility[i]);
                }

            }

            previousUtility = nextUtility.clone();


        } while (delta <= epsilon);
        System.out.println("FINAL POLICIES: ");
        for (int i = 0; i < policies.length; i++) {
            System.out.println(policies[i]);

        }
    }

    public double summaryOfProbabilitiesTimesUtility(State state, int[] action, double[] utilities) {
        double utilitySum = 0;
        List<State> possibleStates = getPossibleNextStatesFromState(state, action);

        for (State s : possibleStates) {
            utilitySum += transitionFunction(state, action, s) * utilities[getIndexOfState(s)];
        }
        return utilitySum;
    }

    public int getIndexOfState(State state) {
        for (int i = 0; i < states.length; i++) {
            if (state.isSameState(states[i]))
                return i;
        }
        return -1;
    }

    public double getRewards(State currentState, int[] actionToPerform) {
        List<State> possibleLandingStates = getPossibleNextStatesFromState(currentState, null);

        double cumulativeReward = 0;
        for (State state : possibleLandingStates) {
            cumulativeReward += transitionFunction(currentState, actionToPerform, state)
                    * originalRewards[getIndexOfState(state)];
        }
        return cumulativeReward;
    }

    /**
     * Returns the probability of going from State source to State target using
     * action given
     *
     * @param source
     * @param action
     * @param target
     * @return Double
     */
    private double transitionFunction(State source, int[] action, State target) {
        if (target.pins[action[1] - 1].size() == source.pins[action[1] - 1].size())
            return 0.1;
        else
            return 0.9;
    }

    /**
     * Finds all the possible landing states for the state given by executing all
     * actions available Returns a list of the possible states
     *
     * @param state
     * @return List
     */
    private List<State> getPossibleNextStatesFromState(State state, int[] currentAction) {
        List<int[]> currentActions = new ArrayList<>();
        if (currentAction != null) {
            currentActions.add(currentAction);
        } else {
            currentActions = this.actions;
        }

        List<State> possibleStates = new ArrayList<>();

        for (int[] action : currentActions) {
            //TODO: let performAction return not only the state we should end, but also the wrong one,
            //in case there was an error and he fucked up
            List<State> states = performAction(state, action);

            if (!states.isEmpty()) {
                for (State currentState : states) {
                    if (states.get(0).isSameState(currentState) ||
		                    states.get(1).isSameState(currentState)) {
	                    if (!possibleStates.contains(currentState))
		                    possibleStates.add(currentState);
	                    break;
                    }
                }
            }

        }

        return possibleStates;
    }

    /**
     * Perform action on State and return the newState that is being produced. If
     * the action is not a possible one return null
     *
     * @param state
     * @param action
     * @return List of states
     */
    private List<State> performAction(State state, int[] action) {
        List<State> landingStates = new ArrayList<>();
        Stack[] pinsCopy = state.pins.clone();
        State successState = new State(pinsCopy);
        int tempValue;
        if (successState.pins[action[0] - 1].isEmpty())
            return landingStates; //this is empty. So better than null
        else {
            tempValue = (int) successState.pins[action[0] - 1].pop();
            successState.pins[action[1] - 1].push(tempValue);
            landingStates.add(successState);

//            if (shouldReturnFailedStates) {
//	            State failState = new State(pinsCopy);
//	            int[] failAction = getFailActionFromAction(action);
//	            tempValue = (int) failState.pins[failAction[0] - 1].pop();
//	            failState.pins[failAction[1] - 1].push(tempValue);
//	            landingStates.add(failState);
//            }
        }

        return landingStates;
    }

    /**
     * Generates an array containing all of the possible actions
     */
    private void generateActions() {

        this.actions.add(new int[]{1, 2});

        this.actions.add(new int[]{1, 3});

        this.actions.add(new int[]{2, 1});

        this.actions.add(new int[]{2, 3});

        this.actions.add(new int[]{3, 1});

        this.actions.add(new int[]{3, 2});

    }

    private List<Integer> getPossibleActions(State state) {
        List<Integer> possibleActions = new ArrayList();
        for (int i = 0; i < actions.size(); i++) {
            if (!performAction(state, actions.get(i)).isEmpty())
                possibleActions.add(i);
        }

        return possibleActions;
    }

    /**
     * Prints the layout of disks in each of the states generated Mostly used for
     * debugging
     */
    private void printStatesRepresentation() {
        for (int i = 0; i < states.length; i++) {
            states[i].printStateInTerminal();
        }
    }

    private int[] getFailActionFromAction(int[] successAction) {
    	for (int i = 0; i < actions.size(); i++) {
    		//If the action that we are iterating at the moment has the same origin pin but at the same
		    //time not the same target pin, then this is the action you need to perform to reach the failed state
    		if (actions.get(i)[0] == successAction[0] && actions.get(i)[1] != successAction[1]) {
    			return actions.get(i);
		    }
	    }

	    return null;
    }

    private void generateOriginalRewards() {
	    originalRewards = new int[12];
	    for (int i = 0; i < 12; i++) {
		    if (i == 5)
			    originalRewards[i] = 100;
		    else if (i == 3 || i == 7 || i == 10)
			    originalRewards[i] = -10;
		    else
			    originalRewards[i] = -1;
	    }
    }

    private void generateLandingStates() {
	    landingStates = new ArrayList<>();
	    for (int i = 0; i < states.length; i++) {
		    landingStates.add(getPossibleNextStatesFromState(states[i], null));
	    }
    }
}
