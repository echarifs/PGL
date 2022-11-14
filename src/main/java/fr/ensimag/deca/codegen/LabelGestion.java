/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Label;

/**
 * Manager of labels during generation of code
 * 
 * @author gl01
 */
public class LabelGestion {

    private int indexLabelEndIf;
	private int indexLabelElse;
    private int indexLabelFalse;
    private int indexLabelTrue;
    private int indexLabelEndCMP;
    private int indexLabelWhile;
    private int indexLabelEndWhile;
	private final String labelEndIf = "EndIf";
    private final String labelFalse = "False";
    private final String labelTrue = "True";
    private final String labelendCMP = "EndCMP";
	private final String labelElse = "Else";
    private final String labelWhile = "While";
    private final String labelEndWhile = "EndWhile";
        
        /**
        * Get the label of if generated with index
        *
        * @return label generated
        */
        
        /**
        * Get the label of end_if generated with index
        *
        * @return label generated
        */
        public Label getEndIfLabel() {
            Label label = new Label(labelEndIf + indexLabelEndIf);
            indexLabelEndIf++;
            return label;
        }
        
        /**
        * Get the label of else generated with index
        *
        * @return label generated
        */
        public Label getElseLabel() {
            Label label = new Label(labelElse + indexLabelElse);
            indexLabelElse++;
            return label;
        }
        
        /**
        * Get the label of end_if generated with index
        *
        * @return label generated
        */
        

        public Label getFalseLabel()
        {
            Label label = new Label(labelFalse + indexLabelFalse);
            indexLabelFalse++;
            return label;
        }

        public Label getTrueLabel()
        {
            Label label = new Label(labelTrue + indexLabelTrue);
            indexLabelTrue++;
            return label;
        }


        public Label getEndCMPLabel()
        {
            Label label = new Label(labelendCMP + indexLabelEndCMP);
            indexLabelEndCMP++;
            return label;
        }


        public Label getWhileLabel() {
            Label label = new Label(labelWhile + indexLabelWhile);
            indexLabelWhile++;
            return label;
        }


        public Label getEndWhileLabel() {
            Label label = new Label(labelEndWhile + indexLabelEndWhile);
            indexLabelEndWhile++;
            return label;
        }
 
        /**
        * Constructor of label manager, initialize all the index of labels.
        *
        */
        public LabelGestion() {
            indexLabelEndIf = 0;
            indexLabelElse = 0;
            indexLabelFalse = 0;
            indexLabelEndCMP = 0;
            indexLabelWhile = 0;
            indexLabelEndWhile = 0;
        }

}
