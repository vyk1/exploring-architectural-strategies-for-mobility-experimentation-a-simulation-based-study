# Useful Cheats (hopefully)


## Converting GMaps markers to iFogSim?

### Gateways and proxies (fog nodes)

1. Export the file from GM
2. Add a new column called "block"
3. Use Regex command `ponto (9,|3,|37,|8,|12,|13,|16,|17,)` to select multiple markers then, add an indicator for the block (it has to be a number), it will be like:
```csv
WKT,nome,block
"POINT (144.9517062 -37.8133123)",Ponto 3,1
```
- if you are using VSCode, you can use crtl+shift+l

IMPORTANT
- beware the "lines" or "ponto" in other languages
- the script only generates ONE proxy per block
- note that the column WKT of Gmaps uses the format LONGITUDE and LATITUTE, as described in the scripts
4. Run `node maps-fog-nodes-to-simulator` to generate the formated file

The output should be as follows:
```
ID,Latitude,Longitude,Block,Level,Parent,State,Details
0,-37.8136,144.9631,0,0,-1,VIC,Cloud Data Center
```

### Devices (edge resources) walks

1. Export the file from GM
- check if your file is in the following format:
```
WKT,nome,
"POINT (144.95362 -37.81282)",Ponto 1,
```

2. Run `node maps-walks-to-simulator.js`

The output should be as follows:
```
Latitude,Longitude
-37.81282, 144.95362
```

### Rainbow CSV

I recommend using the Rainbown CSV in VSCode.

RBQL:

`select a4, count (a4) group by a4 --> count blocks for exp1 and exp2`

