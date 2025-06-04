package com.example.mathexampro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {
    public static List<Question> getRandomQuestions(){
        List<Question> all = new ArrayList<>();
        all.add(new Question("What is the sum of the infinite series: 1 − ½ + ¼ − ⅛ + ... ?", new String[]{"1", "0", "2", "Does not converge"}, 0));
        all.add(new Question("Evaluate the limit: lim (x → 0) [sin(3x) / x]", new String[]{"0","1","3","Undefined"}, 2));
        all.add(new Question("How many integers between 1 and 1000 are divisible by neither 2 nor 5?", new String[]{"400","500","300","600"},0));
        all.add(new Question("Find the determinant of the matrix: [[2, -1], [4, 3]]", new String[]{"10","-10","11","5"},0));
        all.add(new Question("If P(A) = 0.6, P(B) = 0.5, and P(A ∩ B) = 0.3, what is P(A ∪ B)?",new String[]{"0.8","1.1","0.9","0.7"},0));
        all.add(new Question("What is the smallest positive integer x such that: 3x ≡ 1 (mod 7)",new String[]{"1", "2", "3", "5"}, 3));
        all.add(new Question("Solve log₂(x² − 1) = 3", new String[]{"x=4", "x=±3", "x=3", "x=2"}, 1));

        Collections.shuffle(all);
        return all;
    }


}
