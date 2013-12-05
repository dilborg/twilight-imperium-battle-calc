// TI calculator is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// TI calculator is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with TI calculator.  If not, see <http://www.gnu.org/licenses/>.


package net.zabuchy.tibattlecalc;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
public class Fleet {
	public int race;
	public static int SHIP_FIGHTER = 0;
	public static int SHIP_CARRIER = 1;
	public static int SHIP_DESTROYER = 2;
	public static int SHIP_CRUISER = 3;
	public static int SHIP_DREADNOUGHT = 4;
	public static int SHIP_WAR_SUN = 5;

	public static String shipName[] = { "Fighter", "Carrier", "Destroyer",
			"Cruiser", "Dreadnought", "War Sun" };
	public double shipHitProb[] = { 0.2, 0.2, 0.2, 0.4, 0.6, 0.8 };

	public double[] getShipHitProb() {
		return shipHitProb;
	}

	public void setShipHitProb(double[] shipHitProb) {
		this.shipHitProb = shipHitProb;
	}

	private double savedShipHitProb[];
	public static int shipShots[] = { 1, 1, 1, 1, 1, 3 };
	public static int shipHP[] = { 1, 1, 1, 1, 2, 2 };
	public static int MAXDAMAGE = 100;
	// public static int DAMAGED = 5;
	public static int SHIPS = 6;
	public int ships[];
	public int damage = 0;

        private  HashMap<Integer, HashMap<Integer, Double>> shootCache = new HashMap<Integer, HashMap<Integer, Double>>();

	public Fleet(int ships[]) {
		if (ships == null) {
			throw new RuntimeException("Fleet needs ships");
		}
		this.ships = ships;
	}

	public HashMap<Double, Integer> remainingHits(int hit, int fighterDamage) {
		int[] newShips = kill(hit, fighterDamage);
		int current;

		HashMap<Double, Integer> hitMap = new HashMap<Double, Integer>();
		for (int i = 0; i < SHIPS; i++) {
			if (newShips[i] > 0) {
				if (hitMap.containsKey(shipHitProb[i])) {
					current = hitMap.get(shipHitProb[i]);
				} else {
					current = 0;
				}
				hitMap.put(shipHitProb[i], current + newShips[i]
								* shipShots[i]);
			}
		}
		return hitMap;
	}

	/**
	 * Replaces probabilities array with a new version. Old probabilities are
	 * saved.
	 */
	public void pushProb(double newProbs[]) {
		savedShipHitProb = shipHitProb;
		shipHitProb = newProbs;
	}

	/**
	 * Restores old probabilities array.
	 */
	public void popProb() {
		shipHitProb = savedShipHitProb;
	}

	/**
	 * Kills appropriate number of ships, including fighters.
	 * 
	 * @param hit
	 * @param fighterDamage
	 * @return
	 */
	public int[] kill(int hit, int fighterDamage) {
		int newShips[] = ships.clone();
		int maxDamage = newShips[SHIP_DREADNOUGHT] + newShips[SHIP_WAR_SUN];

		// reduce number of fighters
		newShips[SHIP_FIGHTER] -= fighterDamage;

		// hits will only cause a damage
		if (hit <= maxDamage) {
			damage = hit;
			hit = 0;
		} else {
			// first hit the ones that can take a damage
			hit -= maxDamage;
			damage = maxDamage;
			// start killing off from left to right
			for (int i = 0; i <= 3; i++) {
				if (newShips[i] >= hit) {
					newShips[i] -= hit;
					hit = 0;
					break;
				}
				hit -= newShips[i];
				newShips[i] = 0;
			}

			for (int i = 4; i <= 5; i++) {
				if (newShips[i] >= hit) {
					newShips[i] -= hit;
					damage -= hit;
					hit = 0;
					break;
				}
				hit -= newShips[i];
				damage -= newShips[i];
				newShips[i] = 0;
			}
		}
		assert (hit == 0);

		return newShips;
	}

	/**
	 * 
	 * Return total hit points.
	 * 
	 * @return
	 */
	public int remainingHP(int hit, int fighterDamage) {
		int[] newShips = kill(hit, fighterDamage);
		int hp = 0;
		for (int i = 0; i < SHIPS; i++) {
			hp += newShips[i] * shipHP[i];
		}
		hp -= damage;

		return hp;
	}

        public HashMap<Integer, Double> shootProxy(int hit, int fighterDamage) {
            int key = Fleet.MAXDAMAGE*100*hit + fighterDamage;
            if(!shootCache.containsKey(key)) {
                shootCache.put(key, shoot(hit, fighterDamage));
            } 
            return shootCache.get(key);
        }

	public HashMap<Integer, Double> shoot(int hit, int fighterDamage) {
		int[] newShips = kill(hit, fighterDamage);
		// double[] hits = new double[5];
		// all my ship types
		HashMap<Integer, Double>[] hitTypes = new HashMap[SHIPS];

		for (int i = 0; i < SHIPS; i++) {
			hitTypes[i] = Stat.prob(shipHitProb[i], newShips[i] * shipShots[i]);
		}

		HashMap<Integer, Double> combined;// = new HashMap<Integer, Double>();
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();

		int combinedHit;
		double combinedProb;

		combined = hitTypes[0];
		for (int i = 1; i < SHIPS; i++) {
			result = new HashMap<Integer, Double>();
			for (Map.Entry<Integer, Double> e0 : combined.entrySet()) {
				for (Map.Entry<Integer, Double> e1 : hitTypes[i].entrySet()) {
					combinedHit = e0.getKey() + e1.getKey();
					combinedProb = e0.getValue() * e1.getValue();
					// add to combined set
					if (result.containsKey(combinedHit)) {
						combinedProb += result.get(combinedHit);
					}
					result.put(combinedHit, combinedProb);
				}
			}
			// System.out.println(result);
			// new combined is a result
			combined = (HashMap<Integer, Double>) result;
		}

		double sum = 0;
		for (Map.Entry<Integer, Double> e : result.entrySet()) {
			sum += e.getValue();
		}
		assert Math.abs(1 - sum) < 0.001 : "Does not sum up to 100%";

		// System.out.println(hitTypes[0]);
		// System.out.println(hitTypes[1]);
		// System.out.println(result);
		// System.out.println(sum);
		return result;
	}
}
