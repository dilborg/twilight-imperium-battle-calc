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

package net.zabuchy.tiservlet;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.zabuchy.tibattlecalc.*;

/**
 *
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
@SuppressWarnings("serial")
public class Battle extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		int race1 = 0, race2 = 0;
		int ships1[] = null, ships2[] = null;

		try {
			race1 = Integer.parseInt(req.getParameter("race1"));
			ships1 = new int[] {
					Integer.parseInt(req.getParameter("fighters1")),
					Integer.parseInt(req.getParameter("carriers1")),
					Integer.parseInt(req.getParameter("destroyers1")),
					Integer.parseInt(req.getParameter("cruisers1")),
					Integer.parseInt(req.getParameter("dreadnoughts1")),
					Integer.parseInt(req.getParameter("warsuns1")), };

			race2 = Integer.parseInt(req.getParameter("race2"));
			ships2 = new int[] {
					Integer.parseInt(req.getParameter("fighters2")),
					Integer.parseInt(req.getParameter("carriers2")),
					Integer.parseInt(req.getParameter("destroyers2")),
					Integer.parseInt(req.getParameter("cruisers2")),
					Integer.parseInt(req.getParameter("dreadnoughts2")),
					Integer.parseInt(req.getParameter("warsuns2")), };
		} catch (NumberFormatException ex) {
			throw ex;// new ServletException(ex.getMessage());
		}
		
		if(race1 != FleetBuilder.RACE_OTHER && race1 == race2) {
			throw new RuntimeException("You can't have 2 the same races");
		}
		
		//@TODO check if any not > Fleet.MAXDAMAGE
		
		FleetBuilder fb = new FleetBuilder();
		fb.setShips(ships1);
		fb.setRace(race1);
		Fleet player1 = fb.build();

		fb = new FleetBuilder();
		fb.setShips(ships2);
		fb.setRace(race2);
		Fleet player2 = fb.build();

		Node nodes[];
		nodes = Tree.destroyerBarrage(FleetBuilder.startNode(), player1, player2, 0);
		if(race1 == FleetBuilder.RACE_MENTAK) {
			nodes = Tree.mentak(nodes, player1, 1);
		} else if (race2 == FleetBuilder.RACE_MENTAK) {
			nodes = Tree.mentak(nodes, player2, 2);
		}
		
		if(race1 == FleetBuilder.RACE_XXCHA) {
			nodes = Tree.xxcha(nodes, player1, player2, 1);
		} else if (race2 == FleetBuilder.RACE_XXCHA) {
			nodes = Tree.xxcha(nodes, player1, player2, 2);
		}
		//System.out.println(Arrays.toString(nodes));
		//Tree battle = new Tree(player1, player2, FleetBuilder.startNode());
		Tree battle = new Tree(player1, player2, nodes);
		
		battle.start();

		RequestDispatcher view = req
				.getRequestDispatcher("WEB-INF/jsp/battle.jsp");
		req.setAttribute("race1Name", FleetBuilder.getRace(race1));
		req.setAttribute("race2Name", FleetBuilder.getRace(race2));
		req.setAttribute("result", battle.getStats());
		view.forward(req, resp);
	}
}
