package hanoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hanoi {

	private int[][] actions = new int[6][2];
	private State[] states;

	public void prepare() {
		StateGenerator stateGenerator = new StateGenerator();
		stateGenerator.generateHardCodedStates();
		states = stateGenerator.getStateSpace();

		generateActions();
	}

	public void executePolicyIteration() {

	}

	public void executeValueIteration() {
		printStatesRepresentation();
	}

	/**
	 * Returns the probability of going from State source to State target using action given
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
	 * Finds all the possible landing states for the state given
	 * by executing all actions available
	 * Returns a list of the possible states
	 *
	 * @param state
	 * @return List
	 */
	private List<State> getPossibleNextStatesFromState(State state) {
		List<State> possibleStates = new ArrayList<>();

		for (int[] action : this.actions) {
			State newState = performAction(state, action);

			if (newState != null) {
				for (State currentState : states) {
					if (state.isSameState(newState)) {
						possibleStates.add(currentState);
					}
				}
			}

		}

		return possibleStates;
	}

	/**
	 * Perform action on State and return the newState that is being produced.
	 * If the action is not a possible one return null
	 *
	 * @param state
	 * @param action
	 * @return State or null
	 */
	private State performAction(State state, int[] action) {
		//TODO Write the algorithm of performAction
		return null;
	}


	/**
	 * Generates an array containing all of the possible actions
	 */
	private void generateActions() {
		this.actions[0][0] = 1;
		this.actions[0][1] = 2;

		this.actions[1][0] = 1;
		this.actions[1][1] = 3;

		this.actions[2][0] = 2;
		this.actions[2][1] = 1;

		this.actions[3][0] = 2;
		this.actions[3][1] = 3;

		this.actions[4][0] = 3;
		this.actions[4][1] = 1;

		this.actions[5][0] = 3;
		this.actions[5][1] = 2;
	}

	/**
	 * Prints the layout of disks in each of the states generated
	 * Mostly used for debugging
	 */
	private void printStatesRepresentation() {
		for (int i = 0; i < states.length; i++) {
			states[i].printStateInTerminal();
		}
	}
}
