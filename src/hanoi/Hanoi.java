package hanoi;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Jama.Matrix;

public class Hanoi {

    private static final int STATES_NUMBER = 12;

    private List<int[]> actions = new ArrayList<>();
    private double[][][] transitionFunction;
    private double[][][] rewardFunction;


    private int[] originalRewards;
    private double epsilon = 2.220446049250313e-16;

    //TODO: put yellow variables inside of function that are using them
    //Attributes to perform value iteration
    double[] previousUtility = new double[STATES_NUMBER];
    double[] nextUtility = new double[STATES_NUMBER];
    double delta;
    private double discountFactor = 0.9;

    int[] policies = new int[STATES_NUMBER];


    public void prepare() {
        generateActions();
        generateOriginalRewards();
        generateTransitionFunction();
        generateRewardFunction();
    }

    public void policyIteration() {

        //Attributes for policy iteration
        double[] rewards = new double[STATES_NUMBER];
        double[][] utilityMatrix = getIdentityMatrix(STATES_NUMBER);
        double[] utilities = new double[STATES_NUMBER];
        boolean unchanged;

        //Initialize first policy for each state with action 0 (move from pin1 to pin2)
        for (int i = 0; i < STATES_NUMBER; i++) {
            policies[i] = 0;
        }

        do {
            //TODO: refactor extract the calculation of utilities in a function
            for (int row = 0; row < STATES_NUMBER; row++) {
                for (int col = 0; col < STATES_NUMBER; col++) {

                    utilityMatrix[row][col] += -(discountFactor * getTransitionFunction(row, policies[row], col));

                }
            }
            for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {

                rewards[stateIdx] = getRewardFunction(stateIdx, policies[stateIdx]);

            }

            Matrix lhs = new Matrix(utilityMatrix);
            Matrix rhs = new Matrix(rewards, STATES_NUMBER);
            Matrix ans = lhs.solve(rhs);

            utilities = ans.getColumnPackedCopy();
            unchanged = true;

            System.out.print("asdasdasd");
            for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {

                List<Integer> possibleActionsGivenState = getPossibleActions(stateIdx);
                double maxUtility = -Double.MAX_VALUE;
                int maxUtilityActionIdx = -1;

                for (int actionIdx : possibleActionsGivenState) {
                    double currentUtility = getRewardFunction(stateIdx, actionIdx) + discountFactor * getSumTransitionTimesUtility(stateIdx, actionIdx, utilities);

                    if(currentUtility > maxUtility) {
                        maxUtility = currentUtility;
                        maxUtilityActionIdx = actionIdx;
                    }
                }
                if (possibleActionsGivenState.size() != 0) {
                    if (policies[stateIdx] != maxUtilityActionIdx) {
                        policies[stateIdx] = maxUtilityActionIdx;
                        unchanged = false;
                    }
                }
            }

        } while (!unchanged);

        System.out.println("Policies: ");
        for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {
            System.out.println("policy: " + policies[stateIdx] + "   Utility: " + utilities[stateIdx]);
        }


    }

    //generate an identity matrix of size "size"
    private double[][] getIdentityMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (row == col) matrix[row][col] = 1;
            }
        }
        return matrix;
    }

    public void valueIteration() {

        do {
            delta = 0;

            //For state s belonging to state space
            for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {

                List<Integer> possibleActions = getPossibleActions(stateIdx);
                double[] stateUtility = new double[possibleActions.size()];
                int stateUtilityIdx = 0;
                double maxUtility = -100000000;
                int maxUtilityActionIdx = -1;
                for (int actionIdx : possibleActions) {
                    stateUtility[stateUtilityIdx] = getRewardFunction(stateIdx, actionIdx) + discountFactor * getSumTransitionTimesUtility(stateIdx, actionIdx, previousUtility);
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
            System.arraycopy(nextUtility, 0, previousUtility, 0, STATES_NUMBER);

            System.out.println("Current Delta: ");
            System.out.println(delta);
        } while (delta > epsilon);

        System.out.println("Policies: ");
        for (int stateIdx = 0; stateIdx < STATES_NUMBER; stateIdx++) {
            System.out.println("policy: " + policies[stateIdx] + "   Utility: " + nextUtility[stateIdx]);
        }


    }

    private double getSumTransitionTimesUtility(int stateIdx, int actionIdx, double[] utility) {
        List<Integer> possibleLandingStates = getPossibleLandingStates(stateIdx, actionIdx);
        double sum = 0;
        for (int landStateIdx : possibleLandingStates) {
            sum += getTransitionFunction(stateIdx, actionIdx, landStateIdx) * utility[landStateIdx];
        }

        return sum;
    }

    private List<Integer> getPossibleActions(int stateIdx) {
        List<Integer> possibleActions = new ArrayList<>();

        for (int actionIdx = 0; actionIdx < 6; actionIdx++) {
            if (trimArray(getTransitionFunction(stateIdx, actionIdx)).length != 0) {
                possibleActions.add(actionIdx);
            }
        }
        return possibleActions;

    }

    private void generateOriginalRewards() {
        originalRewards = new int[STATES_NUMBER];
        for (int i = 0; i < STATES_NUMBER; i++) {
            if (i == 5)
                originalRewards[i] = 100;
            else if (i == 3 || i == 7 || i == 10)
                originalRewards[i] = -10;
            else
                originalRewards[i] = -1;
        }
    }

    /**
     * Generates an array containing all of the possible actions
     */
    private void generateActions() {

        this.actions.add(new int[]{1, 2});

        this.actions.add(new int[]{1, 3});

        this.actions.add(new int[]{2, 1});

        this.actions.add(new int[]{2, 3});

        this.actions.add(new int[]{3, 1});

        this.actions.add(new int[]{3, 2});

    }

    private void generateTransitionFunction() {
        transitionFunction = new double[STATES_NUMBER][6][STATES_NUMBER];
        //Actions

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

    public double getTransitionFunction(int stateIdx, int actionIdx, int landStateIdx) {
        return transitionFunction[stateIdx][actionIdx][landStateIdx];
    }

    public void generateRewardFunction() {
        rewardFunction = new double[STATES_NUMBER][6][STATES_NUMBER];
        for (int s = 0; s < STATES_NUMBER; s++) {
            for (int a = 0; a < 6; a++) {
                for (int sp = 0; sp < STATES_NUMBER; sp++) {
                    rewardFunction[s][a][sp] = -1;
                    if (sp == 5) rewardFunction[s][a][sp] = 100;
                    if (sp == 3 || sp == 7 || sp == 10) rewardFunction[s][a][sp] = -10;
                }
            }
        }
    }

    public double getRewardFunction(int stateIdx, int actionIdx, int landStateIdx) {
        return rewardFunction[stateIdx][actionIdx][landStateIdx];
    }

    public double getRewardFunction(int stateIdx, int actionIdx) {
        double reward = 0;
        for (int landStateIdx = 0; landStateIdx < STATES_NUMBER; landStateIdx++) {
            reward += getTransitionFunction(stateIdx, actionIdx, landStateIdx) * getRewardFunction(stateIdx, actionIdx, landStateIdx);
        }
        return reward;
    }

    public List<Integer> getPossibleLandingStates(int stateIdx, int actionIdx) {

        List<Integer> landingStatesIdxs = new ArrayList<>();

        for (int landStateIdx = 0; landStateIdx < STATES_NUMBER; landStateIdx++) {
            if (transitionFunction[stateIdx][actionIdx][landStateIdx] != 0.0) {
                landingStatesIdxs.add(landStateIdx);
            }
        }
        return landingStatesIdxs;

    }

    public double[] getTransitionFunction(int stateIdx, int actionIdx) {
        return trimArray(transitionFunction[stateIdx][actionIdx]);
    }

    //TODO: if we have time move "util" function in a util class
    public double[] trimArray(double[] startArray) {

        List<Double> tempValues = new ArrayList<>();
        double[] newArray;
        int sizeOfNewArray = 0;
        for (int idx = 0; idx < startArray.length; idx++) {
            if (startArray[idx] != 0.0) {
                sizeOfNewArray++;
                tempValues.add(startArray[idx]);
            }
        }

        newArray = new double[sizeOfNewArray];
        for (int v = 0; v < tempValues.size(); v++) {

            newArray[v] = tempValues.get(v);
        }

        return newArray;
    }
}
