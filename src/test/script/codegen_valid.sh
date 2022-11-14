#!/bin/sh

#Script qui execute decac sur tous les tests dans valid (ourTests et provided)
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

for i in ./src/test/deca/codegen/valid/ourTests/*.deca
do
    if decac $i 2>&1 \
        | head -n 1 | grep -q $i
    then
        echo "Echec inattendu de decac sur " "$i"
        exit 1
    else
        echo "OK compile" "$i"
    
fi
done

for i in ./src/test/deca/codegen/valid/provided/*.deca
do
    if decac $i 2>&1 \
        | head -n 1 | grep -q $i
    then
        echo "Echec inattendu de decac sur " "$i"
        #exit 1
    else
        echo "OK compile" "$i"
    
fi
done

for i in ./src/test/deca/codegen/perf/provided/*.deca
do
    if decac $i 2>&1 \
        | head -n 1 | grep -q $i
    then
        echo "Echec inattendu de decac sur " "$i"
        #exit 1
    else
        echo "OK compile" "$i"
    
fi
done

for i in ./src/test/deca/codegen/perf/*.deca
do
    if decac $i 2>&1 \
        | head -n 1 | grep -q $i
    then
        echo "Echec inattendu de decac sur " "$i"
        #exit 1
    else
        echo "OK compile" "$i"
    
fi
done