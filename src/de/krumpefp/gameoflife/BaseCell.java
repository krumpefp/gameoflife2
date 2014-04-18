package de.krumpefp.gameoflife;

import java.util.ArrayList;
import java.util.List;

public class BaseCell {
	
	//		---- class constants ----
	private final float energyFactorSurrounding = 0.05f;
	private final float energyFactorDirect = 0.01f;
	private final float plantEnergyFactor = 1;
	private final float animalEnergyRecovery = 0.5f;
	
	//		---- class data ----
	private int energy;
	
	private List<Animal> habitants;
	private Plant vegetation;
	
	//		---- constructor ----
	
	public BaseCell(int seed) {
		this.energy = 64;
		
		this.habitants = new ArrayList<>();
		
		switch (seed) {
			case 0:
				this.vegetation = null;
				break;
			case 1:
				this.vegetation = new Plant();
				break;
			case 2:
				this.habitants.add(new Animal());
				this.vegetation = null;
				break;
			case 3:
				this.habitants.add(new Animal());
				this.vegetation = new Plant();
				break;
		}
	}
	
	//		---- public functions ----
	
	public void addHabitant (Animal animal) {
		this.habitants.add(animal);
	}
	
	public void erasePlant() {
		this.vegetation = null;
	}
	
	public void feedAnimal(int number, int[] surrounding) {
		assert (this.habitants.size() > number) : "[ERROR] Number is larger than size!";
		
		this.habitants.get(number).feed(surrounding);
	}
	
	/**
	 * get a code that indicates the status of the field:
	 * 00: contains nothing
	 * 01: contains a habitant
	 * 10: contains vegetation
	 * 11: contains vegetation and a habitant
	 * @return
	 */
	public int[] getCellStatus() {
		int[] cellStatus;
		
		/*if (this.habitant != null && this.vegetation != null) {
			cellStatus = new int[] {0, 0, 0};
		} else */if (this.habitants.size() > 0) {
			cellStatus = new int[] { 64 + this.habitants.get(0).getStatus() * 3/4, 0, 0 };
		} else if (this.vegetation != null) {
			cellStatus = new int[] { 0, 64 + this.vegetation.getEnergy() * 3/4, 0 };
		} else {
			cellStatus = new int[] {68, 68, 68};
		}
		
		return cellStatus;
	}
	
	public int getAnimalAction(int number, int[] surroundingEnergy) {
		if (this.habitants != null) {
			return this.habitants.get(number).getAction(surroundingEnergy);
		} else {
			return 0;
		}
	}
	
	public int getPlantEnergy() {
	
		int result = 0;
		
		if (this.vegetation != null) {
			result = (int) (this.vegetation.getEnergy());
		}
		
		return result;
	}
	
	public int getEnergy() {
	
		int result = 0;
		
		if (this.vegetation != null) {
			result = (int) (this.vegetation.transferEnergy(this.plantEnergyFactor));
		}
		
		return result;
	}
	
	public int transferPlantEnergy() {
		
		if (this.vegetation == null) {
			return 0;
		} else {
			return this.vegetation.transferEnergy(plantEnergyFactor);
		}
	}
	
	public int transferEnergy(boolean isSurrounding) {
		float factor;
		
		if (isSurrounding) {
			factor = this.energyFactorSurrounding;
		} else {
			factor = this.energyFactorDirect;
		}
		
		int result = (int) (this.energy * factor);
		
		this.energy -= this.energy * factor;
		
		return result;
	}
	
	public void growPlant(int[] surrounding) {
		if (this.vegetation != null) {
			this.vegetation.grow(surrounding);
		}
	}
	
	public boolean hasVegetation() {
		if (this.vegetation != null) {
			return true;
		}
		
		return false;
	}
	
	public void nextRound () {
		this.energy += (250 - energy) / 40;
	}
	
	public void seedVegetation () {
		if (energy >= 64) {
			this.vegetation = new Plant();
		}
	}
	
	//		---- getters and setters ----
	public Animal moveHabitant(int number) {
		Animal temp = this.habitants.get(number);
		
		this.habitants.remove(number);
		
		return temp;
	}
	
	public int getVegetationAction() {
		if (this.vegetation == null) {
			return 1;
		} else {
			return this.vegetation.getAction();
		}
	}

	public int hasHabitant() {
		return this.habitants.size();
	}

	public void removeHabitant(int number) {
		this.energy += 64 + this.habitants.get(number).getEnergy() * this.animalEnergyRecovery;
		
		this.habitants.remove(number);
	}

	public void removeVegetation() {
		this.vegetation = null;
	}
	
	//		---- private functions ----
}
