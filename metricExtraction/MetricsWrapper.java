package metricExtraction;

import java.util.Hashtable;

import agents.robinBaumgarten.Agent;
import engine.core.MarioAgentEvent;
import engine.core.MarioGame;
import engine.core.MarioResult;
import java.util.*;

public class MetricsWrapper {

    private String name;
    private MarioLevelWrapper level;
    private Hashtable<enum_MarioMetrics, Float> metricVals = new Hashtable<enum_MarioMetrics, Float>();
    private int widthCells;
    private int heightCells;

    private int ticksPerRun;

    private static List<Character> solidChars = Arrays.asList('X','#','%','D','S', 't');
    private static List<Character> rewardChars = Arrays.asList('C','L','U','@','!','2','1');
    private static List<Character> enemyChars = Arrays.asList('y','Y','g','G','k','K','r','R');
    private static List<Character> standableChars = Arrays.asList('-', 'M', 'F', '|','*','B','b');
    private static List<Character> pipeChars = Arrays.asList('t','T');



    //Constructor for creating with a specified but unevaluated level
    public MetricsWrapper(String name, int ticksPerRun,  MarioLevelWrapper level) {
        //this.config = config;
        this.name = name;
        this.level = level;
        this.ticksPerRun = ticksPerRun;
        //this.fitness = 0f;
        this.widthCells = level.tileWidth;
        this.heightCells = level.tileHeight;
        

        this.updateLevelFeatures();
    }


    public void updateLevelFeatures() {
        //this.blockCount = level.getBlockCount();
        char[][] charRep = Main_LevelMetricExtraction.charRepFromString(level.getStringRep());
        
        //Instantiate local feature scores
        float bc = 0;
        float contig = 0;
        float linearity = 0;
        float density = 0;
        float enemyCount = 0;
        float pipeCount = 0;
        float rewardCount = 0;
        float clearColumns = 0;
        float emptySpace = 0;
        //String floor = "X";
        
        //Looping through every block, row by row
        for (int y = 0; y < charRep.length; y++) {
            for (int x = 0; x < charRep[y].length; x++) {

                //Solid Tile Detected
                if(solidChars.contains(charRep[y][x])){
                    bc += 1;

                    //Check adjacent tiles

                    //UP
                    if (y > 0) {
                        if(solidChars.contains(charRep[y - 1][x])){
                            contig +=1;
                        }
                        else if(standableChars.contains(charRep[y - 1][x])){
                            density+=1;
                        }
                    }
                    //DOWN
                    if (y < charRep.length - 1 ) {
                        if(solidChars.contains(charRep[y + 1][x])){
                            contig +=1;
                        }

                    }
                    //LEFT
                    if (x > 0) {
                        if(solidChars.contains(charRep[y][x-1])){
                            contig +=1;
                            linearity+=1;
                        }
                    }
                    //RIGHT
                    if (x < charRep[y].length - 1) {
                        if(solidChars.contains(charRep[y][x + 1])){
                            contig +=1;
                            linearity+=1;
                        }
                    }
                }
                //Enemy Detected
                if(enemyChars.contains(charRep[y][x])){
                    enemyCount+=1;
                }                
                //Empty Space Detected
                if(standableChars.contains(charRep[y][x])){
                    emptySpace+=1;
                }
                //Reward Tile Detected
                if(rewardChars.contains(charRep[y][x])){
                    rewardCount+=1;
                }
                //Pipe Tile Detected
                if(pipeChars.contains(charRep[y][x])){
                    pipeCount+=1;
                }
            }

        }

        //Loop through each column
        for (int x = 0; x < charRep[0].length; x++) {
            Boolean colClear = true;
            for (int y = 0; y < charRep.length; y++) {

                //Empty Space Detected
                if(!standableChars.contains(charRep[y][x])){
                    colClear=false;
                }

            }
            if (colClear) {
                clearColumns += 1;
            }

        }
        
        metricVals.put(enum_MarioMetrics.BlockCount,bc);
        metricVals.put(enum_MarioMetrics.ClearColumns, clearColumns);
        metricVals.put(enum_MarioMetrics.Contiguity,contig);
        metricVals.put(enum_MarioMetrics.Linearity,linearity);
        metricVals.put(enum_MarioMetrics.Density,(density/(float)charRep[0].length));
        metricVals.put(enum_MarioMetrics.EnemyCount, enemyCount);
        metricVals.put(enum_MarioMetrics.EmptySpaceCount, emptySpace);
        metricVals.put(enum_MarioMetrics.PipeCount, pipeCount);
        metricVals.put(enum_MarioMetrics.RewardCount, rewardCount);

    }

    private void updateResultFeatures(MarioResult result) {

        metricVals.put(enum_MarioMetrics.Playability,(float)result.getCompletionPercentage());
        metricVals.put(enum_MarioMetrics.JumpCount,(float)result.getNumJumps());
        metricVals.put(enum_MarioMetrics.JumpEntropy,(float) result.getNumJumps() / (float) result.getAgentEvents().size());metricVals.put(enum_MarioMetrics.TimeTaken,(float)(ticksPerRun - (result.getRemainingTime() / 1000)));
        metricVals.put(enum_MarioMetrics.Speed,(((float)result.getCompletionPercentage()*1000) / (float)((ticksPerRun*1000) - result.getRemainingTime())));
    }
    //Run the a* agent to update our attributes
    public void runAgent() {
        Agent agent = new Agent();
        runAgent(agent);
    }

    //Run the a* agent to update our attributes
    public void runAgent(Agent agent) {

        MarioResult result = new MarioGame().runGame(agent, level.getStringRep(), ticksPerRun);
    	
    	//RUN VISIBLE
    	//result = new MarioGame().runGame(agent, level.getStringRep(), config.ticksPerRun, 0, true);

        updateResultFeatures(result);
    }

    public void runAgentNTimes(int n){
        float totPlayability = 0f;
        float totJumpCount = 0f;
        float totJumpEntropy = 0f;
        float totSpeed = 0f;
        float totTimeTaken = 0f;
        float totEnemiesKilled = 0f;
        float totKilledByStomp = 0f;
        float totMaxJumpTime = 0f;
        float totOnGroundRatio = 0f;
        float totAvgY = 0f;

        for (int i = 0; i <n; i++){
            Agent agent = new Agent();
            //Run with no visuals
            MarioResult result = new MarioGame().runGame(agent, level.getStringRep(), ticksPerRun);

            totPlayability+=(float)result.getCompletionPercentage();
            totJumpCount+=(float)result.getNumJumps();
            totJumpEntropy+=(float) (result.getNumJumps() / (float) result.getAgentEvents().size());
            totSpeed+=(float)((result.getCompletionPercentage()*1000) / ((ticksPerRun*1000) - result.getRemainingTime()));
            totTimeTaken+= (float) (ticksPerRun - (result.getRemainingTime() / 1000));
            totEnemiesKilled += (float) (result.getKillsTotal());
            totKilledByStomp += (float) (result.getKillsByStomp());
            totMaxJumpTime += (float) (result.getMaxJumpAirTime());
            totOnGroundRatio += (float) (GetTimeOnGroundRatio(result.getAgentEvents()));
            totAvgY +=(float) (GetAverageYPos(result.getAgentEvents()));
        }
        metricVals.put(enum_MarioMetrics.Playability,(float)totPlayability/n);
        metricVals.put(enum_MarioMetrics.JumpCount,(float)totJumpCount/n);
        metricVals.put(enum_MarioMetrics.JumpEntropy,(float) totJumpEntropy/n);
        metricVals.put(enum_MarioMetrics.Speed,(float)totSpeed/n);
        metricVals.put(enum_MarioMetrics.TimeTaken,(float)totTimeTaken/n);
        metricVals.put(enum_MarioMetrics.TimeTaken,(float)totTimeTaken/n);
        metricVals.put(enum_MarioMetrics.TotalEnemyDeaths, (float)totEnemiesKilled/n);
        metricVals.put(enum_MarioMetrics.KillsByStomp, (float)totKilledByStomp/n);
        metricVals.put(enum_MarioMetrics.MaxJumpAirTime, (float)totMaxJumpTime/n);
        metricVals.put(enum_MarioMetrics.OnGroundRatio, (float)totOnGroundRatio/n);
        metricVals.put(enum_MarioMetrics.AverageY, (float)totAvgY/n);
    }

    private float GetTimeOnGroundRatio(ArrayList<MarioAgentEvent> events){
        float eventsOnGround =0f;
        for (MarioAgentEvent e : events) {
            if (e.getMarioOnGround()) {
                eventsOnGround += 1;
            }
        }
        return (eventsOnGround/events.size());
    }

    private float GetAverageYPos(ArrayList<MarioAgentEvent> events){
        float totY =0f;
        for (MarioAgentEvent e : events) {
            totY+=e.getMarioY();
        }
        return (totY/events.size());
    }

    public MarioLevelWrapper getLevel() {
        return level;
    }

    public float GetMetricValue(enum_MarioMetrics metric){
        return metricVals.get(metric);
    }

    public int getWidth() {
        return widthCells;
    }

    public int getHeight() {
        return heightCells;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String newName) {
    	this.name = newName;
    }

    public char[][] getCharRep() {
        return Main_LevelMetricExtraction.charRepFromString(this.level.getStringRep());
    }

    public void printSummary(){
        System.out.println("Level name: " + this.name);

        for (enum_MarioMetrics metric : enum_MarioMetrics.values()) { 
            System.out.println(metric + " value: " + metricVals.get(metric)); 
        }

    }

}