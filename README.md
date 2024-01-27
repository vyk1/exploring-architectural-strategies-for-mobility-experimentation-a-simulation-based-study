# About 
This repository holds the most important experiments from my [Masters' Thesis](https://github.com/vyk1/service-placements-fog-and-mobility-a-study-towards-the-state-of-the-art) and was published under the [SAC 2024 event](https://www.sigapp.org/sac/sac2024/).

Please, refer to the previous link for further information.

## Changes made from iFogSim[v2.0.0](https://github.com/Cloudslab/iFogSim/releases/tag/v2.0.0):

### New classes & packages

- RandomMobilityGenerator.java: enables IPS experiments with random movement generation
- References.java: configures mobility dataset references + north south and mobility patterns
- DataParser.java: configuresfog topologies references files
- Package org.fog.test.perfeval.ips: main classes that run the proposed experiments

## New directories

- /docs: markdown-based documentation directory
- /diagrams: mermaidJS markdown diagrams developed to demonstrate clustered and edgewards approaches
- /dataset/: new directory refactored experiment
    - /edgeResources: edge resources csv files for IPS and OPS experiments
    - /official: user interactions (walks) csv files 
    - /results: log csv files created during each experiment run. Format: ALD;COST IN CLOUD;NU;MT
- utils: mostly NodeJS files to accelerate the development

Please refer to the [cheats file](/docs/cheats.md) for further cheats (:

For the logging of the results, [click here](/docs/results-logging.md)
