import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.File;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;

class MyObject {
    private int intValue;
    private double doubleValue;

    public MyObject(int intValue, double doubleValue) {
        this.intValue = intValue;
        this.doubleValue = doubleValue;
    }

    public int getDay() {
        return intValue;
    }

    public double getData() {
        return doubleValue;
    }
}

public class HtmlParser {
    public static void fileCopy() {
        String sourceFileName = "data.csv";
        String destinationFileName = "output.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine(); 
            }
            System.out.println("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String id, int start, int end, ArrayList<Double> result) {
        String fileName = "output.csv";
        String output = "";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true))) {
            writer.write(id + "," + start + "," + end + "\n");
            for (int i = 0; i < result.size(); i++) {
                String temp = String.format("%.2f",result.get(i));
                temp = removeZeros(temp);
                output = output + temp + " ";
                //System.out.println(output);
            }
            writer.write(editString(output,0) + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void simpleWrite(String id, int start, int end, String result) {
        String fileName = "output.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true))) {
            writer.write(id + "," + start + "," + end + "\n" + result + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String removeZeros(String number) {
        if (number.contains(".")) {
            while (number.endsWith("0")) {
                number = number.substring(0, number.length() - 1);
            }
            if (number.endsWith(".")) {
                number = number.substring(0, number.length() - 1);
            }
        }
        return number;
    }

    public static String editString(String input, int mode) {

            input = input.trim();
            input = input.replaceAll("\\s+",",");
            return input;
    }

    public static LinkedHashMap<String, ArrayList<MyObject>> fileReader() {
        String sourceFileName = "data.csv";
        LinkedHashMap<String, ArrayList<MyObject>> map = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
            String line;
            String[] ticker = {};

            int day = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if(day == 0) {
                    ticker = line.split(",");
                } else {
                    int i = 0;
                    String[] num = line.split(",");
                    for(String nums : num) {
                        if(!line.isEmpty()) {
                        double value = Double.parseDouble(nums);
                        MyObject obj = new MyObject(day, value);
                        map.computeIfAbsent(ticker[i++],k -> new ArrayList<>()).add(obj);
                        }
                    }
                    /*for (Map.Entry<String, ArrayList<MyObject>> entry : map.entrySet()) {
                        String symbol = entry.getKey();
                        ArrayList<MyObject> dataList = entry.getValue();
                        System.out.println("Symbol: " + symbol);
                        for (MyObject obj : dataList) {
                            System.out.println("Day: " + obj.getDay() + ", Value: " + obj.getData());
                        }
                    }
                    
                    String fileName = "data2.csv";
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                        for (Map.Entry<String, ArrayList<MyObject>> entry : map.entrySet()) {
                            String symbol = entry.getKey();
                        ArrayList<MyObject> dataList = entry.getValue();
                        writer.write("Symbol: " + symbol + "\n");
                        for (MyObject obj : dataList) {
                            writer.write("Day: " + obj.getDay() + ", Value: " + obj.getData() + "\n");
                        }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
                day++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void movAvg(String id, int start, int end, LinkedHashMap<String, ArrayList<MyObject>>dataMap) {
        ArrayList<MyObject> targetObj = dataMap.get(id);
        LinkedHashMap<Integer, Double> values = new LinkedHashMap<>();//can refer to total days
        ArrayList<Double> avg = new ArrayList<>();
        if (targetObj != null) {
            for (MyObject obj : targetObj) {
                int day = obj.getDay();
                double value = obj.getData();
                values.put(day, value);
                //System.out.println(day + ": " + value);
            }
        }
        for(int i = start; i <= end - 4; i++) {
            double temp = (values.get(i) + values.get(i+1) + values.get(i+2) + values.get(i+3) + values.get(i+4))/5.0;
            //System.out.println(temp);
            avg.add(temp);
        }
        writeFile(id, start, end, avg);
    }

    /*public static double powerValue(double base, int exponent) {
        if(exponent == 0) {
            return 1;
        } else if(exponent > 0) {
            return base * powerValue(base, exponent - 1);
        } else {
            return 1 / (base * powerValue(base, exponent - 1));
        }
    }*/

    public static Double avgValue(String id, int start, int end, LinkedHashMap<String, ArrayList<MyObject>>dataMap) {
        ArrayList<MyObject> targetObj = dataMap.get(id);
        LinkedHashMap<Integer, Double> values = new LinkedHashMap<>();//can refer to total days
        double numDays = end - start + 1;
        double temp = 0.0;
        if (targetObj != null) {
            for (MyObject obj : targetObj) {
                int day = obj.getDay();
                double value = obj.getData();
                values.put(day, value);
                //System.out.println(day + ": " + value);
            }
        }
        for(int i = start; i <= end; i++) {
            temp = temp + values.get(i);
            //System.out.println(temp);
        }
        double result = temp/numDays;
        return result;
    }

    public static double stdDev(String id, int start, int end, LinkedHashMap<String, ArrayList<MyObject>>dataMap, int task) {
        double expValue = avgValue(id, start, end, dataMap);
        ArrayList<MyObject> targetObj = dataMap.get(id);
        LinkedHashMap<Integer, Double> values = new LinkedHashMap<>();//can refer to total days
        double temp = 0;
        double numDays = end - start + 1;
        if (targetObj != null) {
            for (MyObject obj : targetObj) {
                int day = obj.getDay();
                double value = obj.getData();
                values.put(day, value);
                //System.out.println(day + ": " + value);
            }
        }
        for(int i = start; i <= end; i++) {
            temp = temp + Math.pow(values.get(i) - expValue, 2);
            //System.out.println(temp);
        }
        double result = Math.pow(temp/(numDays - 1), 0.5);
        if(task == 2) {
            String resultString = String.format("%.2f",result);
            resultString = removeZeros(resultString);
            simpleWrite(id, start, end, resultString);
        }
            return result;
    }

    public static void compareSize(LinkedHashMap<String, Double> stdDevMap, int start, int end) {
        int cnt = 0;
        String topKey = null;
        double topValue = 0;
        String secKey = null;
        double secValue = 0;
        String thirdKey = null;
        double thirdValue = 0;
        for(Map.Entry<String, Double> entry : stdDevMap.entrySet()) {
            cnt = 0;
            double value = entry.getValue();
            String key = entry.getKey();
            //System.out.println(value);
            for(Map.Entry<String, Double> enter : stdDevMap.entrySet()) {
                double compareValue = enter.getValue();
                if(value < compareValue) {
                    cnt++;
                }
                if(cnt >= 3)break;
            }
            //System.out.println(cnt);
            if(cnt < 3) {
                //can also store the key and value via hashmap
                if(cnt == 0) {
                    topKey = key;
                    topValue = value;
                }
                if(cnt == 1) {
                    secKey = key;
                    secValue = value;
                }
                if(cnt == 2) {
                    thirdKey = key;
                    thirdValue = value;
                }
            }
        }
        String title = topKey + "," + secKey + "," + thirdKey;
        String topV = String.format("%.2f", topValue);
        String secV = String.format("%.2f", secValue);
        String thirdV = String.format("%.2f", thirdValue);
        topV = removeZeros(topV);
        secV = removeZeros(secV);
        thirdV = removeZeros(thirdV);
        String result = topV + "," + secV + "," + thirdV;
        simpleWrite(title, start, end, result);
    }

    public static double avgTime(int start, int end) {
        double begin = start;
        double last = end;
        return (begin + last)/2 ;
    }

    public static void linearReg(String id, int start, int end, LinkedHashMap<String, ArrayList<MyObject>>dataMap) {
        double expValue = avgValue(id, start, end, dataMap);
        double avg = avgTime(start, end);
        ArrayList<MyObject> targetObj = dataMap.get(id);
        LinkedHashMap<Integer, Double> values = new LinkedHashMap<>();//can refer to total days
        double tmpNume = 0;
        double tmpDemo = 0;
        if (targetObj != null) {
            for (MyObject obj : targetObj) {
                int day = obj.getDay();
                double value = obj.getData();
                values.put(day, value);
                //System.out.println(day + ": " + value);
            }
        }
        for(int i = start; i <= end; i++) {
            tmpNume = tmpNume + ((i - avg)*(values.get(i) - expValue));
            tmpDemo = tmpDemo + Math.pow(i - avg, 2);
        }
        double slope = tmpNume/tmpDemo;
        double intercept = expValue - slope*avg;
        String slopeString = String.format("%.2f",slope);
        String interceptString = String.format("%.2f",intercept);
        slopeString = removeZeros(slopeString);
        interceptString = removeZeros(interceptString);
        String resultString = slopeString + "," + interceptString;
        //System.out.println(resultString);
        simpleWrite(id, start, end, resultString);
    }

    public static void tickerSymbol() {
        try {
            Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
            System.out.println(doc.title());

            Elements tdNumber = doc.select("td");
            Elements thAlpha = doc.select("th");

            String ticker = thAlpha.text();
            String num = tdNumber.text();
            ticker = editString(ticker,0);
            num = editString(num,0);

            String fileName = "data.csv";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                File file = new File(fileName);
                if(file.length() == 0) {
                    writer.write(ticker);
                }
                writer.write("\n" + num);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(ticker + num);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LinkedHashMap<String, ArrayList<MyObject>> data = new LinkedHashMap<>();
        if(args.length < 2) {
            int mode = Integer.parseInt(args[0]);
            if(mode == 0) {
                tickerSymbol();
            }
        } else if(args.length < 3) {
            int mode = Integer.parseInt(args[0]);
            int task = Integer.parseInt(args[1]);
            if(mode == 1) {
                data = fileReader();
                if(task == 0) {
                    fileCopy();
                }
            }
        } else if(args.length >= 3) {
            int mode = Integer.parseInt(args[0]);
            int task = Integer.parseInt(args[1]);
        String ticker = args[2];
        int start = Integer.parseInt(args[3]);
        int end = Integer.parseInt(args[4]);
        if(mode == 0) {
            tickerSymbol();
        }
        if(mode == 1) {
            data = fileReader();
            if(task == 0) {
                fileCopy();
            }
            if(task == 1) {
                movAvg(ticker, start, end, data);
            }
            if(task == 2) {
                stdDev(ticker, start, end, data, task);
            }
            if(task == 3) {
                LinkedHashMap<String, Double> stdDevMap = new LinkedHashMap<>();
                for(Map.Entry<String, ArrayList<MyObject>> entry : data.entrySet()) {
                    String symbol = entry.getKey();
                    stdDevMap.put(symbol, stdDev(symbol, start, end, data, task));
                }
                compareSize(stdDevMap, start, end);
            }
            if(task == 4) {
                linearReg(ticker, start, end, data);
            }
        }
    }
    }
}
