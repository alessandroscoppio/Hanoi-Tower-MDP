package hanoi;

import static hanoi.State.diskA;
import static hanoi.State.diskB;

public class StateGenerator {

	private State[] stateSpace;

//	public StateGenerator() {
//		this.possibleStates = new ArrayList();
//		Stack pinA = new Stack();
//		Stack pinB = new Stack();
//		Stack pinC = new Stack();
//
//		pinA.push(State.d2);
//		pinA.push(State.d1);
//
//		Stack[] pins = { pinA, pinB, pinC };
//
//		startState = new State(pins);
//
//		this.possibleStates.add(startState);
//	}
	
	public StateGenerator() {

	}

//	public void generatePossibleState(State state) {
////		if (this.possibleStates.size() == maxStates) {
////			return; // IF WE DON'T ACTIVATE THIS CODE IT WILL GO INFINITE.
////		}
//		
//		//TODO: last time we debugged the code when creating a new state it was actually adding disk 1 to each new pole.
//		//we should fix it.
//		@SuppressWarnings("rawtypes")
//		Stack[] tempPins = new Stack[3];
//		tempPins[0] = (Stack) state.pin1.clone();
//		tempPins[1] = (Stack) state.pin2.clone();
//		tempPins[2] = (Stack) state.pin3.clone();
//
//		for (int i = 0; i < tempPins.length; i++) {
//
//			if (!tempPins[i].isEmpty()) {
//
//				int topDisk = (int) tempPins[i].pop();
//
//				for (int j = 0; j < tempPins.length; j++) {
//					if (j != i) {
//						tempPins[j].push(topDisk);
//						State newState = new State(tempPins);
//
//						if (!stateExists(newState)) {
//							possibleStates.add(newState);
//							generatePossibleState(newState);
//						}
//					}
//				}
//
//				// restore initial pins iin order to generate valid states
//				tempPins = state.pins;
//			}
//		}
//	}

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
