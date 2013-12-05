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

import java.text.DecimalFormat;

/**
 *
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
public class Node {
    public double probability;
    public int damage1;
    public int damage2;
    public int fightersDamage1;
    public int fightersDamage2;
    
/*    public double getProbability() {
        return probability;
    }
*/
    public int getDamage1() {
        return damage1;
    }

    public int getDamage2() {
        return damage2;
    }

    public Node(double probability, int damage1, int damage2, int fightersDamage1, int fightersDamage2) {
        this.probability = probability;
        this.damage1 = damage1;
        this.damage2 = damage2;
        this.fightersDamage1 = fightersDamage1;
        this.fightersDamage2 = fightersDamage2;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.#######");
        return "{P: " + df.format(probability) + ", " + damage1 + ", " + damage2 + ", " + fightersDamage1 +  ", " + fightersDamage2 + "}";
    }


}
