/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockist.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mmu1t
 */
@WebServlet(urlPatterns = "/supplier/*")
public class SupplierDetailServlet extends HttpServlet {

    @Resource(lookup = "jdbc/stockist")
    private DataSource connPool;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        //Assume no error checks
        String supplierId = pathInfo.substring(1);

        try (Connection conn = connPool.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM stockist.suppliers where SupplierID = ?");
            ps.setString(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            JsonObject supplier = Json.createObjectBuilder()
                    .add("Supplier_id", rs.getString("SupplierID"))
                    .add("Company_Name", rs.getString("CompanyName"))
                    .add("Contact_Number", rs.getString("ContactNumber"))
                    .add("Address", rs.getString("Address"))
                    .build();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(MediaType.APPLICATION_JSON);
            try (PrintWriter pw = resp.getWriter()) {
                pw.println(supplier.toString());

            }
            rs.close();
        } catch (SQLException ex) {
            log(ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

    }

}
