#!/bin/bash

AFFECTS=("without" "affects1" "affects2" "affects3" )
# A=( `seq 0 $((${#AFFECTS[@]}-1))` )
A=( $(eval echo {0..$((${#AFFECTS[@]}-1))} ))

K=( 10 100 1000 10000 ) # 100000 1000000

# M=( `seq 0 10` )
# M=( $(eval echo {0..10} ))
M=( 0 5 )

# N=( `seq 1 100` )
# N=( $(eval echo {1..100} ))
N=( 1 2 )

## run driver? YES=1 NO=0
## TODO default GEN to 1 if does not exist
GEN=1; ## generate base ontologies
STA=0; ## obtain ontology stats
REF=0; ## refine ontologies with affects models
REA=0; ## reason ontologies and note time
DIF=0; ## generate diff results
GRA=0; ## create graphs

## clean-up -- if exists
if [ $REA = 1 ]; then
    echo -n > tasks.txt
fi
if [ $STA = 1 -a -e ./output/stats.txt ]; then
    echo -n > ./output/stats.txt
fi

## generate and refine ontologies
if [ $GEN = 1 -o $STA = 1 -o $REF = 1 -o $REA = 1 ]; then
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
			mv "n$n.omn" $_
		    elif [ $a -ge 1 -a $a -le 3 -a $REF = 1 ]; then
			# else if (1 <= a >= 3) then refine ontology
			echo "Generating a=$a k=$k m=$m n=$n"
			cp "./output/without/k$k/m$m/n$n.owl" ./output
			lein run $a $k $m $n
			mkdir -p "./output/affects$a/k$k/m$m/"
			mv "n$n.owl" $_
			mv "n$n.omn" $_
			rm "./output/n$n.owl"
		    fi

		    if [ $a = 0 -a $STA = 1 ]; then
			cp "./output/without/k$k/m$m/n$n.owl" ./output
			lein run -3 $k $m $n
			rm "./output/n$n.owl"
		    fi

		    ## note tasks
		    if [ $REA = 1 ]; then
			echo "$a $k $m $n" >> tasks.txt
		    fi
		done
	    done
	done
    done
fi

## reason ontologies
if [ $REA = 1 ]; then
    echo "Reasoning started"

    ## randomise the tasks
    shuf tasks.txt -o tasks.txt

    ## read in data
    readarray LINES < tasks.txt

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

    ## clean up
    rm tasks.txt

    echo "Reasoning complete"
else
    echo "Reasoning skipped"
fi

## diff results
if [ $DIF = 1 ]; then
    ## clean up
    echo -n > ./output/diff.txt
    extra=4

    for k in "${K[@]}"; do
	for m in "${M[@]}"; do
	    for n in "${N[@]}"; do
		for a in 1 2 3; do
		    diff=`diff -U 0 "./output/without/k$k/m$m/n$n.omn" "./output/affects$a/k$k/m$m/n$n.omn" | grep -v ^@ | wc -l`
		    echo "[$a $k $m $n $(($diff - $extra))]" >> ./output/diff.txt
		done
	    done
	done
    done
    echo "Diffing complete"
else
    echo "Diffing skipped"
fi

## graphs
if [ $GRA = 1 ]; then
    echo "Creating graphs"
    lein run -2
    mkdir -p ./output/graphs/
    mv *.png $_
    echo "Graphs complete"
else
    echo "Graphs skipped"
fi
