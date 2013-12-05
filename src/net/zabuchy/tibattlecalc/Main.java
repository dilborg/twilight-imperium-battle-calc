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

/**
 * * units that can take the damages will receive it before any is destroyed
 */
package net.zabuchy.tibattlecalc;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FleetBuilder fb = new FleetBuilder();
        fb.setShips(new int[]{10, 3, 5, 0, 0, 9});
        //fb.setRace(race1);
        Fleet player1 = fb.build();

        fb = new FleetBuilder();
        fb.setShips(new int[]{20, 0, 7, 1, 8, 3});
        //fb.setRace(race2);
        Fleet player2 = fb.build();

        Node nodes[];
        nodes = Tree.destroyerBarrage(FleetBuilder.startNode(), player1, player2, 0);
        Tree battle = new Tree(player1, player2, nodes);

        battle.start();
        System.out.println(battle.getStats());
    }
}

class Stat {

    public static long binom(int N, int K) {
        long[][] binomial = new long[N + 1][K + 1];

        // base cases
        for (int k = 1; k <= K; k++) {
            binomial[0][k] = 0;
        }
        for (int n = 0; n <= N; n++) {
            binomial[n][0] = 1;
        }

        // bottom-up dynamic programming
        for (int n = 1; n <= N; n++) {
            for (int k = 1; k <= K; k++) {
                binomial[n][k] = binomial[n - 1][k - 1] + binomial[n - 1][k];
            }
        }

        return binomial[N][K];
    }

    public static HashMap<Integer, Double> prob(double hit, int maxDamage) {
        HashMap<Integer, Double> ret = new HashMap<Integer, Double>();
        //each damage is possible
        for (int i = 0; i <= maxDamage; i++) {
            ret.put(i, Stat.binom(maxDamage, i) * Math.pow(hit, i) * Math.pow(1 - hit, maxDamage - i));
        }
        return ret;
    }
}
