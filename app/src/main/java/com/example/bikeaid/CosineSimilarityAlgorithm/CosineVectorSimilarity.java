package com.example.bikeaid.CosineSimilarityAlgorithm;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosineVectorSimilarity {
    public static double consineTextSimilarity(String[] left, String[] right) {
        Map<String, Integer> leftWordCountMap = new HashMap<>();
        Map<String, Integer> rightWordCountMap = new HashMap<>();
        Set<String> uniqueSet = new HashSet<>();// dublicate hudaina
        Integer temp;
        for (String leftWord : left) {
            temp = leftWordCountMap.get(leftWord);
            if (temp == null) {
                leftWordCountMap.put(leftWord, 1);
                uniqueSet.add(leftWord);
            } else {
                leftWordCountMap.put(leftWord, temp + 1);
            }
        }
        for (String rightWord : right) {
            temp = rightWordCountMap.get(rightWord);
            if (temp == null) {
                rightWordCountMap.put(rightWord, 1);
                uniqueSet.add(rightWord);
            } else {
                rightWordCountMap.put(rightWord, temp + 1);
            }
        }
        int[] leftVector = new int[uniqueSet.size()];
        int[] rightVector = new int[uniqueSet.size()];
        int index = 0;
        Integer tempCount;
        for (String uniqueWord : uniqueSet) {
            tempCount = leftWordCountMap.get(uniqueWord);
            leftVector[index] = tempCount == null ? 0 : tempCount;
            tempCount = rightWordCountMap.get(uniqueWord);
            rightVector[index] = tempCount == null ? 0 : tempCount;
            index++;
        }
        return consineVectorSimilarity(leftVector, rightVector);
    }


    private static double consineVectorSimilarity(int[] leftVector, int[] rightVector) {
        if (leftVector.length != rightVector.length)
            return 1;
        double dotProduct = 0;
        double leftNorm = 0;
        double rightNorm = 0;
        for (int i = 0; i < leftVector.length; i++) {
            dotProduct += leftVector[i] * rightVector[i];
            leftNorm += leftVector[i] * leftVector[i];
            rightNorm += rightVector[i] * rightVector[i];
        }

        return dotProduct                / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }

    public static void main(ArrayList<String> test1) {
        ArrayList<String> temp = new ArrayList<>();

        for (int c = 0; c < test1.size(); c++) {
            if (null != test1.get(c) && 0 < test1.get(c).trim().length()) {
                temp.add(test1.get(c));

            }

        }
        String[] left = new String[temp.size()];
        left = temp.toArray(left);
        for (int i = 0; i < left.length; i++) {
            Log.i(left[i], "MESSAGE");
        }
        Log.i(String.valueOf(temp.size()), "tempsize");
        for (int m = 0; m < temp.size(); m++) {
        }
//        String left[]={"abc", "def", "ghi", "jkl", "mno", "pqr","stu", "vwx"};

        String right[] = {"abc", "jkl", "pqr"};
        for (int i = 0; i < left.length; i++) {
            Log.i(right[i], "MESSAGE");
        }

        double a = consineTextSimilarity(left, right);
        Log.i(String.valueOf(a), "similarity");


    }
}