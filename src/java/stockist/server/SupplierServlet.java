/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockist.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
@WebServlet(urlPatterns = "/supplier")
public class SupplierServlet extends HttpServlet {
    
    @Resource(lookup= "jdbc/stockist")
    private DataSource connPool;
    JsonArrayBuilder supplierBuilder = Json.createArrayBuilder();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        try(Connection conn = connPool.getConnection()){
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM stockist.suppliers");
            
            while(rs.next()){
                JsonObject supplier = Json.createObjectBuilder()
                            .add("Supplier_id", rs.getString("SupplierID"))
                            .add("CompanyName", rs.getString("CompanyName"))
                            .build();
                supplierBuilder.add(supplier);
                
            }
            rs.close();
            
        }catch (SQLException ex){
            log(ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
            //throw new IOException(ex);
        }

        try (PrintWriter pw = resp.getWriter()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(MediaType.APPLICATION_JSON);
            pw.println(supplierBuilder.build().toString());

        }
    }
}
