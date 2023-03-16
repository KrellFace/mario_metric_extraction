package metricExtraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.List;
import java.util.EnumSet;
import java.util.Scanner;
        
public class Main_LevelMetricExtraction {

    //static String inputFolders = "C:\\Users\\owith\\Documents\\PhD Work\\ERA Metric Evaluation\\Code Repo\\mario_metric_extraction\\levels\\";
    //static String outputFileName = "C:\\Users\\owith\\Documents\\PhD Work\\ERA Metric Evaluation\\Code Repo\\mario_metric_extraction\\output\\MetricExtraction\\TEST_Renames.csv";

    static String inputFolders = ".\\levels\\";
    static String outputFileName = ".\\output\\MetricExtraction\\TEST_Refactor2.csv";



    //Number of game ticks given to the agent to try and complete the level
    static int ticksPerRun = 10;


    //Number of levels to be evaluated per folder. Set arbitrarily high to evaluate all (>1000 for the Mario AI Benchmark set)
    static int levelsPerFolder = 4;
    
    public static void main(String[] args)  throws IOException{

        calcAndSaveMetrics(inputFolders, outputFileName);

    }

    private static void calcAndSaveMetrics(String levelsFolder, String outputFolder)  throws IOException{
        //File[] generatorFolders = new File("C:\\Users\\owith\\Documents\\PhD Work\\IllumMario Platform\\levels\\").listFiles(File::isDirectory);
        File[] generatorFolders = new File(levelsFolder).listFiles(File::isDirectory);

        List<String[]> outputData = new ArrayList<String[]>();

        EnumSet<enum_MarioMetrics> metricsToExtract = EnumSet.allOf(enum_MarioMetrics.class);

        //Generate the header of the output csv
        String[] firstLine = new String[metricsToExtract.size()+2];
        firstLine[0] = "LevelName"; firstLine[1] = "Generator";  

        int iter = 0;
        for(enum_MarioMetrics metric: metricsToExtract){
            firstLine[iter+2] = metric.name();
            iter+=1;
        }
        outputData.add(firstLine);

        //Calculate and save level metrics
        for (int q = 0; q<generatorFolders.length; q++){
            File[] files = generatorFolders[q].listFiles();
            System.out.println("Processing generator: " + generatorFolders[q].getName());

            int filesToGet = Math.min(files.length, levelsPerFolder);

            for (int i = 0; i<filesToGet;i++){
                char[][] cr = fileToCharRep(files[i]);
                String s = stringRepFromCharRep(cr);
                MarioLevelWrapper lvl = new MarioLevelWrapper(s, false);
                MetricsWrapper lvlwrp = new MetricsWrapper(files[i].getName(), ticksPerRun, lvl);
                lvlwrp.runAgentNTimes((int)5);

                String[] levelLine = new String[metricsToExtract.size()+2];
                levelLine[0] = lvlwrp.getName(); levelLine[1] = generatorFolders[q].getName();  
                
                int subIter = 0;
                for(enum_MarioMetrics metric: metricsToExtract){
                    levelLine[subIter+2] = String.valueOf(lvlwrp.GetMetricValue(metric));
                    subIter+=1;
                }

                outputData.add(levelLine);
            }

            File outCSV = new File(outputFolder);
            FileWriter fileWriter = new FileWriter(outCSV);
            for (int i = 0; i<outputData.size(); i++){
                StringBuilder line = new StringBuilder();
                String[] row = outputData.get(i);
                //System.out.println("Next row: " + row);
                for (int j = 0; j<row.length;j++){
                    line.append("\"");
                    line.append(row[j].replaceAll("\"","\"\""));
                    line.append("\"");
                    
                    line.append(',');
                    
                }
                line.append("\n");
                fileWriter.write(line.toString());

            }
            fileWriter.close();
        }
    }

    public static char[][] fileToCharRep(File fileLevel) {
        char[][] a = null;
        try {
            Scanner scanner = new Scanner(new FileInputStream(fileLevel));
            String line;
            ArrayList < String > lines = new ArrayList < > ();
            int width = 0;
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                width = line.length();
                lines.add(line);
            }
            a = new char[lines.size()][width];
            for (int y = 0; y < lines.size(); y++) {
                for (int x = 0; x < width; x++) {
                    a[y][x] = lines.get(y).charAt(x);
                }
            }          
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }   

    public static String stringRepFromCharRep(char[][] levelRep) {

        String output = "";

        char nulls = '\u0000';

        for (int y = 0; y < levelRep.length; y++) {
            for (int x = 0; x < levelRep[y].length; x++) {

                //Handling for blank cells
                if (levelRep[y][x] == nulls) {
                    output += "-";
                } else {
                    output += String.valueOf(levelRep[y][x]);
                }
                if (x == levelRep[y].length - 1) {
                    output += "\n";
                }
            }
        }
        return output;
    }

    public static char[][] charRepFromString(String stringRep) {

        String[] lines = stringRep.split("\n");

        char[][] charRep = new char[lines.length][lines[0].length()];

        for (int y = 0; y < charRep.length; y++) {
            for (int x = 0; x < charRep[y].length; x++) {

                Character c = lines[y].charAt(x);
                charRep[y][x] = c;

            }
        }
        return charRep;
    }

}
        