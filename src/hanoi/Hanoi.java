package hanoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Hanoi {

//	List<State> possibleStates;
//	List<Integer[]> actions = new ArrayList<Integer[]>();
	int[][] actions = new int[6][2];
	private State[] states;
	

	public static void main(String[] args) {
		
		StateGenerator sg = new StateGenerator();
		
		//TODO: make this possible
		states = sg.generateHardCodedStates();

		for (int i = 0; i < states.length; i++) {
			states[i].printState();
		}

	}

	double getProbabilityStateActionState(State source, int[] action, State target) {
		if (target.pins[action[1]].size() == source.pins[action[1]].size())
			return 0.1;
		else
			return 0.9;
		
	}
	State[] getPossibleNextStatesFromState(State state) {
		List<State> possibleStates;
		for (int[] action : this.actions) {
			
			State newState = performAction(state, action);
			
			if (newState != null) {
				for(State state : states) {
					if(state.isSameState(newState)) {
						possibleStates.add(state);
					} 
				}
			}
			
		}
	}
	
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

//	public valueIteration(State[] stateSpace, int[] actionSpace, ) {}

}
