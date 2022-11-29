package metricExtraction;

public enum enum_MarioMetrics {
    
    //Fitness Metrics
    Playability,

    //Structural Metrics
    Contiguity,
    //AdjustedContiguity,
    Linearity,
    Density,
    //ClearRows,
    ClearColumns,
    PipeCount,
    BlockCount,
    EmptySpaceCount,
    EnemyCount,
    RewardCount,
    
    //Agent Extracted Metrics
    JumpCount,
    JumpEntropy,
    Speed,
    TimeTaken,
    TotalEnemyDeaths,
    KillsByStomp,
    //KillsOverEnemies,
    MaxJumpAirTime,
    OnGroundRatio,
    AverageY

}
