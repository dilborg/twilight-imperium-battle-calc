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

import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
public class Tree {

	int maxDamage1;
	int maxDamage2;
	Fleet fleet1, fleet2;
	// LinkedList<Node> nodes;
	Nodes nodes;
	double hit = 0.5;
	double probabilityMin = 1.0e-6;
	int noLeafs = 0;
	int noNodes = 0;
	double draw, win1, win2;
	double damageReceived1[], damageReceived2[];

	public Tree(Fleet fleet1, Fleet fleet2, Node startNodes[]) {
		this.fleet1 = fleet1;
		this.fleet2 = fleet2;
		damageReceived1 = new double[fleet1.remainingHP(0, 0)];
		damageReceived2 = new double[fleet2.remainingHP(0, 0)];
		maxDamage1 = fleet1.remainingHP(0, 0);
		maxDamage2 = fleet2.remainingHP(0, 0);
		nodes = new Nodes(startNodes);
		// this.nodes = new LinkedList<Node>();
		// special pre-battle events
		// Mentak
		// mentak(nodes, fleet1, 1);
		//nodes.add();
	}

	/**
	 * 
	 * @param nodes
	 * @param fleet1
	 * @param fleet2
	 * @param which
	 * @return
	 */
	public static Node[] destroyerBarrage(Node[] nodes, Fleet fleet1, Fleet fleet2,
			int which) {
		Node[] ret = null;

		assert Math.abs(1 - sumProb(nodes)) < 0.001 : "Probability doesn't sum up to 1 at the beginning of destroyerBarrage";

		int destroyers1 = fleet1.ships[Fleet.SHIP_DESTROYER];
		int fighters1 = fleet1.ships[Fleet.SHIP_FIGHTER];
		int destroyers2 = fleet2.ships[Fleet.SHIP_DESTROYER];
		int fighters2 = fleet2.ships[Fleet.SHIP_FIGHTER];

		int shots1 = Math.min(destroyers1, fighters2);
		int shots2 = Math.min(destroyers2, fighters1);

		if (shots1 == 0 && shots2 == 0) {
			return nodes;
		}

		double prob1 = fleet1.shipHitProb[Fleet.SHIP_DESTROYER];
		double prob2 = fleet2.shipHitProb[Fleet.SHIP_DESTROYER];

		if (shots1 > 2) {
			shots1 = 2;
		}
		if (shots2 > 2) {
			shots2 = 2;
		}

		ret = new Node[(shots1 +1 )* (shots2 + 1)* nodes.length];

		HashMap<Integer, Double> fleet1Hits = Stat.prob(prob1, shots1);
		HashMap<Integer, Double> fleet2Hits = Stat.prob(prob2, shots2);
//		System.out.println(fleet1Hits);
//		System.out.println(fleet2Hits);

		int i=0;
		for (Node n : nodes) {
			for (Map.Entry<Integer, Double> f1 : fleet1Hits.entrySet()) {
				for (Map.Entry<Integer, Double> f2 : fleet2Hits.entrySet()) {
					ret[i] = new Node(n.probability * f1.getValue() * f2.getValue(),n.damage1,n.damage2,n.fightersDamage1 + f2.getKey(),n.fightersDamage2 + f1.getKey());
					i++;
				}
			}
		}

		
		
		assert Math.abs(1 - sumProb(nodes)) < 0.001 : "Probability doesn't sum up to 1 at the end of destroyerBarrage";
		return ret;
	}

	/**
	 * For XXCHA we need so simulate first turn with the enemy's stats lowered.
	 * 
	 * @param nodes
	 * @param fleet1
	 * @param fleet2
	 * @param which Which fleet is XXCHA's
	 * @return
	 */
	public static Node[] xxcha(Node[] nodes, Fleet fleet1, Fleet fleet2, int which) {
		// Node[] ret = null;// = new Node[1];
		//Fleet xxcha = null;
		Fleet enemy = null;

		// check that probability is 1
		assert Math.abs(1 - sumProb(nodes)) < 0.001 : "Probability doesn't sum up to 1 at the beginning of mentak";

		if (which == 1) {
			//xxcha = fleet1;
			enemy = fleet2;
		} else if (which == 2) {
			//xxcha = fleet2;
			enemy = fleet1;
		} else {
			throw new RuntimeException("which should equal 1 or 2");
		}

		double[] enemyProbs = new double[enemy.shipHitProb.length];
		for (int i = 0; i < enemy.shipHitProb.length; i++) {
			enemyProbs[i] = enemy.shipHitProb[i] - 0.1;
		}

		enemy.pushProb(enemyProbs);

		Node[] newNodes = null;// = new Node[nodes.length];
		Node[] tmpNodes = null;
		int pos = 0;
		// emulate one turn for each nodes
		for (Node n : nodes) {
			HashMap<Integer, Double> fleet1Hits = fleet1.shoot(n.getDamage1(),
					n.fightersDamage1);
			HashMap<Integer, Double> fleet2Hits = fleet2.shoot(n.getDamage2(),
					n.fightersDamage2);
			// new array to accommodate existing + new nodes
			// = new Node[];
			if (newNodes != null) {
				// copy newNodes into
				tmpNodes = new Node[newNodes.length + fleet1Hits.size()
						* fleet2Hits.size()];
				System.arraycopy(newNodes, 0, tmpNodes, 0, newNodes.length);
				pos = newNodes.length;
				newNodes = tmpNodes;
			} else {
				newNodes = new Node[fleet1Hits.size() * fleet2Hits.size()];
			}

			double prob;
			int damage1, damage2;

			// create new nodes for each combination
			for (Map.Entry<Integer, Double> f1 : fleet1Hits.entrySet()) {
				for (Map.Entry<Integer, Double> f2 : fleet2Hits.entrySet()) {
					prob = f1.getValue() * f2.getValue() * n.probability;
					// damage inflicted to fleet1
					damage1 = f2.getKey() + n.getDamage1();
					damage2 = f1.getKey() + n.getDamage2();
					newNodes[pos++] = new Node(prob, damage1, damage2,
							n.fightersDamage1, n.fightersDamage2);
				}
			}
		}
		enemy.popProb();
		// check that probability is 1
		assert Math.abs(1 - sumProb(newNodes)) < 0.001 : "Probability doesn't sum up to 1 at the end of mentak";
		return newNodes;
	}

	/**
	 * Mentak special ability. Up to two Cruisers or Destroyers fire.
	 * 
	 * @param nodes
	 * @param mentaks
	 * @param which
	 *            Which fleet is the Mentak's one: 1 or 2.
	 * @return nodes
	 */
	public static Node[] mentak(Node[] nodes, Fleet mentaks, int which) {
		Node[] ret = null;// = new Node[1];
		// check that probability is 1
		assert Math.abs(1 - sumProb(nodes)) < 0.001 : "Probability doesn't sum up to 1 at the beginning of mentak";

		HashMap<Integer, Double> hits = null;// new HashMap<Integer, Double>();

		// check number of Cruisers and Destroyers
		if (mentaks.ships[Fleet.SHIP_CRUISER] >= 2) {
			// fire with 2 cruisers
			hits = Stat.prob(mentaks.shipHitProb[Fleet.SHIP_CRUISER], 2);
		} else if (mentaks.ships[Fleet.SHIP_CRUISER] == 1
				&& mentaks.ships[Fleet.SHIP_DESTROYER] >= 1) {
			// fire with 1 cruiser and 1 destroyer
			HashMap<Integer, Double> hits1 = Stat.prob(
					mentaks.shipHitProb[Fleet.SHIP_CRUISER], 1);
			HashMap<Integer, Double> hits2 = Stat.prob(
					mentaks.shipHitProb[Fleet.SHIP_DESTROYER], 1);
			hits = new HashMap<Integer, Double>();

			for (Map.Entry<Integer, Double> e1 : hits1.entrySet()) {
				for (Map.Entry<Integer, Double> e2 : hits2.entrySet()) {
					int hit = e1.getKey() + e2.getKey();
					double prob = e1.getValue() * e2.getValue();
					if (hits.containsKey(hit)) {
						hits.put(hit, prob + hits.get(hit));
					} else {
						hits.put(hit, prob);
					}
				}
			}
		} else if (mentaks.ships[Fleet.SHIP_CRUISER] == 1) {
			// fire with 1 cruiser
			hits = Stat.prob(mentaks.shipHitProb[Fleet.SHIP_CRUISER], 1);
		} else if (mentaks.ships[Fleet.SHIP_DESTROYER] >= 2) {
			// fire with 2 destroyers
			hits = Stat.prob(mentaks.shipHitProb[Fleet.SHIP_DESTROYER], 2);
		} else if (mentaks.ships[Fleet.SHIP_DESTROYER] == 1) {
			// fire with 1 destroyer
			hits = Stat.prob(mentaks.shipHitProb[Fleet.SHIP_DESTROYER], 1);
		} else {
			// no effect
			return nodes;
		}
		ret = new Node[nodes.length * hits.size()];
		int i = 0;
		for (Node n : nodes) {
			for (Map.Entry<Integer, Double> e : hits.entrySet()) {
				// assume that Mentaks are fleet1 so they only deal damage to
				// fleet2
				if (which == 1) {
					ret[i] = new Node(n.probability * e.getValue(), n
							.getDamage1(), n.getDamage2() + e.getKey(),
							n.fightersDamage1, n.fightersDamage2);
				} else if (which == 2) {
					ret[i] = new Node(n.probability * e.getValue(), n
							.getDamage1()
							+ e.getKey(), n.getDamage2(), n.fightersDamage1,
							n.fightersDamage2);
				} else {
					throw new java.lang.RuntimeException(
							"Which must be equal 1 or 2.");
				}
				i++;
			}
		}

		// check that probability is 1
		assert Math.abs(1 - sumProb(ret)) < 0.001 : "Probability doesn't sum up to 1 at the end of mentak";
		return ret;
	}

	private static double sumProb(Node[] nodes) {
		double ret = 0.0;
		for (Node n : nodes) {
			ret += n.probability;
		}
		return ret;
	}

	public void start() {
		Node node;
		int i = 0;

		System.out.println("Starting with "+ nodes.size() + " nodes");
		while (nodes.size() > 0) {
			if (i % 10000 == 0) {
				System.out.println(nodes.size());
			}
			node = nodes.pop();
			process(node);
			i++;
/*                        System.out.println("Iteration: "+i);
                        System.out.println(nodes);*/
		}
		//
		stats();
		// System.out.println(damageReceived1);
		// System.out.println(damageReceived2);
	}

	public void process(Node n) {
		HashMap<Integer, Double> fleet1Hits = fleet1.shootProxy(n.getDamage1(),
				n.fightersDamage1);
		HashMap<Integer, Double> fleet2Hits = fleet2.shootProxy(n.getDamage2(),
				n.fightersDamage2);

		double prob;
		int damage1, damage2;

		// create new nodes for each combination
		for (Map.Entry<Integer, Double> fleet1 : fleet1Hits.entrySet()) {
			for (Map.Entry<Integer, Double> fleet2 : fleet2Hits.entrySet()) {
				prob = fleet1.getValue() * fleet2.getValue() * n.probability;
				// damage inflicted to fleet1
				damage1 = fleet2.getKey() + n.getDamage1() + n.fightersDamage1;
				damage2 = fleet1.getKey() + n.getDamage2() + n.fightersDamage2;
				if (damage1 >= maxDamage1 || damage2 >= maxDamage2) {
					// System.out.println("One fleet has won, parent node: " + n
					// + "; prob: "+ prob + " d1: " + damage1 + " d2: "+
					// damage2);
					result(prob, damage1, damage2);
				} else if (prob > probabilityMin) {
					noNodes += 1;
					// System.out.println("Creating new child, parent node: " +
					// n + "; prob: "+ prob + " d1: " + damage1 + " d2: "+
					// damage2);
					nodes.add(new Node(prob, damage1, damage2,
							n.fightersDamage1, n.fightersDamage2));
				}
				// System.out.println("Key : " + fleet1.getKey() + " value " +
				// fleet1.getValue());
			}
		}
		// stats();
	}

	public void stats() {
		System.out.println("Leafs: " + noLeafs);
		System.out.println("Nodes created: " + noNodes);
		System.out.println("Win1: " + win1);
		System.out.println("Win2: " + win2);
		System.out.println("Draw: " + draw);
	}

	public Map<String, String> getStats() {
		Map<String, String> stats = new HashMap<String, String>();

		stats.put("leafs", Integer.toString(noLeafs));
		stats.put("nodesCreated", Integer.toString(noNodes));
		stats.put("win1", String.format("%.2f", win1 * 100));
		stats.put("win2", String.format("%.2f", win2 * 100));
		stats.put("draw", String.format("%.2f", draw * 100));
		stats.put("accuracy", String.format("%.4f", win1 * 100 + win2 * 100
				+ draw * 100));

		return stats;
	}

	public void result(double probability, int damage1, int damage2) {
		noLeafs += 1;
		if (damage1 >= maxDamage1 && damage2 >= maxDamage2) {
			draw += probability;
		} else if (damage1 >= maxDamage1) {
			win2 += probability;
			// fleet 2 won, note damage it took
			damageReceived2[damage2] += probability;
		} else if (damage2 >= maxDamage2) {
			win1 += probability;
			// fleet 2 won, note damage it took
			damageReceived1[damage1] += probability;
		} else {
			throw new RuntimeException(
					"Called end result when both fleets are still alive.");
		}
	}
}
