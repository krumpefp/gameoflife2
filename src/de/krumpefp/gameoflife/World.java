package de.krumpefp.gameoflife;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

public class World extends Frame {

	// ---- class constants ----
	private final int xSpread = 3;
	private final int ySpread = 3;
	private final int seedFactor = 5;
	private final int habitantStart = 200;
	private final int habitantProbability = 1000;

	// ---- class data ----

	private int xSize, ySize, cellSize;
	private int xCellCount, yCellCount;
	private long rounds;

	private int refresh;

	private BaseCell[][] newCells;
	private ArrayList<BaseCell> cells = new ArrayList<>();

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
		int random = this.randomGenerator.nextInt(100);

		newCells = new BaseCell[xCellCount][yCellCount];

		for (int x = 0; x < xCellCount; x++) {
			for (int y = 0; y < yCellCount; y++) {
				if (random == 0) {
					this.newCells[x][y] = new BaseCell(1);
				} else {
					this.newCells[x][y] = new BaseCell(0);
				}

				random = this.randomGenerator.nextInt(100);
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

	private int[] getSurrounding(int i) {
		int[] result = new int[9];

		int[] currentIndex = calcCellCoordinatesFromIndex(i);

		int counter = 0;
		for (int x = currentIndex[0] - 1; x <= currentIndex[0] + 1; x++) {
			for (int y = currentIndex[1] - 1; y <= currentIndex[1] + 1; y++) {

				int index = calcIndexFromCoordinates((x + xCellCount)
						% xCellCount, (y + yCellCount) % yCellCount);

				boolean isSurrounding = true;
				if (index != i) {
					isSurrounding = false;
				}
				result[counter] = cells.get(index)
						.transferEnergy(isSurrounding);
				counter++;
			}
		}

		for (int x = currentIndex[0] - 1; x <= currentIndex[0] + 1; x++) {
			for (int y = currentIndex[1] - 1; y <= currentIndex[1] + 1; y++) {

				int index = calcIndexFromCoordinates((x + xCellCount)
						% xCellCount, (y + yCellCount) % yCellCount);

				boolean isSurrounding = true;
				if (index != i) {
					isSurrounding = false;
				}
				result[counter] = cells.get(index)
						.transferEnergy(isSurrounding);
				counter++;
			}
		}

		return result;
	}

	private int calcIndexFromCoordinates(int x, int y) {
		return y * xCellCount + x;
	}

	private void printCell(Graphics2D g, int[] status, int x, int y) {
		float[] hsb = Color.RGBtoHSB(status[0], status[1], status[2], null);
		g.setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
		g.fillRect(x, y, this.cellSize, this.cellSize);
	}

	private int calcCellIndexFromPx(int x, int y) {
		return (y / (this.cellSize + 1)) * xCellCount + x / (this.cellSize + 1);
	}

	private int[] calcCellCoordinatesFromIndex(int index) {
		int xIndex = index % xCellCount;
		int yIndex = index / yCellCount;

		return new int[] { xIndex, yIndex };
	}

	private int scaleX(int x) {

		return (x + xCellCount) % xCellCount;
	}

	private int scaleY(int y) {

		return (y + yCellCount) % yCellCount;
	}
}