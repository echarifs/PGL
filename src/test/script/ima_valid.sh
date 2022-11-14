#!/bin/sh

#Script qui execute ima sur tous les tests.ass dans valid (ourTests et provided)
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

for i in ./src/test/deca/codegen/valid/ourTests/*.ass
do
    if ima $i 2>&1 \
        | head -n 1 | grep -q $i
    then
        echo "Echec inattendu de ima sur " "$i"
        exit 1
    else
        echo "OK" "$i"
    
fi

for i in ./src/test/deca/codegen/valid/provided/*.ass
do
    if ima $i 2>&1 \
        | head -n 1 | grep -q $i
    then
        echo "Echec inattendu de ima sur " "$i"
        exit 1
    else
        echo "OK" "$i"
    
fi
done