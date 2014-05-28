/* 
 * Copyright 2014 Filip Krumpe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package de.krumpefp.gameoflife;

public class Plant {
	
	// ---- class constants ----
	private final float effect = 0.05f;
	private final int dieConstant = 60;
	
	private int energy;

	// ---- constructors ----
	public Plant() {
		this.energy = 64;
	}

	// ---- public functions ----
	/**
	 * Output: 0: plant will sow new 1: plant will survive 2 plant will die
	 * fields
	 * 
	 * @param surroundingEnergy
	 * @return coding of the next action
	 */
	public int getAction() {
		if (this.energy > 200) {
			return 0;
		} else if (this.energy < dieConstant) {
			return 2;
		} else {
			return 1;
		}
	}

	/**
	 * Reduce the energy of a plant f.e. when the plant is eaten by an animal
	 * 
	 * @param energy
	 */
	public void drainEnergy(float energy) {
		this.energy -= energy;
	}

	public int getEnergy() {
		return this.energy;
	}

	public void grow(int[] surrounding) {

		assert (surrounding.length == 9) : "[ERROR] Surrounding has size != 9";

		float sum = 0;
		for (int i = 0; i < surrounding.length; i++) {			
			sum += surrounding[i] * effect;// / (energy * 0.01);
		}
		
		this.energy += sum;
	}

	public int transferEnergy(float plantEnergyFactor) {
		int result = (int) (plantEnergyFactor * this.energy);
		
		this.energy -= (int) (plantEnergyFactor * this.energy);
		
		return result;
	}
}
