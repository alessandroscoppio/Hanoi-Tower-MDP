package hanoi;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.ArrayList;
import java.util.List;

public class Plotter {

	private List<Double> state0OptimalValues = new ArrayList<>();
	private List<Double> state0Keys = new ArrayList<>();
	private List<Double> state1OptimalValues = new ArrayList<>();
	private List<Double> state1Keys = new ArrayList<>();
	private List<Double> state2OptimalValues = new ArrayList<>();
	private List<Double> state2Keys = new ArrayList<>();
	private List<Double> state3OptimalValues = new ArrayList<>();
	private List<Double> state3Keys = new ArrayList<>();
	private List<Double> state4OptimalValues = new ArrayList<>();
	private List<Double> state4Keys = new ArrayList<>();
	private List<Double> state6OptimalValues = new ArrayList<>();
	private List<Double> state6Keys = new ArrayList<>();
	private List<Double> state7OptimalValues = new ArrayList<>();
	private List<Double> state7Keys = new ArrayList<>();
	private List<Double> state8OptimalValues = new ArrayList<>();
	private List<Double> state8Keys = new ArrayList<>();
	private List<Double> state9OptimalValues = new ArrayList<>();
	private List<Double> state9Keys = new ArrayList<>();
	private List<Double> state10OptimalValues = new ArrayList<>();
	private List<Double> state10Keys = new ArrayList<>();
	private List<Double> state11OptimalValues = new ArrayList<>();
	private List<Double> state11Keys = new ArrayList<>();

	public void logQValue(int stateIdx, int actionIdx, int iteration, double[][] qValues) {
		if (stateIdx == 0 && actionIdx == 0) {
			state0OptimalValues.add(qValues[stateIdx][actionIdx]);
			state0Keys.add((double) iteration);
		} else if (stateIdx == 1 && actionIdx == 1) {
			state1OptimalValues.add(qValues[stateIdx][actionIdx]);
			state1Keys.add((double) iteration);
		} else if (stateIdx == 2 && actionIdx == 5) {
			state2OptimalValues.add(qValues[stateIdx][actionIdx]);
			state2Keys.add((double) iteration);
		} else if (stateIdx == 3 && actionIdx == 3) {
			state3OptimalValues.add(qValues[stateIdx][actionIdx]);
			state3Keys.add((double) iteration);
		} else if (stateIdx == 4 && actionIdx == 3) {
			state4OptimalValues.add(qValues[stateIdx][actionIdx]);
			state4Keys.add((double) iteration);
		} else if (stateIdx == 6 && actionIdx == 1) {
			state6OptimalValues.add(qValues[stateIdx][actionIdx]);
			state6Keys.add((double) iteration);
		} else if (stateIdx == 7 && actionIdx == 1) {
			state7OptimalValues.add(qValues[stateIdx][actionIdx]);
			state7Keys.add((double) iteration);
		} else if (stateIdx == 8 && actionIdx == 3) {
			state8OptimalValues.add(qValues[stateIdx][actionIdx]);
			state8Keys.add((double) iteration);
		} else if (stateIdx == 9 && actionIdx == 2) {
			state9OptimalValues.add(qValues[stateIdx][actionIdx]);
			state9Keys.add((double) iteration);
		} else if (stateIdx == 10 && actionIdx == 5) {
			state10OptimalValues.add(qValues[stateIdx][actionIdx]);
			state10Keys.add((double) iteration);
		} else if (stateIdx == 11 && actionIdx == 4) {
			state11OptimalValues.add(qValues[stateIdx][actionIdx]);
			state11Keys.add((double) iteration);
		}

	}

	public void plot() {
		XYChart chart = new XYChartBuilder().width(1280).height(720).title("Convergence Speed").xAxisTitle("Iterations").yAxisTitle("Q Values").build();
		chart.addSeries("s1", state0Keys, state0OptimalValues);
		chart.addSeries("s2", state1Keys, state1OptimalValues);
		chart.addSeries("s3", state2Keys, state2OptimalValues);
		chart.addSeries("s4", state3Keys, state3OptimalValues);
		chart.addSeries("s5", state4Keys, state4OptimalValues);
		chart.addSeries("s7", state6Keys, state6OptimalValues);
		chart.addSeries("s8", state7Keys, state7OptimalValues);
		chart.addSeries("s9", state8Keys, state8OptimalValues);
		chart.addSeries("s10", state9Keys, state9OptimalValues);
		chart.addSeries("s11", state10Keys, state10OptimalValues);
		chart.addSeries("s12", state11Keys, state11OptimalValues);
		new SwingWrapper(chart).displayChart();
	}
}
