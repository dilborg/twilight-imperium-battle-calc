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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.zabuchy.tibattlecalc.*;

/**
 *
 * @author Tomasz Muras <nexor1984@gmail.com>
 */
@SuppressWarnings("serial")
public class Start extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		RequestDispatcher view = req.getRequestDispatcher("WEB-INF/jsp/start.jsp");
		req.setAttribute("races", FleetBuilder.races);
		
		view.forward(req, resp);
	}
}
