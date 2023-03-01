# Commands cheatsheet

After a simulation run, your configuration output will be at `/dataset/`. From root, you can use the following command to copy it the formatted file (`number of users-mobility pattern-topology.csv`). Eg: `5-1-1.csv` is for 5 users, 1st mobility pattern and 1st topology. 


Example from inside the `dataset/results/comparative/ips`:

`cat ../../../output_path_data.csv >> 25-1-1.csv`

### Multiplicating users?

Use the bash file `multiple-walks.sh`