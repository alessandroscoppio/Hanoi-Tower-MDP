package hanoi;

import static hanoi.State.diskA;
import static hanoi.State.diskB;

public class StateGenerator {

	private State[] stateSpace;
	
	public StateGenerator() {

	}

	public void generateHardCodedStates() {
		stateSpace = new State[12];
		
		//Generate state 1
		stateSpace[0] = new State();
		stateSpace[0].pins[0].push(diskA);
		stateSpace[0].pins[0].push(diskB);	
		
		//Generate state 2
		stateSpace[1] = new State();
		stateSpace[1].pins[0].push(diskA);
		stateSpace[1].pins[1].push(diskB);	

		//Generate state 3
		stateSpace[2] = new State();
		stateSpace[2].pins[0].push(diskA);
		stateSpace[2].pins[2].push(diskB);	
		
		//Generate state 4
		stateSpace[3] = new State();
		stateSpace[3].pins[1].push(diskB);	
		stateSpace[3].pins[1].push(diskA);
		
		//Generate state 5
		stateSpace[4] = new State();
		stateSpace[4].pins[1].push(diskB);	
		stateSpace[4].pins[2].push(diskA);

		
		//Generate state 6
		stateSpace[5] = new State();
		stateSpace[5].pins[2].push(diskA);
		stateSpace[5].pins[2].push(diskB);	
		
		//Generate state 7
		stateSpace[6] = new State();
		stateSpace[6].pins[0].push(diskB);
		stateSpace[6].pins[2].push(diskA);	
		
		//Generate state 8
		stateSpace[7] = new State();
		stateSpace[7].pins[0].push(diskB);
		stateSpace[7].pins[0].push(diskA);	
		
		//Generate state 9
		stateSpace[8] = new State();
		stateSpace[8].pins[0].push(diskB);
		stateSpace[8].pins[1].push(diskA);	
		
		//Generate state 10
		stateSpace[9] = new State();
		stateSpace[9].pins[1].push(diskA);
		stateSpace[9].pins[1].push(diskB);	
		
		//Generate state 11
		stateSpace[10] = new State();
		stateSpace[10].pins[2].push(diskB);
		stateSpace[10].pins[2].push(diskA);	
		
		//Generate state 12
		stateSpace[11] = new State();
		stateSpace[11].pins[1].push(diskA);
		stateSpace[11].pins[2].push(diskB);
	}

	public State[] getStateSpace() {
		return stateSpace;
	}

	/**
	 * Checks if state in stateSpace
	 * @param currentState
	 * @return boolean
	 */
	private boolean stateExists(State currentState) {
		for (State state : stateSpace) {
			if (state.isSameState(currentState)) {
				return true;
			}
		}

		return false;
	}
}
