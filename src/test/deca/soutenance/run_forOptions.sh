#!/bin/bash

FILE=$(basename $1 ".deca")


echo -e "  -------------------------------------------------------------------------------   "
echo " Decac options"
echo " We are taking : "$FILE".deca as an exemple"
echo -e "  -------------------------------------------------------------------------------   "
echo -e "\n"
cat $FILE".deca"
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear

echo -e "\n   decac -----------------------------------------------------------------------   \n"
echo -e "   decac   \n"
echo -e "Affiche les options de compilateur"
echo -e "\n"
decac
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear

echo -e "\n   decac -b -----------------------------------------------------------------------   \n"
echo -e "   decac -b   \n"
echo -e "affiche une bannière indiquant le nom de l’équipe"
echo -e "\n"
decac -b
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear


echo -e "\n   decac -p -----------------------------------------------------------------------  \n"
echo -e "   decac -p "$FILE".deca  \n"
echo -e "arrête decac après l’étape de construction de l’arbre et affiche la décompilation de ce dernier
(c.-à-d. s’il n’y a qu’un fichier source à compiler, la sortie doit être un programme Deca syntaxiquement correct)"
echo -e "\n"
decac -p $FILE".deca"
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear

echo -e "\n   decac -v ----------------------------------------------------------------------- \n"
echo -e "   decac -v "$FILE".deca  \n"
echo -e "arrête decac après l’étape de vérifications (ne produit aucune sortie en l’absence d’erreur"
echo -e "\n"
decac -v $FILE".deca"
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear


echo -e "\n   decac -n ----------------------------------------------------------------------- \n"
echo -e "   decac -n "$FILE".deca  \n"
echo -e "supprime les tests à l’exécution telles que division par 0, debordment memoire.."
echo -e "\n"
decac -n $FILE".deca"
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear


echo -e "\n   decac -r X -----------------------------------------------------------------------  \n"
echo -e "   decac -r 10 "$FILE".deca  \n"
echo -e " limite les registres banalisés disponibles."
echo -e "\n"
decac -r 10 $FILE".deca"
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear


echo -e "\n   decac -d -----------------------------------------------------------------------  \n"
echo -e "   decac -d "$FILE".deca  \n"
echo -e "Active les traces de debug. Répéter l’option plusieurs fois pour avoir plus de traces."
echo -e "\n"
decac -d $FILE".deca"
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear


echo -e "\n   decac -P -----------------------------------------------------------------------  \n"
echo -e "   decac -P "$FILE".deca Prog1.deca  \n"
echo -e "S’il y a plusieurs fichiers sources, lance la compilation des fichiers en parallèle (pour accélérer la compilation"
echo -e "\n"
rm *.ass
ls *.deca
echo -e "\n"
read -n 1 -s -r -p " Press any key to start the parallel compiling"
decac -P $FILE".deca" Prog1.deca
echo -e "\n"
ls *.deca *.ass
echo -e "\n"
read -n 1 -s -r -p " Press any key to continue"
clear



echo -e "\n  [decac] -----------------------------------------------------------------------  \n"
echo -e "   decac "$FILE".deca  "
echo -e "\n"
decac $FILE".deca"
echo -e " OK "$FILE".ass genere!   \n"
read -n 1 -s -r -p " Press any key to continue"
clear

echo -e "\n  [ima] -----------------------------------------------------------------------  \n"
echo -e "   ima "$FILE".ass  "
echo -e "\n"
ima $FILE.ass

echo -e "\n  "
read -n 1 -s -r -p " Press any key to exit"
clear
