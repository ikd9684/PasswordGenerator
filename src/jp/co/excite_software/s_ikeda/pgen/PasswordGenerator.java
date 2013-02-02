package jp.co.excite_software.s_ikeda.pgen;

import java.util.Random;

import jp.co.excite_software.s_ikeda.pgen.CharType.PassPhraseValidator;

public class PasswordGenerator {

    /**  */
    public static final CharType NUMBER =
            new CharType("0123456789".toCharArray());
    /**  */
    public static final CharType ALPHABET_L =
            new CharType("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray());
    /**  */
    public static final CharType ALPHABET_S =
            new CharType("abcdefghijklmnopqrstuvwxyz".toCharArray());
    /**  */
    public static final CharType SYMBOL =
            new CharType("!\"#$%&'()*+,-./:;<=>?@[¥]^_`{|}~".toCharArray());

    /**  */
    private static final Random RND = new Random(System.currentTimeMillis());
    /**  */
    private int totalLen;
    /**  */
    private char[] chars;
    /**  */
    private PassPhraseValidator validator;

    /**
     * 
     */
    public PasswordGenerator() {
        this(new CharType[] { NUMBER });
    }
    /**
     * 
     */
    public PasswordGenerator(CharType[] charTypes) {
        this(charTypes, null);
    }
    /**
     * 
     */
    public PasswordGenerator(CharType[] charTypes, PassPhraseValidator validator) {

        for (int i = 0; i < charTypes.length; i++) {
            totalLen += charTypes[i].getChars().length;
        }
        this.chars = new char[this.totalLen];

        int idx = 0;
        for (int i = 0; i < charTypes.length; i++) {
            System.arraycopy(charTypes[i].getChars(), 0, chars, idx, charTypes[i].getChars().length);
            idx += charTypes[i].getChars().length;
        }
    }

    public String newPasswd(int length) {

        String phrase;
        do {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int r = RND.nextInt(totalLen);
                sb.append(chars[r]);
            }
            phrase = sb.toString();
        }
        while (validator != null && !validator.isValid(phrase));

        return phrase;
    }

    public static void main(String[] args) {

        PassPhraseValidator validator =
            new PassPhraseValidator() {
                @Override
                public boolean isValid(String phrase) {

                    // 同じ文字が３回以上続かないこと
                    char p = phrase.charAt(0);
                    int count = 1;
                    for (int i = 0; i < phrase.length(); i++) {
                        if (p == phrase.charAt(i)) {
                            ++count;

                            if (3 <= count) {
                                return false;
                            }
                        }
                        else {
                            count = 1;
                            p = phrase.charAt(i);
                        }
                    }
                    return false;
                }
        };

        PasswordGenerator generator =
            new PasswordGenerator(
                new CharType[] {
                        NUMBER,
                        ALPHABET_L,
                        ALPHABET_S,
                        new CharType("!#$%&@".toCharArray()),
                },
                validator);

        for (int i = 0; i < 1000; i++) {
            String passwd = generator.newPasswd(16);
            System.out.println(passwd);
        }
    }

}
