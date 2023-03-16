# Mario Metric Extraction

A simple Java based system for calculating and saving phenotypic characteristics of Super Mario Bros levels using the Mario AI Framework.

Takes in sets of Mario levels encoded as .txt files using the Mario AI Framework (https://github.com/amidos2006/Mario-AI-Framework) format, and produces a .csv of the levels with their specified phenotypic characteristics.

## Basic Instalation and Operation

To install, download a copy of this repository into the same directory as a local copy of the Mario AI Framework.

To operate, run 'Main_LevelMetricExtraction.java' after defining a new outputFileName to determine the name of the output csv, and specifying how many levels you want to evaluate per folder with 'levelsPerFolder' (Set arbitrarily high to ensure that all levels from each folder will be evaluated).

Level files are expected to be in nested folders for each individual generator, in a parent directory called 'levels'. 

To add a new metric to calculate, its name would first have to be added to 'enum_MarioMetrics.java', and then the logic for calculating it needs to be added to MetricsWarapper.java.


