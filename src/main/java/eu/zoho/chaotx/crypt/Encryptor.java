package eu.zoho.chaotx.crypt;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * The Encryptor class provides methods for en- and
 * decrypting messages aswell as generating random <i>tokens</i>
 * dependent on a predefined key.
 * 
 * @author HDainester
 */
public class Encryptor {
    public static final int RANDOM_SEED_LENGTH = 128;

    private int[] alphabet;
    private String seed;

    /**
     * Constructs an Encryptor with the passed String
     * as seed.
     * 
     * @param seed string used as seed
     */
    public Encryptor(String seed) {
        this.seed = seed;
    }

    /**
     * Constructs an Encryptor with a random key whose
     * length is equal to {@link #RANDOM_SEED_LENGTH}.
     */
    public Encryptor() {
        this(new Random().ints(RANDOM_SEED_LENGTH, 1, Integer.MAX_VALUE)
            .mapToObj(i -> new String(String.valueOf((char)i)))
            .reduce("", (a, b) -> a+b));
    }

    /**
     * Sets seed used for generation of random tokens.
     * 
     * @param seed string used as seed
     */
    public void setSeed(String seed) {
        this.seed = seed;
    }

    /**
     * Returns seed used for generation of random tokens.
     * 
     * @return seed
     */
    public String getSeed() {
        return seed;
    }

    /**
     * Sets alphabet used for generation of random tokens.
     * 
     * @param alphabet array holding character set
     */
    public void setAlphabet(int[] alphabet) {
        this.alphabet = alphabet;
    }

    /**
     * Sets alphabet according to passed AlphabetType.
     * 
     * @param type alphabet type
     * @see #setAlphabet(int[])
     */
    public void setAlphabet(AlphabetType type) {
        int i, j = 0;
        int[] alphabet = null;

        switch(type) {
            case ALPHA_NUMERIC:
                alphabet = new int[2*26+10];
                for(i = 48; i < 58; ++i, ++j) alphabet[j] = i;
                for(i = 65; i < 91; ++i, ++j) alphabet[j] = i;
                for(i = 97; i < 123; ++i, ++j) alphabet[j] = i;
                break;
            case ASCII:
                alphabet = new int[128-33];
                for(i = 33; i < 128; ++i) alphabet[i-1] = i;
                break;
            case LATIN_1:
                alphabet = new int[256-33];
                for(i = 33; i < 256; ++i) alphabet[i-1] = i;
                break;
            default:
                break;
        }

        setAlphabet(alphabet);
    }

    /**
     * Returns currently set alphabet. If no alphabet has been set
     * <i>null</i> will be returned indicating that <strong>all</strong>
     * possible characters are valid.
     * 
     * @return alphabet
     */
    public int[] getAlphabet() {
        return alphabet;
    }

    /**
     * <p>Creates a random token dependent on {@code key}
     * and the seed ({@link #getSeed()}) of given {@code length}.</p>
     * 
     * <p>The currently set alphabet will be used to determine the
     * range of valid chars. If the alphabet has not been set or
     * is equal to null all char-codes are valid.</p>
     * 
     * @param key for generator
     * @param length of the token
     * @return random token
     */
    public String createToken(String key, int length) {
        int klen = key.length(), slen = seed.length();
        Random rng = new Random(klen);

        for(int i = 0; i < klen; ++i)
            rng.setSeed(rng.nextLong() + key.charAt(i) + seed.charAt(i%slen));

        int a_min, a_max;
        if(alphabet == null) {
            a_min = 1;
            a_max = Integer.MAX_VALUE;
        } else {
            a_min = alphabet[0];
            a_max = alphabet[alphabet.length-1];
        }

        return rng.ints(a_min, a_max+1)
            .filter(i -> alphabet != null ? IntStream.of(alphabet).anyMatch(v -> v == i) : true)
            .limit(length).mapToObj(i -> new String(String.valueOf((char)i)))
            .reduce("", (a, b) -> a+b);
    }

    /**
     * Creates a random token dependent on the seed
     * ({@link #getSeed()}) of given length.
     * 
     * @param length of the token
     * @return random token
     */
    public String createToken(int length) {
        return createToken(seed, length);
    }

    /**
     * <p>En-/Decrypts given message {@code m} using the
     * key {@code k} and the conversion function {@code f}.</p>
     * 
     * <p>If the length of the key is smaller than the length
     * of the message the seed of the calling Encryptor-Object
     * will be appended to the key.</p>
     * 
     * @param m message
     * @param k key
     * @param f conversion function
     * @return en-/decrypted message
     * @throws IllegalArgumentException if length of message {@code m}
     *  	is smaller than the length of key {@code k}
     * 
     * @see EncryptFunction
     */
    public String execute(String m, String k, EncryptFunction f) {
        if(m.length() < k.length())
            throw new IllegalArgumentException("message may not be smaller than the key");

        StringBuilder c = new StringBuilder();
        while(k.length() < m.length()) k += seed;

        for(int i = 0; i < m.length(); ++i)
            c.append((char)f.compute(m.charAt(i), k.charAt(i)));

        return c.toString();
    }
}