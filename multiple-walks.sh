
source="1.csv"

destination="dataset/official/comparative/ops/1/"

cd $destination

for i in {1..25}
do
    cat $source >> "usersLocation-melbCBD_${i}.csv"
done


