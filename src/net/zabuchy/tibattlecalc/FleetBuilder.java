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

/**
 * 
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
public class FleetBuilder {
	public static final String races[] = { "Other", "Mentak", "Xxcha",
			"Jolnar", "Naalu", "Sardakk", "Lizix" };
	public static final double BATTLE_OTHER[] = { 0.2, 0.2, 0.2, 0.4, 0.6, 0.8 };
	public static final double BATTLE_JOLNAR[] = { 0.1, 0.1, 0.1, 0.3, 0.5, 0.7 };
	public static final double BATTLE_NAALU[] = { 0.3, 0.2, 0.2, 0.4, 0.6, 0.8 };
	public static final double BATTLE_SARDAKK[] = { 0.3, 0.3, 0.3, 0.5, 0.7, 0.9 };
	public static final double BATTLE_LIZIX[] = { 0.2, 0.2, 0.2, 0.4, 0.7, 0.8 };
	public static final int RACE_OTHER = 0;
	public static final int RACE_MENTAK = 1;
	public static final int RACE_XXCHA = 2;
	public static final int RACE_JOLNAR = 3;
	public static final int RACE_NAALU = 4;
	public static final int RACE_SARDAKK = 5;
	public static final int RACE_LIZIX = 6;

	// technologies
	public boolean techHylarVAssaultLaser = false;
	public boolean techAssaultCannon = false;
	public boolean techCybernetics = false;
	public boolean techAdvancedFighters = false;

	private int race = RACE_OTHER;
	private int ships[];

	public static String getRace(int i) {
		return races[i];
	}

	public void setRace(int race) {
		this.race = race;
	}

	public static Node[] startNode() {
		Node[] start = new Node[1];
		start[0] = new Node(1, 0, 0, 0, 0);
		return start;
	}

	public Fleet build() {
		Fleet fleet = new Fleet(ships);
		switch (race) {
		case RACE_JOLNAR:
			fleet.setShipHitProb(BATTLE_JOLNAR);
			break;
		case RACE_NAALU:
			fleet.setShipHitProb(BATTLE_NAALU);
			break;
		case RACE_SARDAKK:
			fleet.setShipHitProb(BATTLE_SARDAKK);
			break;
		case RACE_LIZIX:
			fleet.setShipHitProb(BATTLE_LIZIX);
			break;
		default:
			fleet.setShipHitProb(BATTLE_OTHER);
			break;
		}

		if (techHylarVAssaultLaser) {
			// Cruisers and Destroyers +1
		}
		if (techAssaultCannon) {
			// Each Dreadnought fires once before the battle
		}
		if (techCybernetics) {
			// Fighters +1
		}
		if (techAdvancedFighters) {
			// Fighters +1
		}
		return fleet;
	}

	public void setShips(int[] ships) {
		this.ships = ships;
	}
}
