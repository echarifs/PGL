#!/bin/bash

	echo "-------------------------------------------------------------------------------  "
	echo " We are executing file : divParZero.deca"
	echo "------------------------------------------------------------------------------- "
	echo -e "\n"
	cat ./divParZero.deca 
	echo -e "\n"
	read -n 1 -s -r -p " Press any key to continue"

	echo -e "\n"
	echo -e "-------------------------------------------------------------------------------"
	echo -e "\n"


        decac ./divParZero.deca
        if [ ! -f ./divParZero.ass ]; then
        echo -e "Fichier ./divParZero.ass non généré."
        fi

resultat=$(ima ./divParZero.ass)
rm -f ./divParZero.ass

# On code en dur la valeur attendue.
attendu=ok

if [ "$resultat" = "$attendu" ]; then
    echo -e "Tout va bien"
else
    echo "Résultat inattendu de ima:"
    echo "$resultat"
fi
    echo -e " "
    echo -e "\n"
    read -n 1 -s -r -p " Press any key to continue"
    clear


	echo "-------------------------------------------------------------------------------  "
	echo " We are executing file : sans_init.deca "
	echo "------------------------------------------------------------------------------- "
	echo -e "\n"
	cat ./sans_init.deca 
	echo -e "\n"
	read -n 1 -s -r -p " Press any key to continue"

	echo -e "\n"
	echo -e "-------------------------------------------------------------------------------"
	echo -e "\n"


        decac ./sans_init.deca
        if [ ! -f ./sans_init.ass ]; then
        echo -e "Fichier ./sans_init.ass non généré."
        fi

resultat=$(ima ./sans_init.ass)
rm -f ./sans_init.ass

# On code en dur la valeur attendue.
attendu=ok

if [ "$resultat" = "$attendu" ]; then
    echo -e "Tout va bien"
else
    echo "Résultat inattendu de ima:"
    echo "$resultat"
fi
    echo -e " "
    echo -e "\n"
    read -n 1 -s -r -p " Press any key to continue"
    clear

	echo "-------------------------------------------------------------------------------  "
	echo " We are executing file : debordement.ass"
	echo "------------------------------------------------------------------------------- "
	echo -e "\n"
	echo "Si on déclare 100000 variables. "
	echo -e "\n"
	read -n 1 -s -r -p " Press any key to continue"

	echo -e "\n"
	echo -e "-------------------------------------------------------------------------------"
	echo -e "\n"


resultat=$(ima ./debordement.ass)

# On code en dur la valeur attendue.
attendu=ok

if [ "$resultat" = "$attendu" ]; then
    echo -e "Tout va bien"
else
    echo "Résultat inattendu de ima:"
    echo "$resultat"
fi
    echo -e " "
    echo -e "\n"
    read -n 1 -s -r -p " Press any key to continue"
    clear
