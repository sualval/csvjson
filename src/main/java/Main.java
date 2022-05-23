import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {


    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        String fileNameXML = "data.xml";
        String fileJson = "new_dataCSV.json";
        String fileJson1 = "new_dataXML.json";
//task1
        List<Employee> list = parseCSV(columnMapping, fileNameCSV);
        writeString(listToJson(list), fileJson);
//task2
        List<Employee> listXML = parseXML(fileNameXML);
        writeString(listToJson(listXML), fileJson1);
//task3
        String json = readString(fileJson);
        jsonToList(json).forEach(System.out::println);

    }


    private static String readString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();


    }

    private static List<Employee> jsonToList(String json) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, listType);

    }

    private static List<Employee> parseXML(String fileName) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Node node = document.getDocumentElement();
            return read(node);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(fileName)).build()) {
            ColumnPositionMappingStrategy<Employee> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnPositionMappingStrategy.setColumnMapping(columnMapping);
            columnPositionMappingStrategy.setType(Employee.class);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(columnPositionMappingStrategy).build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(list);
    }


    private static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> read(Node node) {
        HashMap<String, String> hm = new HashMap<>();
        List<Employee> employees = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeName().equals("employee")) {
                NodeList nodeListEmployee = currentNode.getChildNodes();
                for (int j = 1; j < nodeListEmployee.getLength(); j++) {
                    Node nodeEmployee = nodeListEmployee.item(j);
                    if (nodeEmployee.getNodeType() == nodeEmployee.ELEMENT_NODE) {
                        String key = nodeEmployee.getNodeName();
                        String value = nodeEmployee.getTextContent();
                        hm.put(key, value);
                    }
                }
                try {
                    employees.add(new Employee(Long.parseLong(hm.get("id")), hm.get("firstName"), hm.get("lastName"), hm.get("country"), Integer.parseInt(hm.get("age"))));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return employees;
    }
}








