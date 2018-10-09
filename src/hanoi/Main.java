package hanoi;

public class Main {
	public static void main(String[] args) {
		Hanoi hanoi = new Hanoi();
		hanoi.prepare();
//		hanoi.valueIteration();
//		hanoi.policyIteration();
		hanoi.qLearning();
	}
}
