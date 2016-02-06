/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class WeightedRandom<V> {

	private static final Random r = new Random();

	private final HashMap<V, Double> data = new HashMap();
	private double maxWeight = 0;
	private double weightSum;

	public double addEntry(V obj, double weight) {
		data.put(obj, weight);
		this.weightSum += weight;
		this.maxWeight = Math.max(this.maxWeight, weight);
		return this.weightSum;
	}

	public double remove(V val) {
		double ret = data.remove(val);
		this.weightSum -= ret;
		return ret;
	}

	public V getRandomEntry() {
		double d = r.nextDouble()*this.weightSum;
		double p = 0;
		for (V obj : data.keySet()) {
			p += data.get(obj);
			if (d <= p) {
				return obj;
			}
		}
		return null;
	}

	public V getRandomEntry(V fallback, double wt) {
		double sum = this.weightSum+wt;
		double d = r.nextDouble()*sum;
		double p = 0;
		for (V obj : data.keySet()) {
			p += data.get(obj);
			if (d <= p) {
				return obj;
			}
		}
		return fallback;
	}

	public double getWeight(V obj) {
		return data.get(obj);
	}

	public double getMaxWeight() {
		return this.maxWeight;
	}

	public double getTotalWeight() {
		return this.weightSum;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public int size() {
		return data.size();
	}

	public boolean hasEntry(V obj) {
		return data.containsKey(obj);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public static class InvertedWeightedRandom<V> {
		private final NavigableMap<Double, V> data = new TreeMap<Double, V>();
		private double weightSum;

		public void addEntry(double weight, V result) {
			weightSum += weight;
			data.put(weightSum, result);
		}

		public V getRandomEntry() {
			double value = r.nextDouble()*this.weightSum;
			//ReikaJavaLibrary.pConsole(value+" of "+this.data.toString());
			return data.ceilingEntry(value).getValue();
		}

		public boolean isEmpty() {
			return data.isEmpty();
		}

		public int size() {
			return data.size();
		}

		@Override
		public String toString() {
			return data.toString();
		}
	}

}
