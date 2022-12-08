import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class XMLFile {
    public static String sqlQuery(String start, String end) {
        //this method is used to create the xml string

        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Unable to connect");
        }
        try {
            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC&useSSL=false"
                    , "mpandey", "B00917801");
            statement = connect.createStatement();
            Statement statement1 = connect.createStatement();
            statement1.execute("use csci3901;");
            //Query1
            //1. Customer information
            //a. Report the customer name, address, value of orders, and owing balance for
            // customers who became customers (have their first order) in the given time
            // period.
            resultSet = statement.executeQuery("SELECT \n" +
                    "    a.customerNumber,\n" +
                    "    a.customerName,\n" +
                    "    a.addressLine1,\n" +
                    "    a.city,\n" +
                    "    a.state,\n" +
                    "    a.postalCode,\n" +
                    "    a.country,\n" +
                    "    a.s AS value_of_orders,\n" +
                    "    (SUM(p.amount) - a.s) AS outstanding_balance\n" +
                    "FROM\n" +
                    "    (SELECT \n" +
                    "        c.customerNumber,\n" +
                    "            c.customerName,\n" +
                    "            o.orderNumber,\n" +
                    "            c.addressLine1,\n" +
                    "            c.city,\n" +
                    "            c.state,\n" +
                    "            c.postalCode,\n" +
                    "            c.country,\n" +
                    "            SUM(od.priceEach * quantityOrdered) AS s\n" +
                    "    FROM\n" +
                    "        orders o, customers c, orderdetails od\n" +
                    "    WHERE\n" +
                    "        o.customerNumber = c.customernumber\n" +
                    "            AND o.orderNumber = od.orderNumber\n" +
                    "            AND o.orderDate = (SELECT \n" +
                    "                MIN(o1.orderDate)\n" +
                    "            FROM\n" +
                    "                orders o1\n" +
                    "            WHERE\n" +
                    "                o.customerNumber = o1.customerNumber)\n" +
                    "            AND o.orderDate BETWEEN "+ "'" + start+ "'" + "and " + "'" + end + "' " +
                    "    GROUP BY o.orderNumber) a,\n" +
                    "    payments p\n" +
                    "WHERE\n" +
                    "    a.customerNumber = p.customerNumber\n" +
                    "GROUP BY a.orderNumber;");
//Arraylist to store the data.
            ArrayList<String> CustomerName = new ArrayList<>();
            ArrayList<String> Address = new ArrayList<>();
            ArrayList<String> City = new ArrayList<>();
            ArrayList<String> Region = new ArrayList<>();
            ArrayList<String> PostalCode = new ArrayList<>();
            ArrayList<String> Country = new ArrayList<>();
            ArrayList<Double> order_value = new ArrayList<>();
            ArrayList<Double> outstanding_balance = new ArrayList<>();

            while (resultSet.next()) {
                CustomerName.add(resultSet.getString("customerName"));
                String str = resultSet.getString("addressLine1").replace("\n", " ");
                Address.add(str);
                City.add(resultSet.getString("city"));
                Region.add(resultSet.getString("state"));
                PostalCode.add(resultSet.getString("postalCode"));
                Country.add(resultSet.getString("country"));
                order_value.add(resultSet.getDouble("value_of_orders"));
                outstanding_balance.add(resultSet.getDouble("outstanding_balance"));
            }
            String result1 = "";
            String str1 = "\t<customer_list>\n";
            String str2 = "";
            for (int i = 0; i < CustomerName.size(); i++) {

                str2 = str2 + "\t\t<customer>\n" +
                        "\t\t\t<customer_name> " + CustomerName.get(i) + "</customer_name>\n" +
                        "\t\t\t<address>\n" +
                        "\t\t\t\t<street_address> " + Address.get(i) + " </street_address>\n" +
                        "\t\t\t\t<city> " + City.get(i) + " </city>\n" +
                        "\t\t\t\t<postal_code> " + PostalCode.get(i) + " </postal_code>\n" +
                        "\t\t\t\t<country> " + Country.get(i) + " </country>\n" +
                        "\t\t\t</address>\n" +
                        "\t\t\t<order_value> " + order_value.get(i) + " </order_value>\n" +
                        "\t\t\t<outstanding_balance> " + outstanding_balance.get(i) + " </outstanding_balance>\n" +
                        "\t\t</customer>\n";

            }
            str1 = str1 + str2 + "\t</customer_list>\n";
            result1 = result1 + str1;
            str1 = "";
            str2 = "";
//-------------------------------------------------------------------
            //QUERY2
            //2. Product information
            //a. Report, for each product whose first sale is in the given period, the product
            // name, product line name, the date the product was first sold and, for each
            // customer who bought the product in the given period, the customer name and
            // total number of units ordered by that customer
           resultSet = statement.executeQuery("SELECT \n" +
                   "    p.productName,\n" +
                   "    p.productLine,\n" +
                   "    o.orderDate AS introduction_date,\n" +
                   "    c.customerName,\n" +
                   "    od.quantityOrdered\n" +
                   "FROM\n" +
                   "    (SELECT \n" +
                   "        o.orderNumber, o.orderDate, od.productCode, o.customerNumber\n" +
                   "    FROM\n" +
                   "        orders o, orderdetails od\n" +
                   "    WHERE\n" +
                   "        od.orderNumber = o.orderNumber\n" +
                   "            AND orderDate BETWEEN "+ "'" + start+ "'" + "and " + "'" + end + "') AS a,\n" +
                   "    orderdetails od,\n" +
                   "    orders o,\n" +
                   "    products p,\n" +
                   "    customers c\n" +
                   "WHERE\n" +
                   "    od.productCode = a.productCode\n" +
                   "        AND o.orderNumber = od.orderNumber\n" +
                   "        AND a.productCode = p.productCode\n" +
                   "        AND c.customerNumber = a.customerNumber\n" +
                   "GROUP BY od.productCode;");

//Arraylist to store the data.
            ArrayList<String> pn = new ArrayList<>();
            ArrayList<String> pl = new ArrayList<>();
            ArrayList<String> od = new ArrayList<>();
            ArrayList<String> cn = new ArrayList<>();
            ArrayList<Double> q = new ArrayList<>();

            while (resultSet.next()) {
                pn.add(resultSet.getString("productName"));
                pl.add(resultSet.getString("productLine"));
                od.add(resultSet.getString("introduction_date"));
                String h = resultSet.getString("customerName").replace("\n", " ");
                cn.add(h);
                q.add(resultSet.getDouble("quantityOrdered"));
            }
            String result2 = "";
            str1 = "\t<product_list>\n";
                str2 = str2 + "\t\t<product>\n";

                String q3 = "";
                for (int i = 0; i < pn.size(); i++) {
                    q3 = q3 +"\t\t\t<product_name>" + pn.get(i) + " </product_name>\n" +
                             "\t\t\t<product_line_name> " + pl.get(i) + " </product_line_name>\n" +
                             "\t\t\t<product_sales>\n"+
                             "\t\t\t\t<product_name> " + pn.get(i) + " </product_name>\n" +
                             "\t\t\t\t<intro_date> " + od.get(i) + " </intro_date>\n" +
                             "\t\t\t\t<customer_sales>\n"+
                             "\t\t\t\t\t<customer_name> " + cn.get(i) + " </customer_name>\n" +
                             "\t\t\t\t\t<units_sold> " + q.get(i) + " </units_sold>\n" +
                             "\t\t\t\t</customer_sales>\n"+
                             "\t\t\t</product_sales>\n";
                }
                str2 = str2 + q3 + "\t\t</product>\n";

            str1 = str1 + str2 + "\t</product_list>\n";
            result2 = result2 + str1;

            str1 = "";
            str2 = "";
//--------------------------------------------------------------
            //QUERY3
            //3. Office information
            // a. Report, for each city office, their city, territory, number of staff at the office,
            // number of new customers in the given period, and the value of sales to new
            // customers in the given period

            resultSet = statement.executeQuery("SELECT \n" +
                    "    a.city,\n" +
                    "    a.territory,\n" +
                    "    COUNT(b.employeeNumber) AS employee_count,\n" +
                    "    c.customerName,\n" +
                    "    SUM(e.quantityOrdered*e.priceEach) as customer_sales_value\n" +
                    "FROM\n" +
                    "    offices a,\n" +
                    "    employees b,\n" +
                    "    customers c,\n" +
                    "    orders d,\n" +
                    "    orderdetails e\n" +
                    "WHERE\n" +
                    "    a.officeCode = b.officeCode\n" +
                    "\tAND b.employeeNumber = c.salesRepEmployeeNumber\n" +
                    "    and c.customerNumber=d.customerNumber\n" +
                    "    and d.orderNumber=e.orderNumber\n" +
                    "    and d.orderDate BETWEEN "+ "'" + start+ "'" + "and " + "'" + end + "' " +
                    "GROUP BY a.city;");
//Arraylist to store the data.
            ArrayList<String> city = new ArrayList<>();
            ArrayList<String> territory = new ArrayList<>();
            ArrayList<Integer> employee_count = new ArrayList<>();
            ArrayList<String> customerName = new ArrayList<>();
            ArrayList<Double> customer_sales_value = new ArrayList<>();

            while (resultSet.next()) {
                city.add(resultSet.getString("city"));
                territory.add(resultSet.getString("territory"));
                employee_count.add(resultSet.getInt("employee_count"));
                String str = resultSet.getString("customerName").replace("\n", " ");
                customerName.add(str);
                customer_sales_value.add(resultSet.getDouble("customer_sales_value"));
            }

            resultSet.close();

            String result3 = "";
            str1 = "\t<office_list>\n";
            str2 = "";
            for (int i = 0; i < city.size(); i++) {

                str2 = str2 +
                        "\t\t<office_city> " + city.get(i) + "</office_city>\n" +
                        "\t\t<territory>> " + territory.get(i) + "</territory>>\n" +
                        "\t\t<employee_count>> " + employee_count.get(i) + "</employee_count>>\n" +
                        "\t\t<new_customer>\n" +
                        "\t\t\t<customer_name> " + customerName.get(i) + " </customer_name>\n" +
                        "\t\t\t<customer_sales_value> " + customer_sales_value.get(i) + " </customer_sales_value>\n" +
                        "\t\t</new_customer>\n" ;
            }
            str1 = str1 + str2 + "\t</office_list>\n";
            result3 = result3 + str1;
            return result1 + result2 + result3;
        }
        catch (Exception e) {
            System.out.println("Connection failed");
            System.out.println(e.getMessage());
            return null;
        }
    }


    public static String printString(String xmlstr, String start, String end) {
        String str1 = "<?xml version=”1.0” encoding=”UTF-8” ?> \n";
        String str2 = "<time_period_summary> \n" +
                "\t<year> \n" +
                "\t\t<start_date>" + start + " </start_date> \n" +
                "\t\t<end_date>" + end + "</end_date> \n" +
                "\t</year> \n";

        return str1 + str2 + xmlstr + "</time_period_summary> \n";
    }
    public static boolean Writexml(String w, String file) {
        //reference from https://www.w3schools.com/java/java_files_create.asp
        File newf = new File(file);
        try {
            newf.createNewFile();
            FileWriter fw = new FileWriter(newf);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(w);
            bw.close();
            return true;

        } catch (IOException e) {
            System.out.println("Error!");
            e.printStackTrace();
            return false;
        }
    }
}
