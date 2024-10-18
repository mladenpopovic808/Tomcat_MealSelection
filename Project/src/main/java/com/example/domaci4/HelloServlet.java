package com.example.domaci4;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {

    private String password;
    private static final String [] days = {"monday", "tuesday", "wednesday", "thursday", "friday"}; // za izvlacenje cookie-a

    private  final List<String> mondayFood = new ArrayList<>();
    private  final List<String> tuesdayFood = new ArrayList<>();
    private  final List<String> wednesdayFood = new ArrayList<>();
    private  final List<String> thursdayFood = new ArrayList<>();
    private  final List<String> fridayFood = new ArrayList<>();
    public static Map<String, Integer> mondayMap = new ConcurrentHashMap<>();
    public static Map<String, Integer> tuesdayMap = new ConcurrentHashMap<>();
    public static Map<String, Integer> wednesdayMap = new ConcurrentHashMap<>();
    public static Map<String, Integer> thursdayMap = new ConcurrentHashMap<>();
    public static Map<String, Integer> fridayMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<HttpSession> sessions = new CopyOnWriteArrayList<>();


    public void init() {
        System.out.println("init method");
        try {
            loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("service method");
        super.service(req, resp);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("doGet method");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if(request.getParameter("password") != null && request.getParameter("password").equals(password)) {
            out.println("<h1>Obroci: <h1>");
            out.println("<h3>Ponedeljak</h3>");

            for(String food: mondayMap.keySet()) out.print("<p>" + food + ": " + mondayMap.get(food) + "</p>");
            out.println("<br>");
            out.println("<h3>Utorak</h3>");

            for(String food: tuesdayMap.keySet()) out.print("<p>" + food + ": " + tuesdayMap.get(food) + "</p>");
            out.println("<br>");
            out.println("<h3>Sreda</h3>");

            for(String food: wednesdayMap.keySet()) out.print("<p>" + food + ": " + wednesdayMap.get(food) + "</p>");
            out.println("<br>");
            out.println("<h3>Cetvrtak</h3>");

            for(String food: thursdayMap.keySet()) out.print("<p>" + food + ": " + thursdayMap.get(food) + "</p>");
            out.println("<br>");
            out.println("<h3>Petak</h3>");

            for(String food: fridayMap.keySet()) out.print("<p>" + food + ": " + fridayMap.get(food) + "</p>");
            out.print("<p />");

            out.println("<form action=\"/hello-servlet\" method=\"post\"><input type=\"submit\" name=\"delete\" value=\"Obrisi\" /></form>");
        }
        else if(request.getSession().getAttribute("alreadySubmitted") != null) {
            out.println("<h1>Izabrali ste sledece obroke:</h1>");
            out.printf("<p>Ponedeljak: "+ request.getSession().getAttribute("monday") + "</p>");
            out.printf("<p>Utorak: "+ request.getSession().getAttribute("tuesday") + "</p>");
            out.printf("<p>Sreda: "+ request.getSession().getAttribute("wednesday") + "</p>");
            out.printf("<p>Cetvrtak: "+ request.getSession().getAttribute("thursday") + "</p>");
            out.printf("<p>Petak: "+ request.getSession().getAttribute("friday") + "</p>");
        }
        else { //korisnik unosi obroke
            out.println("<html><body>");
            out.println("<h1>" + "Izaberite obroke" + "</h1>");
            out.println("<form action=\"/hello-servlet\" method=\"post\">");

            out.println(" <label> Ponedeljak:</label>");
            out.println("<select name=\"monday\">");
            for (String s : mondayFood) {
                out.println("<option value=\"" + s + "\">" + s + "</option>");
            }
            out.println("</select> <p/>");

            out.println(" <label> Utorak:</label>");
            out.println("<select name=\"tuesday\">");
            for (String s : tuesdayFood) {
                out.println("<option value=\"" + s + "\">" + s + "</option>");
            }
            out.println("</select> <p/>");

            out.println(" <label> Sreda:</label>");
            out.println("<select name=\"wednesday\">");
            for (String s : wednesdayFood) {
                out.println("<option value=\"" + s + "\">" + s + "</option>");
            }
            out.println("</select> <p/>");

            out.println(" <label> Cetvrtak:</label>");
            out.println("<select name=\"thursday\">");
            for (String s : thursdayFood) {
                out.println("<option value=\"" + s + "\">" + s + "</option>");
            }
            out.println("</select> <p/>");
            out.println(" <label> Petak:</label>");
            out.println("<select name=\"friday\">");
            for (String s : fridayFood) {
                out.println("<option value=\"" + s + "\">" + s + "</option>");
            }
            out.println("</select> <p/>");
            out.println("<input type=\"submit\" name=\"submit\" value=\"submit\"> </input> </form>");
            out.println("</body></html>");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("doPost method");
        if(request.getParameter("delete") != null){ // button parametar u bodiju name:value
            deleteData();
            response.getWriter().println("<h1>Uspesno ste obrisali obroke!</h1>");
        }
        else {
            if(request.getSession().getAttribute("alreadySubmitted") != null){
                response.getWriter().println("<h1>Vec si porucio hranu za ovu nedelju!</h1>");
                return;
            }

            setCookies(request);//ponedeljak:pica...
            if(!sessions.contains(request.getSession())){
                sessions.add(request.getSession());//da bih mogao da izbrisem sesije
            }
            putInTheMap(mondayMap, request.getParameter("monday"));
            putInTheMap(tuesdayMap, request.getParameter("tuesday"));
            putInTheMap(wednesdayMap, request.getParameter("wednesday"));
            putInTheMap(thursdayMap, request.getParameter("thursday"));
            putInTheMap(fridayMap, request.getParameter("friday"));

            response.sendRedirect("/hello-servlet");
        }
    }
    private void setCookies(HttpServletRequest request){
        for (String day: days){
            request.getSession().setAttribute(day, request.getParameter(day)); //ponedeljak:pica
        }
        request.getSession().setAttribute("alreadySubmitted", "yess");
    }
    private void putInTheMap(Map<String, Integer> map, String food){
        map.put(food, map.get(food) + 1);
    }

    private void loadFiles() throws IOException {
        //ne znam zasto ne rade relativne
        File mondayFile = new File("C:\\Users\\Mladen\\Desktop\\FAKS FAKS\\Veb programiranje\\Domaci4 Mladen Popovic RN 97-21\\src\\main\\resources\\ponedeljak.txt");
        File tuesdayFile = new File("C:\\Users\\Mladen\\Desktop\\FAKS FAKS\\Veb programiranje\\Domaci4 Mladen Popovic RN 97-21\\src\\main\\resources\\utorak.txt");
        File wednesdayFile = new File("C:\\Users\\Mladen\\Desktop\\FAKS FAKS\\Veb programiranje\\Domaci4 Mladen Popovic RN 97-21\\src\\main\\resources\\sreda.txt");
        File thursdayFile = new File("C:\\Users\\Mladen\\Desktop\\FAKS FAKS\\Veb programiranje\\Domaci4 Mladen Popovic RN 97-21\\src\\main\\resources\\cetvrtak.txt");
        File fridayFile = new File("C:\\Users\\Mladen\\Desktop\\FAKS FAKS\\Veb programiranje\\Domaci4 Mladen Popovic RN 97-21\\src\\main\\resources\\petak.txt");
        File passwordFile = new File("C:\\Users\\Mladen\\Desktop\\FAKS FAKS\\Veb programiranje\\Domaci4 Mladen Popovic RN 97-21\\src\\main\\resources\\password.txt");

        readFile(mondayFile,mondayFood);
        readFile(tuesdayFile,tuesdayFood);
        readFile(wednesdayFile,wednesdayFood);
        readFile(thursdayFile,thursdayFood);
        readFile(fridayFile,fridayFood);
        readPassword(passwordFile);

        initializeMap(mondayMap, mondayFood);
        initializeMap(tuesdayMap, tuesdayFood);
        initializeMap(wednesdayMap, wednesdayFood);
        initializeMap(thursdayMap, thursdayFood);
        initializeMap(fridayMap, fridayFood);
    }

    private void initializeMap(Map<String, Integer> map, List<String> list){
        for (String s : list)
            map.put(s, 0);
    }
    private void readFile(File file, List<String> list) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            list.add(line);
        }
    }
    private void readPassword(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        password = bufferedReader.readLine();

    }
    //praznimo mape porucenih obroka.
    private void deleteData(){
        Iterator<HttpSession> iterator = sessions.iterator();
        System.out.println("sessions size: " + sessions.size());
        while (iterator.hasNext()){
            deleteSessionAttributes(iterator.next());
        }
        initializeMap(mondayMap, mondayFood);
        initializeMap(tuesdayMap, tuesdayFood);
        initializeMap(wednesdayMap, wednesdayFood);
        initializeMap(thursdayMap, thursdayFood);
        initializeMap(fridayMap, fridayFood);
    }
    private void deleteSessionAttributes(HttpSession session){
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()){
            String attributeName = attributeNames.nextElement();
            session.removeAttribute(attributeName);
        }
    }

    public void destroy() {System.out.println("destroy method");}
}