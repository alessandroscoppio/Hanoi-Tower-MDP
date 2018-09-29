package hanoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Jama.Matrix;

public class Hanoi {

	private static final int STATES_NUMBER = 12;
	private static final int ACTIONS_NUMBER = 6;
	private static final double SMALLEST_DOUBLE_VALUE = -Double.MAX_VALUE;
	private static final double DISCOUNT_FACTOR = 0.9;
	private static final double EPSILON = 2.220446049250313e-16;

	private double[][][] transitionFunction;
	private double[][][] rewardFunction;

	public void prepare() {
		generateTransitionFunction();
		generateRewardFunction();
	}

	public void policyIteration() {
		int iteration = 0;
		double[] utilities;
		boolean unchanged;
		long startTime = System.currentTimeMillis();

		int[] policies = getRandomPolicy();

		do {
			utilities = calculateUtilityFromPolicy(policies);
			unchanged = true;

			for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {
				//Get the list of a prime actions for this state
				List<Integer> possibleActionsGivenState = getPossibleActions(stateIdx);
				double maxUtility = SMALLEST_DOUBLE_VALUE; //Default to the lowest value possible
				int maxUtilityActionIdx = -1;

				for (int actionIdx : possibleActionsGivenState) {
					double currentUtility = getRewardFunction(stateIdx, actionIdx) + DISCOUNT_FACTOR * getSumTransitionTimesUtility(stateIdx, actionIdx, utilities);

					if (currentUtility > maxUtility) {
						maxUtility = currentUtility;
						maxUtilityActionIdx = actionIdx;
					}
				}

				if (maxUtility != SMALLEST_DOUBLE_VALUE && maxUtilityActionIdx != -1) {
					if (policies[stateIdx] != maxUtilityActionIdx) {
						policies[stateIdx] = maxUtilityActionIdx;
						unchanged = false;
					}
				}
			}

			iteration++;
		} while (!unchanged);
		long endTime = System.currentTimeMillis();
		printStats(iteration, endTime - startTime, "Policy Iteration: \n", utilities, policies);
	}

	public void valueIteration() {
		//Counter Delta and Start time of execution
		int iteration = 0;
		double delta;
		List<Double> accuracy = new ArrayList<>();
		long startTime = System.currentTimeMillis();

		//Initiate Vectors
		int[] policies = new int[STATES_NUMBER];
		double[] previousUtility = new double[STATES_NUMBER];
		double[] nextUtility = new double[STATES_NUMBER];

		//Repeat until delta is smaller than epsilon
		do {
			delta = 0;

			for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {

				List<Integer> possibleActions = getPossibleActions(stateIdx);
				double[] stateUtility = new double[possibleActions.size()];
				int stateUtilityIdx = 0;
				double maxUtility = SMALLEST_DOUBLE_VALUE;
				int maxUtilityActionIdx = -1;
				for (int actionIdx : possibleActions) {
					stateUtility[stateUtilityIdx] = getRewardFunction(stateIdx, actionIdx) + DISCOUNT_FACTOR * getSumTransitionTimesUtility(stateIdx, actionIdx, previousUtility);
					if (stateUtility[stateUtilityIdx] > maxUtility) {
						maxUtility = stateUtility[stateUtilityIdx];
						maxUtilityActionIdx = actionIdx;
					}
					stateUtilityIdx++;
				}
				if (stateUtilityIdx != 0) {
					nextUtility[stateIdx] = maxUtility;
					policies[stateIdx] = maxUtilityActionIdx;
				}

				if (Math.abs(nextUtility[stateIdx] - previousUtility[stateIdx]) > delta) {
					delta = Math.abs(nextUtility[stateIdx] - previousUtility[stateIdx]);
				}
			}

			accuracy.add(delta);

			System.arraycopy(nextUtility, 0, previousUtility, 0, STATES_NUMBER);
			iteration++;
		} while (delta > EPSILON);

		long endTime = System.currentTimeMillis();
		printStats(iteration, endTime - startTime, "Value Iteration: \n", nextUtility, policies);
	}

	private double[] calculateUtilityFromPolicy(int[] policies) {
		double[][] utilityMatrix = getIdentityMatrix(STATES_NUMBER);
		double[] rewards = new double[STATES_NUMBER];
		double[] utilities;
		for (int row = 0; row < STATES_NUMBER; row++) {
			for (int col = 0; col < STATES_NUMBER; col++) {
				utilityMatrix[row][col] += -(DISCOUNT_FACTOR * getTransitionFunction(row, policies[row], col));
			}
			rewards[row] = getRewardFunction(row, policies[row]);
		}

		Matrix lhs = new Matrix(utilityMatrix);
		Matrix rhs = new Matrix(rewards, STATES_NUMBER);
		Matrix ans = lhs.solve(rhs);

		utilities = ans.getColumnPackedCopy();
		return utilities;
	}

	private double getSumTransitionTimesUtility(int stateIdx, int actionIdx, double[] utility) {
		List<Integer> possibleLandingStates = getPossibleLandingStates(stateIdx, actionIdx);
		double sum = 0;
		for (int landStateIdx : possibleLandingStates) {
			sum += getTransitionFunction(stateIdx, actionIdx, landStateIdx) * utility[landStateIdx];
		}

		return sum;
	}

	private double[][] getIdentityMatrix(int size) {
		double[][] matrix = new double[size][size];
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (row == col) matrix[row][col] = 1;
			}
		}
		return matrix;
	}

	private List<Integer> getPossibleActions(int stateIdx) {
		List<Integer> possibleActions = new ArrayList<>();

		for (int actionIdx = 0; actionIdx < ACTIONS_NUMBER; actionIdx++) {
			if (getPossibleLandingStates(stateIdx, actionIdx).size() != 0) {
				possibleActions.add(actionIdx);
			}
		}

		return possibleActions;
	}

	private List<Integer> getPossibleLandingStates(int stateIdx, int actionIdx) {
		List<Integer> landingStatesIdxs = new ArrayList<>();

		for (int landStateIdx = 0; landStateIdx < STATES_NUMBER; landStateIdx++) {
			if (transitionFunction[stateIdx][actionIdx][landStateIdx] != 0.0) {
				landingStatesIdxs.add(landStateIdx);
			}
		}
		return landingStatesIdxs;

	}

	private double getTransitionFunction(int stateIdx, int actionIdx, int landStateIdx) {
		return transitionFunction[stateIdx][actionIdx][landStateIdx];
	}

	private double getRewardFunction(int stateIdx, int actionIdx, int landStateIdx) {
		return rewardFunction[stateIdx][actionIdx][landStateIdx];
	}

	private double getRewardFunction(int stateIdx, int actionIdx) {
		double reward = 0;
		for (int landStateIdx = 0; landStateIdx < STATES_NUMBER; landStateIdx++) {
			reward += getTransitionFunction(stateIdx, actionIdx, landStateIdx) * getRewardFunction(stateIdx, actionIdx, landStateIdx);
		}
		return reward;
	}

	private int[] getRandomPolicy() {
		Random random = new Random();
		int[] policies = new int[12];
		for (int i = 0; i < STATES_NUMBER; i++) {
			policies[i] = random.nextInt(ACTIONS_NUMBER); // range [0...5]
		}

		return policies;
	}

	private void generateTransitionFunction() {
		transitionFunction = new double[STATES_NUMBER][ACTIONS_NUMBER][STATES_NUMBER];

		// State 1
		transitionFunction[0][0][1] = 0.9; //Action 0 = 12
		transitionFunction[0][0][2] = 0.1;
		transitionFunction[0][1][2] = 0.9; //Action 1 = 13
		transitionFunction[0][1][1] = 0.1;

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
		transitionFunction[2][0][11] = 0.9;
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
		// Absorbing state

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
		transitionFunction[9][2][11] = 0.1;
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

	private void generateRewardFunction() {
		rewardFunction = new double[STATES_NUMBER][ACTIONS_NUMBER][STATES_NUMBER];
		for (int s = 0; s < STATES_NUMBER; s++) {
			for (int a = 0; a < ACTIONS_NUMBER; a++) {
				for (int sp = 0; sp < STATES_NUMBER; sp++) {
					rewardFunction[s][a][sp] = -1;
					if (sp == 5) rewardFunction[s][a][sp] = 100;
					if (sp == 3 || sp == 7 || sp == 10) rewardFunction[s][a][sp] = -10;
				}
			}
		}
	}

	private void printStats(int iteration, long duration, String s, double[] nextUtility, int[] policies) {
		System.out.println(s);
		printPolicyAndUtility(iteration, nextUtility, policies);
		System.out.println("Duration: " + duration + " ms");
		System.out.println("Optimal Policy after iterations: " + iteration);
		System.out.println();
	}

	private void printPolicyAndUtility(int iteration, double[] utility, int[] policy) {
		System.out.println("#\tState\tPolicy\tUtility");
		for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {
			System.out.printf("%d\t%d\t\t%d\t\t%f\n", iteration, (stateIdx + 1), policy[stateIdx], utility[stateIdx]);
		}
		System.out.println();
	}
}