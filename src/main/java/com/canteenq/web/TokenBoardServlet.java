package com.canteenq.web;

import com.canteenq.model.FoodOrder;
import com.canteenq.service.KitchenService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/board")
public class TokenBoardServlet extends HttpServlet {

    private KitchenService kitchen;

    @Override
    public void init() {
        kitchen = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext())
                .getBean(KitchenService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html><head><title>Token Board</title>");
        out.println("<meta http-equiv='refresh' content='5'>");
        out.println("<style>body{font-family:Segoe UI,Arial;background:#1d1d1d;color:#eee;display:flex;gap:40px;padding:30px}"
                + "div{flex:1}h2{border-bottom:2px solid #555;padding-bottom:8px}"
                + "p{font-size:28px;margin:8px 0}.ready p{color:#7CFC00}.wait p{color:#ffb347}</style></head><body>");
        printColumn(out, "wait", "Preparing", kitchen.preparing());
        printColumn(out, "ready", "Ready - please collect", kitchen.ready());
        out.println("</body></html>");
    }

    private void printColumn(PrintWriter out, String css, String title, List<FoodOrder> orders) {
        out.println("<div class='" + css + "'><h2>" + title + "</h2>");
        if (orders.isEmpty()) {
            out.println("<small>No tokens</small>");
        }
        for (FoodOrder o : orders) {
            out.println("<p>#" + o.getToken() + " &nbsp; " + o.getMember().getName() + "</p>");
        }
        out.println("</div>");
    }
}
