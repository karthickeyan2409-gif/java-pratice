class Solution {
    public String mergeAlternately(String word1, String word2) {
        StringBuilder result = new StringBuilder();
        int i = 0, j = 0;
        int n1 = word1.length();
        int n2 = word2.length();
        
        while (i < n1 && j < n2) {
            result.append(word1.charAt(i));
            result.append(word2.charAt(j));
            i++;
            j++;
        }
        
        // Append whatever's left over from the longer string
        if (i < n1) {
            result.append(word1.substring(i));
        }
        if (j < n2) {
            result.append(word2.substring(j));
        }
        
        return result.toString();
    }
}
