# Useful Cheats (hopefully)

## Converting GMaps markers to iFogSim?

1. Export the file
2. Add a new column called "block"
3. Use Regex command `ponto (9,|3,|37,|8,|12,|13,|16,|17,)` to select multiple markers then, add an indicator for the block (it has to be a number), it will be like:
- if you are using VSCode, you can use crtl+shift+l

```
WKT,nome,block
"POINT (144.9517062 -37.8133123)",Ponto 3,1
```


### Rainbow CSV

I recommend using the Rainbown CSV in VSCode.

RBQL:

`select a4, count (a4) group by a4 --> count blocks for exp1 and exp2`


## Testing if .csv is valid
- parent == level + 1