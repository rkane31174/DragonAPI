/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class ChancedOutputList {

	private final ItemHashMap<Float> data = new ItemHashMap().enableNBT();

	private boolean modifiable = true;
	private final boolean allowOverflow;

	public ChancedOutputList(boolean allowOver100) {
		allowOverflow = false;
	}

	public ChancedOutputList(boolean allowOver100, ItemHashMap<Float> output) {
		this(allowOver100);
		for (ItemStack is : output.keySet()) {
			data.put(is.copy(), output.get(is));
		}
	}

	public ChancedOutputList(boolean allowOver100, ItemStack[] items, float... chances) {
		this(allowOver100);
		for (int i = 0; i < items.length; i++) {
			data.put(items[i].copy(), chances[i]);
		}
	}

	/** Chances are in percentages, so 60% is not 0.6, but 60.0 */
	public ChancedOutputList addItem(ItemStack is, float chance) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		data.put(is, chance);
		return this;
	}

	public Float removeItem(ItemStack is) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		return data.remove(is);
	}

	public ChancedOutputList addItems(ArrayList<ItemStack> li, float chance) {
		for (ItemStack is : li)
			this.addItem(is, chance);
		return this;
	}

	public Collection<ItemStack> keySet() {
		Collection<ItemStack> c = new ArrayList();
		for (ItemStack is : data.keySet())
			c.add(is.copy());
		return c;
	}

	public float getItemChance(ItemStack is) {
		Float get = data.get(is);
		return get != null ? allowOverflow ? get.floatValue() : MathHelper.clamp_float(get.floatValue(), 0, 100) : 0;
	}

	public float getNormalizedItemChance(ItemStack is) {
		float f = this.getItemChance(is);
		return Math.min(1, f/100F);
	}

	public ArrayList<ItemStack> getAllWithChance(float chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			float c = data.get(key);
			if (c == chance)
				li.add(key.copy());
		}
		return li;
	}

	public ArrayList<ItemStack> getAllWithAtLeastChance(float chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			float c = data.get(key);
			if (c >= chance)
				li.add(key.copy());
		}
		return li;
	}

	public ArrayList<ItemStack> calculate() {
		return this.calculate(1);
	}

	public ArrayList<ItemStack> calculate(double factor) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			float c = data.get(key);
			double ch = factor*c/100D;
			if (allowOverflow) {
				while (ch >= 1) {
					ch -= 1;
					li.add(key.copy());
				}
			}
			if (ch >= 1 || ReikaRandomHelper.doWithChance(ch)) // /100 to force all into 0-1 range
				li.add(key.copy());
		}
		return li;
	}

	public ChancedOutputList copy() {
		return new ChancedOutputList(allowOverflow, data);
	}

	public ChancedOutputList lock() {
		modifiable = false;
		return this;
	}

	@Override
	public String toString() {
		return data.toString();
	}
	/*
	public void multiplyChances(float f) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		for (ItemStack is : data.keySet()) {
			float f2 = MathHelper.clamp_float(f*data.get(is), 0, 100);
			data.put(is, f2);
		}
	}

	/** Raises chances by the given power. *//*
	public void powerChances(double p) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		for (ItemStack is : data.keySet()) {
			float cur = data.get(is)/100F;
			float pow = 100F*(float)Math.pow(cur, 1D/p);
			float next = MathHelper.clamp_float(pow, 0, 100);
			data.put(is, next);
		}
	}
	 */

	public void manipulateChances(ChanceManipulator cm) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		for (ItemStack is : data.keySet()) {
			float c = cm.getChance(data.get(is));
			if (!allowOverflow)
				c = MathHelper.clamp_float(c, 0, 100);
			data.put(is, c);
		}
	}

	public static interface ChanceManipulator {

		public float getChance(float original);

	}

	public static class ChanceMultiplier implements ChanceManipulator {

		private final float factor;

		public ChanceMultiplier(float factor) {
			this.factor = factor;
		}

		@Override
		public float getChance(float original) {
			return original*factor;
		}

	}

	public static class ChanceExponentiator implements ChanceManipulator {

		private final double power;

		public ChanceExponentiator(double power) {
			this.power = power;
		}

		@Override
		public float getChance(float original) {
			double p = original/100D;
			double num = Math.pow(p, 1D/power);
			return (float)(100*num);
		}

	}

	public static ChancedOutputList parseFromArray(boolean allowOver100, Object[] arr) {
		if (arr.length%2 != 0)
			throw new MisuseException("Every item must have a specified chance!");
		ChancedOutputList c = new ChancedOutputList(allowOver100);
		for (int i = 0; i < arr.length; i += 2) {
			ItemStack is = ReikaItemHelper.parseItem(arr[i]);
			if (is != null) {
				Object chance = arr[i+1];
				if (chance instanceof Integer)
					chance = new Float((Integer)chance);
				c.addItem(is, (Float)chance);
			}
		}
		return c;
	}

}
