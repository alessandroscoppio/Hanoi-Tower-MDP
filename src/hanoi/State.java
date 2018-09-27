package hanoi;

import java.util.Stack;

public class State {

	public static final int diskA = 2;
	public static final int diskB = 1;

	public Stack[] pins;

	public State() {

		this.pins = new Stack[3];
		for(int i = 0; i < 3; i++) {
			pins[i] = new Stack();
		}
	}

	public State(Stack[] pins) {
		this.pins = new Stack[3];

		for(int i = 0; i < pins.length; i++) {
			this.pins[i] = new Stack();

			for(int j = 0; j < pins[i].size(); j++) {
				this.pins[i].push(pins[i].get(j));
			}
		}

	}

	/**
	 * Prints state configuration in terminal
	 */
	public void printStateInTerminal() {
		for (Stack pin : pins) {
			if (pin.isEmpty()) {
				System.out.print("|");
			} else {
				for (int i = 0; i < pin.size(); i++) {
					System.out.print(pin.get(i));
				}
			}
			System.out.print(" ");
		}
		System.out.println();

	}

	/**
	 * Checks all pins of the given state with this state for the same configuration
	 * 
	 * @param currentState
	 * @return boolean
	 */
	public boolean isSameState(State currentState) {
		try {
			for (int j = 0; j < pins.length; j++) {
				for (int i = 0; i < pins[j].size(); i++) {
					if (pins[j].elementAt(i) != currentState.pins[j].elementAt(i)) {
						return false;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

		return true;
	}

}