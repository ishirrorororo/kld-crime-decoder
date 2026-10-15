package kldcrimedecoder.data;

/**
 * ALL game text lives here: dialogue, hints, and evidence.
 * Students can edit the story without touching any form.
 * Each dialogue line is {speaker, text}.
 */
public class Story {

    // ============ MENU ============
    public static final String GAME_TITLE = "KLDCRIMe: Amie";
    public static final String SUBTITLE   = "Someone is missing. Don't trust the first answer.";

    public static final String HOW_TO_PLAY =
          "You are Arnie. Alli is missing.\n\n"
        + "- Solve each puzzle to move to the next level.\n"
        + "- Wrong answers and hints cost you time.\n"
        + "- Beat the bell before the timer hits 00:00.\n"
        + "- Read the clues carefully. Some of them are about you.";

    // ============ LEVEL TITLES (index = level number) ============
    public static final String[] LEVEL_TITLES = {
        "Prologue: Monday Morning",   // 0
        "Alli's Phone",               // 1
        "Library Computers",          // 2
        "Shelf 005",                  // 3
        "The Journal",                // 4
        "Student Council Room",       // 5
        "The Project Room",           // 6
        "Library, Late Night",        // 7
        "Security Office",            // 8
        "Old Storage Room",           // 9
        "The Mirror Room"             // 10
    };

    // ============ LEVEL 0: PROLOGUE (draft, refined in Phase 5) ============
    public static final String[][] PROLOGUE = {
        {"Mom", "Arnie, breakfast! Don't be late again. Alli's mom already texted me twice."},
        {"Arnie", "Second place again last week. Fine. I'm fine."},
        {"Prof. Null", "Attendance. Alli? ... Alli?"},
        {"Arnie", "She's never absent. Something is wrong."},
        {"Arnie", "Wait. Why is Alli's phone in my bag?"},
        {"Arnie", "I don't remember getting here."}   // foreshadowing gap
    };
    // =====================================================================
    // JOEL: LEVELS 3 TO 5
    // =====================================================================

    /** Joins {speaker, text} lines into one block of text for a JTextArea. */
    public static String format(String[][] lines) {
        StringBuilder sb = new StringBuilder();
        for (String[] line : lines) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            if (!line[0].isEmpty()) {
                sb.append(line[0]).append(": ");
            }
            sb.append(line[1]);
        }
        return sb.toString();
    }

    // ---- shared by the choice moments (Levels 4, 8, 10) ----
    public static final int CHOICE_READ_SECONDS = 30;
    public static final int DENIAL_READ = -10;   // reading carefully lowers denial
    public static final int DENIAL_SKIP = 15;    // skipping raises denial

    // ---- inventory items ----
    public static final String ITEM_JOURNAL  = "Journal";
    public static final String ITEM_BOOKMARK = "Bookmark (A.V.)";

    // ---------- LEVEL 3: Shelf 005 ----------
        public static final String[][] L3_SHELF_INTRO = {
        {"Arnie", "Shelf 005. Hundreds of books, and the label on top is smudged. Maybe I can still read it."}
    };
    public static final String L3_WRONG_SHELF = "Wrong aisle. Amie's code pointed to shelf 005.";
    public static final String L3_PAPER_PROMPT = "The label is smudged. \nUnscramble the letters:";
    public static final String L3_SOLVED = "Unscrambled. Now find the matching book on the shelf.";
    public static final String L3_BOOK_TOO_EARLY =
        "Which book? There are hundreds. The smudged label on top must say something.";
    public static final String[] L3_BOOKS = {
        "Basic Algebra, Vol. 2",
        "Philippine History: A Short Survey",
        "The Double: A Novel",
        "Lab Safety Handbook",
        "Study Skills for Honors Students"
    };
    public static final String L3_WRONG = "That is not a word yet. Try rearranging the letters again.";
    public static final String L3_WRONG_BOOK = "Nothing but pages. This is not the book the note meant.";
    public static final String L3_JOURNAL_FOUND = "A journal is sandwiched between the pages.";
    public static final String[][] L3_FOUND = {
        {"Arnie", "A journal, pressed flat between two chapters. Someone hid it here on purpose."},
        {"Arnie", "The handwriting on the cover looks familiar. Where have I seen it?"},
        {"Arnie", "There is a bookmark with the initials \"A.V.\" scribbled on it. Lots of people have those initials. Right."},
        {"Arnie", "I don't remember ever opening this shelf."}
    };

    // ---------- LEVEL 4: The Journal ----------
    public static final String[][] L4_INTRO = {
        {"", "The journal opens on a page of names. Six of them. Every name begins with \"Am\". Three are scratched out in pen."}
    };
    public static final String L4_PROMPT =
        "Drag each name into CLEARED or SUSPECT. Read the line under each name.";
    public static final String L4_CHOICE_TITLE = "The journal";
    public static final String L4_CHOICE_TEXT =
        "The pages are packed with tiny handwriting.\n\n"
      + "Read them carefully? It costs 30 seconds.\n"
      + "Or skip the pages and go straight to the names?";
    public static final String L4_CHOICE_READ = "Read carefully (-30 sec)";
    public static final String L4_CHOICE_SKIP = "Skip the pages";
    public static final String[][] L4_READ = {
        {"Arnie", "The loops on the g's. Every i dotted with a tiny circle. That is how I write."},
        {"Arnie", "Everyone says their handwriting is one of a kind. Mine must be common."}
    };
    public static final String[][] L4_SKIP = {
        {"Arnie", "No time. Names first. The handwriting can wait."}
    };
    public static final String L4_WRONG_DROP = "Look at the line under the name again.";
    public static final String L4_DONE = "All six names sorted: three cleared, three suspects.";
    public static final String HINT_L4 =
        "Scratched-out names were crossed off for a reason. The others still have a reason to be angry at Alli.";

    /** Each row: {name, who they are, "cleared" or "suspect", why}. */
    public static final String[][] JOURNAL_NAMES = {
        {"Amiel Castro",    "Transferee who sat beside Alli",                  "cleared", "Transferred out last month."},
        {"Amy Dizon",       "Exchange student, former groupmate",              "cleared", "On a school trip abroad."},
        {"Ami Tan",         "Alli's cousin",                                   "cleared", "Goes to another school."},
        {"Amy Mercado",     "Ranked #3, lost the scholarship slot to Alli",    "suspect", "Nothing clears her yet."},
        {"Amariah Navarro", "Kicked out of Alli's group project",              "suspect", "Nothing clears her yet."},
        {"Aimee Ferrer",    "Library regular who copied Alli's notes",         "suspect", "Nothing clears her yet."}
    };

    // ---------- LEVEL 5: Student Council Room ----------
    // L5_QUESTIONS and RIDDLES (Phase 3) and L5_REVEALS must all have the same length.
    public static final String[][] L5_INTRO = {
        {"", "The student council room. Amy Mercado is stacking chairs. She finished third in the ranking, and Alli took the scholarship slot."},
        {"Amy", "Oh. It's you, Arnie. Ask your questions. But I only answer in riddles. Council habit."}
    };
    public static final String[] L5_QUESTIONS = {
        "Where were you after class yesterday?",
        "Can anyone prove that?",
        "Who was the last person with Alli?"
    };
    public static final String[] L5_REVEALS = {
        "Amy: \"Here. Signing the log, all afternoon.\"\n"
      + "The council sign-in log has Amy's name from 3:40 to 5:15, stamped by the clock.",
        "Amy: \"Ask the two officers who kept tripping over me.\"\n"
      + "Both officers remember her loudly rearranging chairs all evening.",
        "Amy: \"I never saw Alli leave. You did. You're the last person she was seen with.\"\n"
      + "Arnie: \"That can't be right. I don't remember that.\""
    };
    public static final String L5_CORRECT = "Correct. Amy relaxes a little.";
    public static final String L5_WRONG = "Amy shakes her head. Try again.";
    public static final String L5_DONE =
        "Amy Mercado is cleared. But her last sentence is still stuck in your head.";
    // TODO Phase 5: LEVEL1 (Amie's texts, Caesar cipher), LEVEL2 (logs), LEVEL3 (journal)
    // TODO Phase 6: LEVEL4 names, LEVEL5 riddles, LEVEL6 posts, LEVEL7 Morse
    // TODO Phase 7: LEVEL8 timeline, LEVEL9 storage room, LEVEL10 mirror, endings

    // ============ HINTS (one per puzzle, filled in Phase 3) ============
    // TODO: HINT_CAESAR, HINT_BINARY, HINT_SCRAMBLE, HINT_RIDDLE, HINT_MORSE, HINT_LOCK
        // ============ PUZZLE DATA ============
    // The numbers connect across levels:
    //   shift 14 = the day of the science fair (Level 1)
    //   shelf 005 = the month the fair was held (Level 2)
    //   lock 1405 = day + month, a date only Arnie remembers (Level 9)
    public static final String CAESAR_PLAIN  = "meet me here";
    public static final int    CAESAR_SHIFT  = 14;
    public static final int    BINARY_VALUE  = 5;
    public static final int    BINARY_BITS   = 8;
    public static final String SCRAMBLE_WORD = "DOUBLE";
    public static final String MORSE_WORD    = "NOTES";
    public static final String LOCK_CODE     = "1405";

    // ============ RIDDLES (Level 5) ============
    // Each row: {question, hint, accepted answers separated by |}
    public static final String[][] RIDDLES = {
        {"I am written when you arrive and again when you leave, but I never go anywhere. What am I?",
         "Amy signs it every time she enters the room.",
         "log|sign in log|sign in sheet|logbook|log book|sign in"},
        {"The more of me you take, the more you leave behind. What am I?",
         "Think about walking down a hallway.",
         "footsteps|footprints|steps"},
        {"I follow you everywhere, but I vanish in the dark. What am I?",
         "You carry it with you, but you never look at it.",
         "shadow|my shadow"}
    };

    // ============ HINTS ============
    public static final String HINT_CAESAR =
        "Every letter was pushed forward by the same number. Try a date from Arnie's life: the science fair was on the 14th.";
    public static final String HINT_BINARY =
        "Read the digits from the right: 1, 2, 4, 8... Add up the place values that have a 1.";
    public static final String HINT_SCRAMBLE =
        "The book is about someone with a second self. Think of a word that means a twin.";
    public static final String HINT_MORSE =
        "A dot is short and a dash is long. The first letter is N: dash, dot.";
    public static final String HINT_LOCK =
        "Two clues you already solved were numbers: the shift and the shelf. Day first, then month.";
    // ============ ENDINGS ============
    public static final String GOOD_LAST_LINE =
        "Some things can't be fixed in a day. But she stayed to listen.";

    public static final String BAD_LAST_LINE = "You lost to yourself.";
}