package hanoi;

import java.util.Stack;

public class State {

	public static final int diskA = 2;
	public static final int diskB = 1;

	public Stack pin1;
	public Stack pin2;
	public Stack pin3;

	public Stack[] pins;

	public State() {
		this.pin1 = new Stack();
		this.pin2 = new Stack();
		this.pin3 = new Stack();

		this.pins = new Stack[]{pin1, pin2, pin3};
	}

	public State(Stack[] pins) {
		this.pin1 = pins[0];
		this.pin2 = pins[1];
		this.pin3 = pins[2];
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
	 * Checks all pins of the given state with this state
	 * for the same configuration
	 * @param currentState
	 * @return boolean
	 */
	public boolean isSameState(State currentState) {
		try {
			for (Stack pin : pins) {
				for (int i = 0; i < pin.size(); i++) {
					if (pin.elementAt(i) != currentState.pins[i].elementAt(i)) {
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