/* C++ code produced by gperf version 3.0.1 */
/* Command-line: gperf -d -7 -l -D -L C++ token.gperf  */
/* Computed positions: -k'1,7,12,$' */

#if !((' ' == 32) && ('!' == 33) && ('"' == 34) && ('#' == 35) \
      && ('%' == 37) && ('&' == 38) && ('\'' == 39) && ('(' == 40) \
      && (')' == 41) && ('*' == 42) && ('+' == 43) && (',' == 44) \
      && ('-' == 45) && ('.' == 46) && ('/' == 47) && ('0' == 48) \
      && ('1' == 49) && ('2' == 50) && ('3' == 51) && ('4' == 52) \
      && ('5' == 53) && ('6' == 54) && ('7' == 55) && ('8' == 56) \
      && ('9' == 57) && (':' == 58) && (';' == 59) && ('<' == 60) \
      && ('=' == 61) && ('>' == 62) && ('?' == 63) && ('A' == 65) \
      && ('B' == 66) && ('C' == 67) && ('D' == 68) && ('E' == 69) \
      && ('F' == 70) && ('G' == 71) && ('H' == 72) && ('I' == 73) \
      && ('J' == 74) && ('K' == 75) && ('L' == 76) && ('M' == 77) \
      && ('N' == 78) && ('O' == 79) && ('P' == 80) && ('Q' == 81) \
      && ('R' == 82) && ('S' == 83) && ('T' == 84) && ('U' == 85) \
      && ('V' == 86) && ('W' == 87) && ('X' == 88) && ('Y' == 89) \
      && ('Z' == 90) && ('[' == 91) && ('\\' == 92) && (']' == 93) \
      && ('^' == 94) && ('_' == 95) && ('a' == 97) && ('b' == 98) \
      && ('c' == 99) && ('d' == 100) && ('e' == 101) && ('f' == 102) \
      && ('g' == 103) && ('h' == 104) && ('i' == 105) && ('j' == 106) \
      && ('k' == 107) && ('l' == 108) && ('m' == 109) && ('n' == 110) \
      && ('o' == 111) && ('p' == 112) && ('q' == 113) && ('r' == 114) \
      && ('s' == 115) && ('t' == 116) && ('u' == 117) && ('v' == 118) \
      && ('w' == 119) && ('x' == 120) && ('y' == 121) && ('z' == 122) \
      && ('{' == 123) && ('|' == 124) && ('}' == 125) && ('~' == 126))
/* The character set is not based on ISO-646.  */
#error "gperf generated tables don't work with this execution character set. Please report a bug to <bug-gnu-gperf@gnu.org>."
#endif


#define TOTAL_KEYWORDS 59
#define MIN_WORD_LENGTH 5
#define MAX_WORD_LENGTH 21
#define MIN_HASH_VALUE 12
#define MAX_HASH_VALUE 141
/* maximum key range = 130, duplicates = 0 */

class Perfect_Hash
{
private:
  static inline unsigned int hash (const char *str, unsigned int len);
public:
  static const char *in_word_set (const char *str, unsigned int len);
};

inline unsigned int
Perfect_Hash::hash (register const char *str, register unsigned int len)
{
  static unsigned char asso_values[] =
    {
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142,   0, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142, 142, 142, 142,
      142, 142, 142, 142, 142, 142, 142,  35,   5,  55,
        5,  10,  50,  25,  15,  15, 142,   0,  20,   5,
       25,   5,  10, 142,   0,   0,   0, 142,   5,  56,
       50,  45, 142, 142, 142, 142, 142, 142
    };
  register int hval = len;

  switch (hval)
    {
      default:
        hval += asso_values[(unsigned char)str[11]];
      /*FALLTHROUGH*/
      case 11:
      case 10:
      case 9:
      case 8:
      case 7:
        hval += asso_values[(unsigned char)str[6]];
      /*FALLTHROUGH*/
      case 6:
      case 5:
      case 4:
      case 3:
      case 2:
      case 1:
        hval += asso_values[(unsigned char)str[0]];
        break;
    }
  return hval + asso_values[(unsigned char)str[len - 1]];
}

const char *
Perfect_Hash::in_word_set (register const char *str, register unsigned int len)
{
  static unsigned char lengthtable[] =
    {
       7, 15, 11, 12, 15, 11, 17, 18, 14, 10, 11, 17, 13, 19,
      16, 17, 18, 14, 11, 17, 18, 19, 11, 17, 19, 16, 12, 13,
      16, 17, 13, 14, 10, 11,  5, 16,  6, 13, 14, 10, 21, 10,
      11, 13, 14, 16, 11, 16,  9, 21, 12, 13, 21, 16, 14, 12,
       7, 12, 11
    };
  static const char * wordlist[] =
    {
      "orphans" /* hash value = 12, index = 0 */,
      "sql-date-format" /* hash value = 15, index = 1 */,
      "margin-left" /* hash value = 16, index = 2 */,
      "margin-right" /* hash value = 17, index = 3 */,
      "sql-time-format" /* hash value = 20, index = 4 */,
      "date-format" /* hash value = 21, index = 5 */,
      "border-left-color" /* hash value = 22, index = 6 */,
      "border-right-color" /* hash value = 23, index = 7 */,
      "text-transform" /* hash value = 24, index = 8 */,
      "margin-top" /* hash value = 25, index = 9 */,
      "master-page" /* hash value = 26, index = 10 */,
      "background-repeat" /* hash value = 27, index = 11 */,
      "margin-bottom" /* hash value = 28, index = 12 */,
      "border-bottom-color" /* hash value = 29, index = 13 */,
      "border-top-style" /* hash value = 31, index = 14 */,
      "border-left-style" /* hash value = 32, index = 15 */,
      "border-right-style" /* hash value = 33, index = 16 */,
      "visible-format" /* hash value = 34, index = 17 */,
      "text-indent" /* hash value = 36, index = 18 */,
      "border-left-width" /* hash value = 37, index = 19 */,
      "border-right-width" /* hash value = 38, index = 20 */,
      "border-bottom-style" /* hash value = 39, index = 21 */,
      "line-height" /* hash value = 41, index = 22 */,
      "page-break-before" /* hash value = 42, index = 23 */,
      "border-bottom-width" /* hash value = 44, index = 24 */,
      "text-linethrough" /* hash value = 46, index = 25 */,
      "padding-left" /* hash value = 47, index = 26 */,
      "string-format" /* hash value = 48, index = 27 */,
      "background-image" /* hash value = 51, index = 28 */,
      "page-break-inside" /* hash value = 52, index = 29 */,
      "text-overline" /* hash value = 53, index = 30 */,
      "padding-bottom" /* hash value = 54, index = 31 */,
      "text-align" /* hash value = 55, index = 32 */,
      "padding-top" /* hash value = 56, index = 33 */,
      "color" /* hash value = 60, index = 34 */,
      "page-break-after" /* hash value = 61, index = 35 */,
      "widows" /* hash value = 62, index = 36 */,
      "padding-right" /* hash value = 63, index = 37 */,
      "text-underline" /* hash value = 64, index = 38 */,
      "can-shrink" /* hash value = 65, index = 39 */,
      "background-attachment" /* hash value = 66, index = 40 */,
      "font-style" /* hash value = 70, index = 41 */,
      "font-weight" /* hash value = 71, index = 42 */,
      "number-format" /* hash value = 73, index = 43 */,
      "letter-spacing" /* hash value = 74, index = 44 */,
      "border-top-color" /* hash value = 76, index = 45 */,
      "white-space" /* hash value = 77, index = 46 */,
      "background-color" /* hash value = 81, index = 47 */,
      "font-size" /* hash value = 84, index = 48 */,
      "background-position-y" /* hash value = 86, index = 49 */,
      "number-align" /* hash value = 87, index = 50 */,
      "show-if-blank" /* hash value = 88, index = 51 */,
      "background-position-x" /* hash value = 91, index = 52 */,
      "border-top-width" /* hash value = 92, index = 53 */,
      "vertical-align" /* hash value = 94, index = 54 */,
      "font-variant" /* hash value = 97, index = 55 */,
      "display" /* hash value = 102, index = 56 */,
      "word-spacing" /* hash value = 128, index = 57 */,
      "font-family" /* hash value = 141, index = 58 */
    };

  static signed char lookup[] =
    {
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  0, -1,
      -1,  1,  2,  3, -1, -1,  4,  5,  6,  7,  8,  9, 10, 11,
      12, 13, -1, 14, 15, 16, 17, -1, 18, 19, 20, 21, -1, 22,
      23, -1, 24, -1, 25, 26, 27, -1, -1, 28, 29, 30, 31, 32,
      33, -1, -1, -1, 34, 35, 36, 37, 38, 39, 40, -1, -1, -1,
      41, 42, -1, 43, 44, -1, 45, 46, -1, -1, -1, 47, -1, -1,
      48, -1, 49, 50, 51, -1, -1, 52, 53, -1, 54, -1, -1, 55,
      -1, -1, -1, -1, 56, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, 57, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, 58
    };

  if (len <= MAX_WORD_LENGTH && len >= MIN_WORD_LENGTH)
    {
      register int key = hash (str, len);

      if (key <= MAX_HASH_VALUE && key >= 0)
        {
          register int index = lookup[key];

          if (index >= 0)
            {
              if (len == lengthtable[index])
                {
                  register const char *s = wordlist[index];

                  if (*str == *s && !memcmp (str + 1, s + 1, len - 1))
                    return s;
                }
            }
        }
    }
  return 0;
}
