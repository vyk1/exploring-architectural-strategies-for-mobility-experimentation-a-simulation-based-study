# About 
This repository houses the crucial experiments conducted during my [Masters' Thesis](https://github.com/vyk1/service-placements-fog-and-mobility-a-study-towards-the-state-of-the-art), which were presented at the [SAC 2024 event](https://doi.org/10.1145/3605098.3635933).

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

## Citation
```
@inproceedings{10.1145/3605098.3635933,
author = {Botelho Martins, Victoria and Macedo, Douglas and Pioli Junior, Laercio},
title = {Exploring Architectural Strategies for Mobility Experimentation: A Simulation-Based Study},
year = {2024},
isbn = {9798400702433},
publisher = {Association for Computing Machinery},
address = {New York, NY, USA},
url = {https://doi.org/10.1145/3605098.3635933},
doi = {10.1145/3605098.3635933},
abstract = {This study conducts a comparative analysis of indoor and outdoor positioning systems, focusing on architectural approaches' efficiency, resource utilization, and scalability. It employs microservice clustered-fog architecture and edgewards-fog for comparison, assessing performance using metrics like application loop delay (ALD), network usage (NU), and migration time (MT). Results favor the clustered approach for its consistency, stability, efficiency, and lower resource use. It also highlights the influence of clusters and regions on system performance, with fewer regions performing better for fewer users and multiple regions for more users. This emphasizes regionality in architectural design. However, these findings are context-specific, requiring validation in diverse scenarios. In summary, this study offers insights into architectural performance in positioning systems, aiding efficient and scalable designs.},
booktitle = {Proceedings of the 39th ACM/SIGAPP Symposium on Applied Computing},
pages = {1749–1756},
numpages = {8},
keywords = {fog computing, indoor positioning systems, mobility},
location = {<conf-loc>, <city>Avila</city>, <country>Spain</country>, </conf-loc>},
series = {SAC '24}
}
```

## SAC presentation

Please, refer [to](./SAC___presentation.pdf).