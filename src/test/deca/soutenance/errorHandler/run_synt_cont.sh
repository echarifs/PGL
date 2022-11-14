#!/bin/bash


for file in $(find . -name "*.deca")
do
	echo -e "-------------------------------------------------------------------------------  "
	echo " We are executing file : "$file
	echo -e "------------------------------------------------------------------------------- "
	echo -e "\n"
	cat $file
	echo -e "\n"
	read -n 1 -s -r -p " Press any key to continue"

	echo -e "\n"
	echo -e "------------------------------------------------------------------------------- "
	echo -e "\n"

	echo -e "decac "$file" \n"
	decac $file >> out
	echo -e " "
	rm out
	echo -e "\n"
	read -n 1 -s -r -p " Press any key to continue"
	clear
done

read -n 1 -s -r -p " Press any key to exit"
clear
