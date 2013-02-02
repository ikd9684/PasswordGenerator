package jp.co.excite_software.s_ikeda.pgen;

import java.io.Serializable;

public class CharType implements Serializable {
    private static final long serialVersionUID = 1L;

    /**  */
    private char[] chars;

    /**
     * @param chars
     */
    public CharType(char[] chars) {
        this.chars = chars;
    }

    /**
     * @return
     */
    public char[] getChars() {
        return this.chars;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < chars.length; i++) {
            sb.append(" " + chars[i] + " ");
            if (i < chars.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * @author ikd9684
     *
     */
    public static interface PassPhraseValidator {
        /**
         * @param phrase
         * @return
         */
        public boolean isValid(String phrase) ;
    }

}
