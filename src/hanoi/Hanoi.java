package hanoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Hanoi {

    private List<int[]> actions = new ArrayList<>();
    private State[] states;
    private double[][][] transitionFunction;
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
	    generateTransitionFunction();
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

    private double summaryOfProbabilitiesTimesUtility(State state, int[] action, double[] utilities) {
        double utilitySum = 0;
        List<State> possibleStates = getPossibleNextStatesFromState(state, action);

        for (State s : possibleStates) {
            utilitySum += transitionFunction(state, action, s) * utilities[getIndexOfState(s)];
        }
        return utilitySum;
    }

    private int getIndexOfState(State state) {
        for (int i = 0; i < states.length; i++) {
            if (state.isSameState(states[i]))
                return i;
        }
        return -1;
    }

    private double getRewards(State currentState, int[] actionToPerform) {
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
    private double transitionFunction(State source, int action, State target) {
        return transitionFunction[getIndexOfState(source)][action][getIndexOfState(target)];
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
        }

        return landingStates;
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

    private void generateTransitionFunction() {
		transitionFunction = new double[12][6][12];
		//Actions

		// State 1
		transitionFunction[0][0][0] = 0.9; //Action 0 = 12
		transitionFunction[0][0][1] = 0.1;
		transitionFunction[0][1][0] = 0.1; //Action 1 = 13
		transitionFunction[0][1][1] = 0.9;

		// State 2
	    transitionFunction[1][0][3] = 0.9; //Action 0 = 12
	    transitionFunction[1][0][4] = 0.1;
	    transitionFunction[1][1][4] = 0.9; //Action 1 = 13
	    transitionFunction[1][1][3] = 0.1;
	    transitionFunction[1][2][0] = 0.9; //Action 2 = 21
	    transitionFunction[1][2][2] = 0.1;
	    transitionFunction[1][3][2] = 0.9; //Action 3 - 23
	    transitionFunction[1][3][0] = 0.1;

	    //State 3
	    transitionFunction[2][0][3] = 0.9;
	    transitionFunction[2][0][10] = 0.1;
	    transitionFunction[2][1][10] = 0.9;
	    transitionFunction[2][1][11] = 0.1;
	    transitionFunction[2][4][0] = 0.9;
	    transitionFunction[2][4][1] = 0.1;
	    transitionFunction[2][5][1] = 0.9;
	    transitionFunction[2][5][0] = 0.1;

	    //State 4
	    transitionFunction[3][2][1] = 0.9;
	    transitionFunction[3][2][4] = 0.1;
	    transitionFunction[3][3][4] = 0.9;
	    transitionFunction[3][3][1] = 0.1;

	    //State 5
	    transitionFunction[4][3][5] = 0.9;
	    transitionFunction[4][3][6] = 0.1;
	    transitionFunction[4][2][6] = 0.9;
	    transitionFunction[4][2][5] = 0.1;
	    transitionFunction[4][4][1] = 0.9;
	    transitionFunction[4][4][3] = 0.1;
	    transitionFunction[4][5][3] = 0.9;
	    transitionFunction[4][5][1] = 0.1;

	    //State 6
	    transitionFunction[5][4][6] = 0.9;
	    transitionFunction[5][4][4] = 0.1;
	    transitionFunction[5][5][4] = 0.9;
	    transitionFunction[5][5][6] = 0.1;

	    //State 7
	    transitionFunction[6][0][4] = 0.9;
	    transitionFunction[6][0][5] = 0.1;
	    transitionFunction[6][1][5] = 0.9;
	    transitionFunction[6][1][4] = 0.1;
	    transitionFunction[6][4][7] = 0.9;
	    transitionFunction[6][4][8] = 0.1;
	    transitionFunction[6][5][8] = 0.9;
	    transitionFunction[6][5][7] = 0.1;

	    //State 8
	    transitionFunction[7][0][8] = 0.9;
	    transitionFunction[7][0][6] = 0.1;
	    transitionFunction[7][1][6] = 0.9;
	    transitionFunction[7][1][8] = 0.1;

	    //State 9
	    transitionFunction[8][0][9] = 0.9; //Action 0 = 12
	    transitionFunction[8][0][11] = 0.1;
	    transitionFunction[8][1][11] = 0.9; //Action 1 = 13
	    transitionFunction[8][1][9] = 0.1;
	    transitionFunction[8][2][7] = 0.9; //Action 2 = 21
	    transitionFunction[8][2][6] = 0.1;
	    transitionFunction[8][3][6] = 0.9; //Action 3 = 23
	    transitionFunction[8][3][7] = 0.1;

	    //State 10
	    transitionFunction[9][2][8] = 0.9; //Action 2 = 21
	    transitionFunction[9][2][9] = 0.1;
	    transitionFunction[9][3][11] = 0.9; //Action 3 = 23
	    transitionFunction[9][3][8] = 0.1;


	    //State 11
	    transitionFunction[10][4][2] = 0.9; //Action 4 = 31
	    transitionFunction[10][4][11] = 0.1;
	    transitionFunction[10][5][11] = 0.9; //Action 5 = 32
	    transitionFunction[10][5][2] = 0.1;


	    //State 12
	    transitionFunction[11][4][8] = 0.9; //Action 4 = 31
	    transitionFunction[11][4][9] = 0.1;
	    transitionFunction[11][5][9] = 0.9; //Action 5 = 32
	    transitionFunction[11][5][8] = 0.1;
	    transitionFunction[11][2][2] = 0.9; //Action 2 = 21
	    transitionFunction[11][2][10] = 0.1;
	    transitionFunction[11][3][10] = 0.9; //Action 3 - 23
	    transitionFunction[11][3][2] = 0.1;
    }
}
