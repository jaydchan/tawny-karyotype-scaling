#!/bin/bash

AFFECTS=("without" "affects1" "affects2" "affects3" )
# A=( `seq 0 $((${#AFFECTS[@]}-1))` )
A=( $(eval echo {0..$((${#AFFECTS[@]}-1))} ))

K=( 10 100 1000 10000 ) # 100000

# M=( `seq 0 10` )
# M=( $(eval echo {0..10} ))
M=( 0 5 )

# N=( `seq 1 100` )
# N=( $(eval echo {1..100} ))
N=( 1 2 )

## run driver? YES=1 NO=0
GEN=1; ## generate base ontologies
REF=1; ## refine ontologies with affects models
REA=1; ## reason ontologies and note time

## clean-up
echo -n > tasks.txt

## generate and refine ontologies
for a in "${A[@]}"; do
    for k in "${K[@]}"; do
	for m in "${M[@]}"; do
	    for n in "${N[@]}"; do
		if [ $a = 0 -a $GEN = 1 ]; then
		    # if a == 0, then generate ontology
		    echo "Generating a=$a k=$k m=$m n=$n"
		    lein run $a $k $m $n
		    mv "n$n.owl" "resources/without/k$k/m$m"
		elif [ $a -ge 1 -a $a -le 3 -a $REF = 1 ]; then
		    # else if (1 <= a >= 3) then refine ontology
		    echo "Generating a=$a k=$k m=$m n=$n"
		    cp "resources/without/k$k/m$m/n$n.owl" ./resources
		    lein run $a $k $m $n
		    mv "n$n.owl" "resources/affects$a/k$k/m$m/n$n.owl"
		    rm "resources/n$n.owl"
		fi
		echo "$a $k $m $n" >> tasks.txt ## note tasks
	    done
	done
    done
done

## randomise the tasks
shuf tasks.txt -o tasks.txt

## reason ontologies
readarray LINES < tasks.txt

if [ $REA = 1 ]; then
    ## clean up
    echo -n > results.txt

    for line in "${LINES[@]}"; do
	PARTS=( $line )
	echo "Reasoning a=${PARTS[0]} k=${PARTS[1]} m=${PARTS[2]} n=${PARTS[3]}"
	cp "resources/${AFFECTS[${PARTS[0]}]}/k${PARTS[1]}/m${PARTS[2]}/n${PARTS[3]}.owl" ./resources
	echo -n "[${PARTS[0]} ${PARTS[1]} ${PARTS[2]} ${PARTS[3]} " >> results.txt
	lein run -1 ${PARTS[1]} ${PARTS[2]} ${PARTS[3]} >> results.txt
	echo "]" >> results.txt
	rm resources/n${PARTS[3]}.owl
    done
    echo "Reasoning complete"
fi

## clean up
rm tasks.txt

## graphs
echo "Creating graphs"
lein run -2
mv *.png ./resources/graphs
