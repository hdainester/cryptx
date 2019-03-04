package eu.zoho.chaotx.crypt;

import java.io.Serializable;

/**
 * Functional Interface to dynamically implement
 * encryption algorithms for sequences of chars
 * (e.g. Strings).
 * 
 * @author HDainester
 */
public interface EncryptFunction extends Serializable {
    /**
     * Takes the given given char-codes {@code m} and
     * {@code k}, who represent chars of a String
     * message and a String key, and computes an
     * output char according to the implemented algorithm.
     * 
     * @param m char from message
     * @param k char from key
     * @return computed char-code
     */
    public int compute(int m, int k);
}