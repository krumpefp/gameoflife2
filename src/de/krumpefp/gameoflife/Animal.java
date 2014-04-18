package de.krumpefp.gameoflife;

import java.util.Random;

public class Animal {
	
	// ---- class constants ----
	private final float effect = 1;
	private final int stepCost = 3;
	private final int birthThreshold = 750;
	
	// the time the agent survives without nutrition
	private int age;
	private int energy;
	
	private Random rand;

	//		---- constructors ----
	public Animal () {
		this.age = 0;
		
		this.energy = 64;
		
		this.rand = new Random();
	}
	
	//		---- public functions ----
	public void feed(int[] surroundingPlantEnergy) {
		
		assert (surroundingPlantEnergy.length != 9) : "[ERROR] Length of SurroundingPlantEnergy != 9";
		
		int sum = 0;
		
		for (int i = 0; i < surroundingPlantEnergy.length; i++) {
			sum += surroundingPlantEnergy[i];
		}
		
		if (sum > 0) {
			int i = 0;
		}
		
		this.energy += sum * effect;
	}

	/**
	 * Get the action of the agent for the next round:
	 * Input is the direct surrounding of the agent coded as follows:
	 * 0: top left
	 * 1: top center
	 * 2: top right
	 * 3: middle left
	 * 4: middle center
	 * 5: middle right
	 * 6: bottom left
	 * 7: bottom center
	 * 8: bottom right
	 * Output
	 * 1: feed
	 * 2: move left
	 * 3: move right
	 * 4: move up
	 * 5: move down
	 * 6: duplicate
	 * 7: die
	 * @param surrounding
	 * @return coding of the next action
	 */
	public int getAction(int[] surroundingPlantEnergy) {
		
		age++;
		
		if (age > 1000) {
			return 7;
		}

		assert (surroundingPlantEnergy.length == 9) : "[ERROR] Surrounding has size != 9";
		
		if (this.energy > birthThreshold) {
			this.energy = this.energy / 4;
			return 6;
		}
		
		int sum = 0;
		
		for (int i = 0; i < surroundingPlantEnergy.length; i++) {
			sum += surroundingPlantEnergy[i];
		}
		
		if (sum > 64) {
			return 1;
		}
		
		// decrease the energy
		this.energy -= stepCost;
		if (this.energy < 0) {
			return 7;
		}
		int result = 2 + rand.nextInt(4);
		
		return result;
	}

	public int getStatus() {
		return this.energy / 3;
	}
	
	public int getEnergy() {
		return this.energy;
	}

	public void addEnergy(int energy) {
		this.energy += energy;
		
		this.age = 0;
	}
}
