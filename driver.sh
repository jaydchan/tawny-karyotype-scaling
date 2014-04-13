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
## TODO default GEN to 1 if does not exist
GEN=1; ## generate base ontologies
STA=1; ## obtain ontology stats
REF=1; ## refine ontologies with affects models
REA=1; ## reason ontologies and note time
GRA=1; ## create graphs

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
		    mkdir -p "./output/without/k$k/m$m/"
		    mv "n$n.owl" $_
		elif [ $a -ge 1 -a $a -le 3 -a $REF = 1 ]; then
		    # else if (1 <= a >= 3) then refine ontology
		    echo "Generating a=$a k=$k m=$m n=$n"
		    cp "./output/without/k$k/m$m/n$n.owl" ./output
		    lein run $a $k $m $n
		    mkdir -p "./output/affects$a/k$k/m$m/"
		    mv "n$n.owl" $_
		    rm "./output/n$n.owl"
		fi
		if [ $a = 0 -a $STA = 1 ]; then
		    cp "./output/without/k$k/m$m/n$n.owl" ./output
		    lein run -3 $k $m $n
		    rm "./output/n$n.owl"
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
    echo -n > ./output/results.txt

    for line in "${LINES[@]}"; do
	PARTS=( $line )
	echo "Reasoning a=${PARTS[0]} k=${PARTS[1]} m=${PARTS[2]} n=${PARTS[3]}"
	cp "output/${AFFECTS[${PARTS[0]}]}/k${PARTS[1]}/m${PARTS[2]}/n${PARTS[3]}.owl" ./output
	echo -n "[${PARTS[0]} ${PARTS[1]} ${PARTS[2]} ${PARTS[3]} " >> ./output/results.txt
	lein run -1 ${PARTS[1]} ${PARTS[2]} ${PARTS[3]} >> ./output/results.txt
	echo "]" >> ./output/results.txt
	rm output/n${PARTS[3]}.owl
    done
    echo "Reasoning complete"
fi

## clean up
rm tasks.txt

## graphs
if [ $GRA = 1 ]; then
    echo "Creating graphs"
    lein run -2
    mkdir -p ./output/graphs/
    mv *.png $_
fi
