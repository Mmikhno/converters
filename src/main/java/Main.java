import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.w3c.dom.NodeList;

public class Main {
    public static void main(String[] args) {
        // задача 1: CSV - JSON парсер
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list1 = parseCSV(columnMapping, fileName);
        String json = listToJson(list1);
        writeString(json, "data.json");
        // задача 2: XML - JSON парсер
        List<Employee> listXml = parseXML("data.xml");
        String json2 = listToJson(listXml);
        writeString(json2, "data2.json");
        // задача 3: JSON парсер
        String new_json = readString("data.json");
        List<Employee> listEmployee = jsonToList(new_json);
        for (Employee item : listEmployee) {
            System.out.println(item);
        }
    }

    static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csvToBean.parse();
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static String listToJson(List list) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static List<Employee> parseXML(String fileName) throws RuntimeException {
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                NodeList employees = nodeList.item(i).getChildNodes();
                long id = 0;
                String firstName = null;
                String lastName = null;
                String country = null;
                int age = 0;
                for (int a = 0; a < employees.getLength(); a++) {
                    Node employee = employees.item(a);
                    if (employee.getNodeName().equals("id")) {
                        id = Long.parseLong(employee.getTextContent());
                    }
                    if (employee.getNodeName().equals("firstName")) {
                        firstName = employee.getTextContent();
                    }
                    if (employee.getNodeName().equals("lastName")) {
                        lastName = employee.getTextContent();
                    }
                    if (employee.getNodeName().equals("country")) {
                        country = employee.getTextContent();
                    }
                    if (employee.getNodeName().equals("age")) {
                        age = Integer.parseInt(employee.getTextContent());
                    }
                }
                Employee emp = new Employee(id, firstName, lastName, country, age);
                list.add(emp);
            }
        } catch (ParserConfigurationException | SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    static String readString(String fileName) {
        String line = null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader buf = new BufferedReader(new FileReader(fileName))) {
            while ((line = buf.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    static List<Employee> jsonToList(String fileName) {
        JSONParser parser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        try {
            Object object = parser.parse(fileName);
            JSONArray jsonArray = (JSONArray) object;

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object o : jsonArray) {
                JSONObject obj = (JSONObject) o;
                Employee emp = gson.fromJson(obj.toJSONString(), Employee.class);
                list.add(emp);
            }
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

}

