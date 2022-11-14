#!/bin/sh

#Script qui execute test_context sur tous les tests Invalid
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

for i in ./src/test/deca/context/invalid/*.deca
do
    if test_context $i 2>&1 \
        | grep -q -e $i
    then
        echo "OK" $i
    else
        echo "Erreur non detectee par test_context pour " "$i"
        exit 1
    fi
done