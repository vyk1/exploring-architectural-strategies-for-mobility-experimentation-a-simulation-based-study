
source="ips2.csv"

destination="dataset/official/comparative/ips/2/"

cd $destination

for i in {1..25}
do
    cat $source >> "usersLocation-melbCBD_${i}.csv"
done


