# About 
This repository houses the crucial experiments conducted during my [Masters' Thesis](https://github.com/vyk1/service-placements-fog-and-mobility-a-study-towards-the-state-of-the-art), which were presented at the [SAC 2024 event](https://www.sigapp.org/sac/sac2024/).

For additional details, please refer to the aforementioned [link](https://github.com/vyk1/service-placements-fog-and-mobility-a-study-towards-the-state-of-the-art).

## Changes made from iFogSim [v2.0.0](https://github.com/Cloudslab/iFogSim/releases/tag/v2.0.0):

### New Classes & Packages

- **RandomMobilityGenerator.java:** Enables IPS experiments with random movement generation.
- **References.java:** Configures mobility dataset references, including north-south and mobility patterns.
- **DataParser.java:** Configures fog topologies references files.
- **Package org.fog.test.perfeval.ips:** Contains main classes that execute the proposed experiments.

## New Directories

- **/docs:** Markdown-based documenptation directory.
- **/diagrams:** MermaidJS markdown diagrams developed to illustrate clustered and edgewards approaches.
- **/dataset/:** Refactored experiment directory
    - **/edgeResources:** Edge resources CSV files for IPS and OPS experiments.
    - **/official:** User interactions (walks) CSV files.
    - **/results:** Log CSV files created during each experiment run. Format: ALD;COST IN CLOUD;NU;MT.
- **/utils:** Mostly NodeJS files to expedite development.

For additional cheats, please refer to the [cheats file](/docs/cheats.md) (:

For logging results, [click here](/docs/results-logging.md).