#!/bin/bash

M=( 1 3 6 10) 
K=( 10 100 1000 10000 )

## generate owl ontologies
# for m in "${M[@]}"
# do
#     echo $m > m.txt
#     for k in "${K[@]}"
#     do
# 	echo $k > k.txt
# 	for n in {1..100}
# 	do
# 	    echo "Generating m=$m k=$k n=$n"
# 	    echo $n > n.txt
# 	    lein run
# 	    mv "n$n.owl" "resources/m$m/k$k"
# 	done
#     done
# done

#clean-up
echo "" > results.txt

## scaling results
for m in "${M[@]}"
do
    echo $m > m.txt
    for k in "${K[@]}"
    do
	echo $k > k.txt
	echo "[$m $k " >> results.txt
	for n in {1..100}
	do
	    echo "Reasoning m=$m k=$k n=$n"
	    echo $n > n.txt
	    cp "resources/m$m/k$k/n$n.owl" ./resources
	    lein run >> results.txt
	    rm "resources/n$n.owl"
	done
    done
done

#clean-up
rm n.txt
rm k.txt
rm m.txt
