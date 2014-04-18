package de.krumpefp.gameoflife;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

public class World extends Frame {

	// ---- class constants ----
	private static final int xSpread = 3;
	private static final int ySpread = 3;
	private static final int seedFactor = 5;
	private static final int habitantStart = 150;
	private static final int habitantProbability = 250;
	private static final int plantProbability = 25;
	private static final int predatorProbability = 5000;

	// ---- class data ----

	private int xSize, ySize, cellSize;
	private int xCellCount, yCellCount;
	private long rounds;

	private int refresh;

	private BaseCell[][] newCells;

	private Random randomGenerator;

	// ---- constructors ----

	public World(int cellSize, int refresh) {
		rounds = 0;
		this.refresh = refresh;

		this.xSize = 1915;
		this.ySize = 1052;

		this.cellSize = cellSize;

		this.xCellCount = (xSize / (cellSize + 1));
		this.yCellCount = (ySize / (cellSize + 1));

		this.randomGenerator = new Random();

		// initialize the cells
		int random = this.randomGenerator.nextInt(plantProbability);

		newCells = new BaseCell[xCellCount][yCellCount];

		for (int x = 0; x < xCellCount; x++) {
			for (int y = 0; y < yCellCount; y++) {
				if (random == 0) {
					this.newCells[x][y] = new BaseCell(1);
				} else {
					this.newCells[x][y] = new BaseCell(0);
				}

				random = this.randomGenerator.nextInt(plantProbability);
			}
		}
	}

	// ---- public functions ----

	public void nextRound() {

		this.rounds++;

		for (int x = 0; x < xCellCount; x++) {
			for (int y = 0; y < yCellCount; y++) {
				newCells[x][y].nextRound();
			}
		}

		for (int x = 0; x < xCellCount; x++) {
			for (int y = 0; y < yCellCount; y++) {
				if (newCells[x][y].hasVegetation()) {
					newCells[x][y].growPlant(getSurrounding(x, y));
				}
			}
		}

		for (int x = 0; x < xCellCount; x++) {
			for (int y = 0; y < yCellCount; y++) {
				switch (newCells[x][y].getVegetationAction()) {
				case 0:
					// plant will die and sow new cells
					newCells[x][y].erasePlant();
					for (int j = 0; j < this.seedFactor; j++) {
						this.newCells[scaleX(x - xSpread
								+ this.randomGenerator.nextInt(1 + 2 * xSpread))][scaleY(y
								- ySpread
								+ this.randomGenerator.nextInt(1 + 2 * ySpread))]
								.seedVegetation();
					}
					break;
				case 2:
					newCells[scaleX(x)][scaleY(y)].removeVegetation();
					break;
				default:
				}
			}
		}

		// handle the animals
		for (int x = 0; x < this.xCellCount; x++) {
			for (int y = 0; y < this.yCellCount; y++) {
				if (newCells[x][y].hasHabitant() > 0) {
					int sub = 0;
					
					for (int i=0; i < newCells[x][y].hasHabitant(); i++) {
						int action = newCells[x][y]
								.getAnimalAction(i - sub, getSurroundingPlantEnergy(x, y));

						switch (action) {
						case 1:
							newCells[x][y]
									.feedAnimal(i - sub, transferSurroundingPlantEnergy(x, y));
							break;
						case 2:
							newCells[scaleX(x - 1)][y].addHabitant(newCells[x][y]
									.moveHabitant(i - sub));
							sub++;
							break;
						case 3:
							newCells[scaleX(x + 1)][y].addHabitant(newCells[x][y]
									.moveHabitant(i - sub));
							sub++;
							break;
						case 4:
							newCells[x][scaleY(y - 1)].addHabitant(newCells[x][y]
									.moveHabitant(i - sub));
							sub++;
							break;
						case 5:
							newCells[x][scaleY(y + 1)].addHabitant(newCells[x][y]
									.moveHabitant(i - sub));
							sub++;
							break;
						case 6:
							int move = randomGenerator.nextInt(4);
							switch (move) {
							case 0:
								newCells[scaleX(x - 3)][y]
										.addHabitant(new Animal());
								break;
							case 1:
								newCells[scaleX(x + 3)][y]
										.addHabitant(new Animal());
								break;
							case 2:
								newCells[x][scaleY(y - 3)]
										.addHabitant(new Animal());
								break;
							case 3:
								newCells[x][scaleY(y + 3)]
										.addHabitant(new Animal());
								break;
							}
							break;
						case 7:
							newCells[x][y].removeHabitant(i - sub);
							sub++;
							break;
						}	
					}
				}
			}
		}

		if (rounds == this.habitantStart) {
			int random = this.randomGenerator.nextInt(this.habitantProbability);

			for (int x = 0; x < xCellCount; x++) {
				for (int y = 0; y < yCellCount; y++) {
					if (random == 0) {
						this.newCells[x][y].addHabitant(new Animal());
					}

					random = this.randomGenerator.nextInt(this.habitantProbability);
				}
			}
		}
		
		// handle Predator
		for (int x = 0; x < this.xCellCount; x++) {
			for (int y = 0; y < this.yCellCount; y++) {
				if (newCells[x][y].hasPredator()) {
					int[] target = checkSurroundingForAnimal(x, y, newCells[x][y].getPredatorRadius()); 
					if (target[0] != -1) {
						newCells[x][y].feedPredator(newCells[target[0]][target[1]].getHabitantEnergy(target[2]));
						newCells[target[0]][target[1]].removeHabitant(target[2]);
					}
					
					switch (newCells[x][y].getPredatorAction()) {
					case 0:
						newCells[x][y].removePredator();
						break;
					case 1:
						int randX = randomGenerator.nextInt(xCellCount);
						int randY = randomGenerator.nextInt(yCellCount);
						newCells[randX][randY].addPredator(new Predator());
						break;
					case 32:
						newCells[scaleX(x+2)][scaleY(y)].addPredator(
								newCells[scaleX(x)][scaleY(y)].movePredator());
						break;
					case 33:
						newCells[scaleX(x-2)][scaleY(y)].addPredator(
								newCells[scaleX(x)][scaleY(y)].movePredator());
						break;
					case 34:
						newCells[scaleX(x)][scaleY(y-2)].addPredator(
								newCells[scaleX(x)][scaleY(y)].movePredator());
						break;
					case 35:
						newCells[scaleX(x)][scaleY(y+2)].addPredator(
								newCells[scaleX(x)][scaleY(y)].movePredator());
						break;
					}
				}
			}
		}
		
		if (rounds == 150) {
			int random = this.randomGenerator.nextInt(predatorProbability);
			for (int x = 0; x < xCellCount; x++) {
				for (int y = 0; y < yCellCount; y++) {
					if (random == 0) {
						newCells[x][y].addPredator(new Predator());
					}

					random = this.randomGenerator.nextInt(predatorProbability);
				}
			}
		}

		if (rounds % refresh == 0) {
			System.out.println("Rounds: " + this.rounds);
			this.repaint();
		}
	}

	/**
	 * A function to print the world to the screen
	 */
	public void paint(Graphics g) {
		Graphics2D ga = (Graphics2D) g;

		for (int x = 0; x < xCellCount; x++) {
			for (int y = 0; y < yCellCount; y++) {

				printCell(ga, newCells[x][y].getCellStatus(), x
						* (cellSize + 1) + 3, y * (cellSize + 1) + 24);
			}
		}
	}

	// ---- private functions ----

	private int[] getSurroundingPlantEnergy(int idxX, int idxY) {
		int[] result = new int[9];

		int counter = 0;

		for (int x = idxX - 1; x <= idxX + 1; x++) {
			for (int y = idxY - 1; y <= idxY + 1; y++) {

				result[counter] = newCells[(x + xCellCount) % xCellCount][(y + yCellCount)
						% yCellCount].getPlantEnergy();
				counter++;
			}
		}

		return result;
	}

	private int[] transferSurroundingPlantEnergy(int idxX, int idxY) {
		int[] result = new int[9];

		int counter = 0;

		for (int x = idxX - 1; x <= idxX + 1; x++) {
			for (int y = idxY - 1; y <= idxY + 1; y++) {

				result[counter] = newCells[(x + xCellCount) % xCellCount][(y + yCellCount)
						% yCellCount].transferPlantEnergy();
				counter++;
			}
		}

		return result;
	}

	private int[] getSurrounding(int idxX, int idxY) {
		int[] result = new int[9];

		int counter = 0;
		for (int x = idxX - 1; x <= idxX + 1; x++) {
			for (int y = idxY - 1; y <= idxY + 1; y++) {

				boolean isSurrounding = true;
				if (x == idxX && y == idxY) {
					isSurrounding = false;
				}

				result[counter] = newCells[(x + xCellCount) % xCellCount][(y + yCellCount)
						% yCellCount].transferEnergy(isSurrounding);
				counter++;
			}
		}

		return result;
	}
	
	private void printCell(Graphics2D g, int[] status, int x, int y) {
		float[] hsb = Color.RGBtoHSB(status[0], status[1], status[2], null);
		g.setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
		g.fillRect(x, y, this.cellSize, this.cellSize);
	}

	private int scaleX(int x) {

		return (x + xCellCount) % xCellCount;
	}

	private int scaleY(int y) {

		return (y + yCellCount) % yCellCount;
	}

	private int[] checkSurroundingForAnimal(int idxX, int idxY, int radius) {
		
		int targetCellX = -1, targetCellY = -1, targetNumber = -1;
		int maxEnergy = Integer.MIN_VALUE;

		for (int x = idxX - radius; x <= idxX + radius; x++) {
			for (int y = idxY - radius; y <= idxY + radius; y++) {
				int numberOfHabitants = newCells[scaleX(x + xCellCount)][scaleY(y + yCellCount)].hasHabitant();
				
				for (int i = 0; i < numberOfHabitants; i++) {
					int habitantEnergy = newCells[scaleX(x + xCellCount)][scaleY(y + yCellCount)].getHabitantEnergy(i); 
					if (habitantEnergy > maxEnergy) {
						targetCellX = scaleX(x + xCellCount);
						targetCellY = scaleY(y + yCellCount);
						targetNumber = i;
					}
				}
			}
		}
		
		if (targetCellX != -1) {
			return new int[] {targetCellX, targetCellY, targetNumber};
		} else {
			return new int[] { -1, -1, -1 };
		}
	}
}