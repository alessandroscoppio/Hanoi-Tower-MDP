package hanoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Hanoi {

	private List<int[]> actions = new ArrayList<>();
	private State[] states;
	private int[] rewards;
	private double epsilon = 2.220446049250313e-16;
	private double discountFactor = 0.9;

	public void prepare() {
		StateGenerator stateGenerator = new StateGenerator();
		stateGenerator.generateHardCodedStates();
		states = stateGenerator.getStateSpace();

		generateActions();
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
			for (int k = 0; k < nextUtility.length; k++) {
				previousUtility[k] = nextUtility[k];
			}

		} while (delta <= epsilon);
		System.out.println("FINAL POLICIES: ");
		for(int i = 0; i < policies.length; i++) {
			System.out.println(policies[i]);

		}
	}

	public double summaryOfProbabilitiesTimesUtility(State state, int[] action, double[] utilities) {
		double utilitySum = 0;
		List<State> possibleStates = getPossibleNextStatesFromState(state, action);
		for (State possibleState : possibleStates) {
			utilitySum += getProbabilityStateActionState(state, action, possibleState) * utilities[getIndexOfState(possibleState)];
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

		int[] originalRewards = new int[12];
		for (int i = 0; i < 12; i++) {
			if (i == 5)
				originalRewards[i] = 100;
			else if (i == 3 || i == 7 || i == 10)
				originalRewards[i] = -10;
			else
				originalRewards[i] = -1;
		}

		List<State> possibleLandingStates = getPossibleNextStatesFromState(currentState, actionToPerform);
		//TODO Each time I execute an action I could end up to another state because I failed. This algorithm things that I always succeed
		double cumulativeReward = 0;
		for (State state : possibleLandingStates) {
			cumulativeReward += getProbabilityStateActionState(currentState, actionToPerform, state)
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
	private double getProbabilityStateActionState(State source, int[] action, State target) {
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

		//TODO This doesn't give me the failure state. And I use this when I calculate the utility.
		for (int[] action : currentActions) {
			State newState = performAction(state, action);

			if (newState != null) {
				for (State currentState : states) {
					if (newState.isSameState(currentState)) {
						if (!possibleStates.contains(currentState))
							possibleStates.add(currentState);
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
	 * @return State or null
	 */
	private State performAction(State state, int[] action) {
		
		Stack[] pinsCopy = state.pins.clone();
		State copy = new State(pinsCopy);
		int tempValue;
		if (copy.pins[action[0] - 1].isEmpty())
			return null;
		else {
			tempValue = (int) copy.pins[action[0] - 1].pop();
			copy.pins[action[1] - 1].push(tempValue);
			return copy;
		}
	}

	/**
	 * Generates an array containing all of the possible actions
	 */
	private void generateActions() {

		this.actions.add(new int[] { 1, 2 });

		this.actions.add(new int[] { 1, 3 });

		this.actions.add(new int[] { 2, 1 });

		this.actions.add(new int[] { 2, 3 });

		this.actions.add(new int[] { 3, 1 });

		this.actions.add(new int[] { 3, 2 });

	}
	
	private List<Integer> getPossibleActions(State state){
		List<Integer> possibleActions = new ArrayList();
		for(int i = 0; i < actions.size(); i++) {
			if(performAction(state, actions.get(i)) != null)
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
}
