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

import java.util.Random;

public class Predator {
	// ---- class constants ----
	private final float foodEffect = 0.5f;
	private final int birthThreshold = 2000;
	private final int energyDrain = 2;
	private final int radius = 5;
	
	// ---- class data ----
	private int age;
	private int energy;
	private Random rand;
	
	// predator state: 1: waiting, 2: moving top, 3: down, 4: left, 5: right
	private int state;
	private int counter;
	
	
	// ---- constructors ----
	public Predator() {
		this.energy = 1000;
		rand = new Random();
	}
	
	// ---- public functions ----
	public void feed(int energy) {
		this.energy += energy * foodEffect;
		
		this.state = rand.nextInt(4) + 2;
		counter = 0;
	}
	
	/**
	 * get the action of the Predator:
	 * 0: die
	 * 1: dupplicate
	 * 2: wait
	 * 32: move top
	 * 33: move down
	 * 34: move left
	 * 35: move right
	 * @return
	 */
	public int getAction() {
		age++;
		this.energy -= energyDrain;
		
		if (this.age > 1000 || this.energy < 0) {
			return 0;
		} else if (this.energy > birthThreshold) {
			this.energy = this.energy / 2;
			return 1;
		} else if (this.state != 1 && counter < 10) {
			counter++;
			return 30 + state;
		} else {
			return 2;
		}
		
	}
	
	public int getStatus() {
		return this.energy / (255 / this.birthThreshold + 1);
	}
	
	public int getEnergy() {
		return this.energy;
	}
	
	public int getRadius() {
		return this.radius;
	}
}
