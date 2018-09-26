package hanoi;

import java.util.Stack;

public class State {

	Stack pin1;
	Stack pin2;
	Stack pin3;
	
//
	Stack[] pins = { pin1, pin2, pin3 };
	public static final int diskA = 2;
	public static final int diskB = 1;

	public State(Stack[] pins) {

		this.pin1 = pins[0];
		this.pin2 = pins[1];
		this.pin3 = pins[2];
	}
	
	public State() {
		this.pin1 = new Stack();
		this.pin2 = new Stack();
		this.pin3 = new Stack();
	}

	public void printState() {
		
		if(pin1.isEmpty())
			System.out.print("|");
		else {
			for(int i = 0; i < pin1.size(); i++) {
				System.out.print(pin1.get(i));
			}
		}
		System.out.print(" ");

		
		if(pin2.isEmpty())
			System.out.print("|");
		else {
			for(int i = 0; i < pin2.size(); i++) {
				System.out.print(pin2.get(i));
			}
		}
		System.out.print(" ");

		
		if(pin3.isEmpty())
			System.out.print("|");
		else {
			for(int i = 0; i < pin3.size(); i++) {
				System.out.print(pin3.get(i));
			}
		}
		System.out.println();
		
	}
	public boolean isSameState(State currentState) {
		try {
			for (int i = 0; i < pin1.size(); i++) {
				if (pin1.elementAt(i) != currentState.pin1.elementAt(i)) {
					return false;
				}
			}
			
			for (int i = 0; i < pin2.size(); i++) {
				if (pin2.elementAt(i) != currentState.pin2.elementAt(i)) {
					return false;
				}
			}
			
			for (int i = 0; i < pin3.size(); i++) {
				if (pin3.elementAt(i) != currentState.pin3.elementAt(i)) {
					return false;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		return true;
	}

}