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
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
public class Nodes {
    private HashMap<Integer, Node> nodes;


    public Nodes(Node[] initial) {
        nodes = new HashMap<Integer,Node>();
    	for(Node n : initial) {
            if(n != null) add(n);
    	}

    }

    @Override
    public String toString()
    {
        String ret = "";
        for(Map.Entry<Integer, Node> e : nodes.entrySet()) {
            ret = ret + e.getKey() + ": " + e.getValue();
        }
        return ret;
    }
    public void add(Node n) {
        //System.out.println(n);
        //int key = n.getDamage1() * Fleet.MAXDAMAGE + n.getDamage2();
    	int key = n.fightersDamage1 * Fleet.MAXDAMAGE * 10000 + n.fightersDamage2 * Fleet.MAXDAMAGE * 100 + n.getDamage1() * Fleet.MAXDAMAGE + n.getDamage2();
    	
        if(nodes.containsKey(key)) {
            nodes.get(key).probability += n.probability;
        } else {
            nodes.put(key, n);
        }
    }

    public Node pop() {
       Iterator<Integer> i = nodes.keySet().iterator();
       int key = i.next();
       return nodes.remove(key);
    }

    public int size() {
        return nodes.size();
    }
}
