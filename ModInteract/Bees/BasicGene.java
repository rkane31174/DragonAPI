/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;

public abstract class BasicGene implements IAllele {

	private final String uid;
	public final String name;
	public final IChromosomeType geneType;

	public BasicGene(String uid, String name, IChromosomeType type) {
		this.uid = uid;
		this.name = name;
		geneType = type;
		this.preInit();
		AlleleManager.alleleRegistry.registerAllele(this, type);
	}

	protected void preInit() {

	}

	@Override
	public final String getUID() {
		return uid;
	}

	@Override
	public boolean isDominant() {
		return true;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getUnlocalizedName() {
		return uid;
	}

}
