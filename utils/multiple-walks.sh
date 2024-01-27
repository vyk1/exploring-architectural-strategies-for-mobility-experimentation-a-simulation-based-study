: <<COMMENT
Creates multiple copies of given user location dataset
COMMENT

source="3.csv"

destination="dataset/official/comparative/ops/3/"

cd $destination

for i in {1..25}
do
    cat $source >> "usersLocation-melbCBD_${i}.csv"
done

