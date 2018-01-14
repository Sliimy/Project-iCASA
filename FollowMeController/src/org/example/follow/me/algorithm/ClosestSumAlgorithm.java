package org.example.follow.me.algorithm;
 
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
 
/**
 * This class implements an algorithm that use a very naive approach of solving
 * the closest sum subset problem on an array of doubles.
 */
public final class ClosestSumAlgorithm {
 
    /**
     * Find the subset of the items whose sum is closest to, without exceeding
     * maximalSum.
     * 
     * Performance are low with doubles. Could largely be improved by using
     * integers instead. The algorithm is sufficiently effective for 15 lights
     * or less.
     * 
     * @param maximalSum
     *            the maximal sum of the subset;
     * @param items
     *            an array containing the weight of each items. The order of
     *            element will be preserved to produce the best
     *            combination.
     * @return the subset of items whose sum is closest to maximalSum
     *         without exceeding it. The combination is given in the same order
     *         as the input array. array[i] contains the value of item[i] if it
     *         is involved in the computing of the closest-sum, or 0 if not.
     */
    public static ArrayList<String> greadySubSetClosestSum(final double maximalSum, final HashMap<String, Double>items) {
 
        // the current best results :
        double bestSum = 0.0d;
        double[] bestCombination = new double[0];
 
        /*
         * Generate all the possible combinations. There are 2^N possibilities
         * that can therefore be represented by a bitset.
         * The use of bitset is done to reduce the number of line of codes.
         * The solution is thus far from being optimized.
         */
        ArrayList<String> serialNumberBinaryLights=new ArrayList<String>();
        double[] energies = new double[items.size()];
        int j=0;
        for (Map.Entry<String,Double> e : items.entrySet()) {
        	energies[j]=e.getValue();
        	j++;
        }
        for (int i = 0; i < Math.pow(2, energies.length); i++) {
            // Get the current combination 
            double[] currentCombination = multiplyByBitset(convertToBitSet(i), energies);
            double currentSum = sum(currentCombination);
 
            // if we have the best result possible
            if (currentSum == maximalSum) {
                // return it
            	for (int k=0; k<currentCombination.length;k++) {
            		String serialNumberLB = getKeyByValue(items, currentCombination[k], serialNumberBinaryLights);
            		serialNumberBinaryLights.add(serialNumberLB);
            	}
                return serialNumberBinaryLights;
            }
 
            // if the current result is better than the previous best result
            if ((currentSum <= maximalSum) && (currentSum > bestSum)) {
                // store it
                bestSum = currentSum;
                bestCombination = currentCombination;
            }
        }
        for (int k=0; k<bestCombination.length;k++) {
    		String serialNumberLB = getKeyByValue(items, bestCombination[k], serialNumberBinaryLights);
    		serialNumberBinaryLights.add(serialNumberLB);
    	}
        return serialNumberBinaryLights;
    }
 
    /**
     * Sum of the given variables or array.
     * 
     * @param variables
     *            the variables to be summed.
     * @return the sum of the variables.
     */
    private static double sum(double... variables) {
        double sum = 0;
        for (double var : variables) {
            sum += var;
        }
        return sum;
    }
 
    /**
     * Convert a number into BitSet.
     * This could be obtained directly in JAVA7 (iCASA is not compatible)
     * 
     * @param number
     *            the number to convert
     * @return the resulting bit set
     */
    private static BitSet convertToBitSet(long number) {
        BitSet bits = new BitSet();
        int index = 0;
        while (number != 0L) {
            if ((number % 2L) != 0) {
                bits.set(index);
            }
            ++index;
            number = number >>> 1;
        }
        return bits;
    }
 
    /**
     * Multiply an array by a bitset
     * 
     * @param bitset
     *            the BitSet
     * @param array
     *            the array
     * @return the resulting array
     */
    private static double[] multiplyByBitset(BitSet bitset, double[] array) {
        assert (bitset.length() == array.length) : "array and bitset must have the same size";
 
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = bitset.get(i) ? array[i] : 0;
        }
        return result;
    }
    
    @SuppressWarnings("hiding")
	public static <String, Double> String getKeyByValue(Map<String, Double> map, Double value,ArrayList<String> valueUsed) {
        for (Entry<String, Double> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())&&!valueUsed.contains(entry.getKey())) {
                return entry.getKey();
            }
        }
        return null;
    }
}