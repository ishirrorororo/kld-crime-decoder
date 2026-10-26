package kldcrimedecoder.data;

/**
 * ALL game text lives here: dialogue, hints, and evidence. Students can edit
 * the story without touching any form. Each dialogue line is {speaker, text}.
 */
public class Story {

    // ============ MENU ============
    public static final String GAME_TITLE = "KLDCRIMe: Amie";
    public static final String SUBTITLE = "Someone is missing. Don't trust the first answer.";

    public static final String HOW_TO_PLAY
            = "You are Arnie. Alli is missing.\n\n"
            + "- Solve each puzzle to move to the next level.\n"
            + "- Wrong answers and hints cost you time.\n"
            + "- Beat the bell before the timer hits 00:00.\n"
            + "- Read the clues carefully. Some of them are about you.";

    // ============ LEVEL TITLES (index = level number) ============
    public static final String[] LEVEL_TITLES = {
        "Prologue: Monday Morning", // 0
        "Alli's Phone", // 1
        "Library Computers", // 2
        "Shelf 005", // 3
        "The Journal", // 4
        "Student Council Room", // 5
        "The Project Room", // 6
        "Library, Late Night", // 7
        "Security Office", // 8
        "Old Storage Room", // 9
        "The Mirror Room" // 10
    };

    // ============ LEVEL 0: PROLOGUE (draft, refined in Phase 5) ============
    public static final String[][] PROLOGUE = {
        {"Mom", "Arnie, breakfast! Don't be late again. Alli's mom already texted me twice."},
        {"Arnie", "Second place again last week. Fine. I'm fine."},
        {"Prof. Null", "Attendance. Alli? ... Alli?"},
        {"Arnie", "She's never absent. Something is wrong."},
        {"Arnie", "Wait. Why is Alli's phone in my bag?"},
        {"Arnie", "I don't remember getting here."} // foreshadowing gap
    };
    // =====================================================================
    // JOEL: LEVELS 3 TO 5
    // =====================================================================

    /**
     * Joins {speaker, text} lines into one block of text for a JTextArea.
     */
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
    public static final String ITEM_JOURNAL = "Journal";
    public static final String ITEM_EVIDENCE_AR = "Journal cover: scratched \"A.R.\"";

    // ---------- LEVEL 3: Shelf 005 ----------
    // ---------- LEVEL 3: Shelf 005 ----------
    public static final String[][] L3_WALK = {
        {"", "The library is quiet. The floor creaks under every step as you walk down the row of shelves to the number five."}
    };
    public static final String L3_WRONG_SHELF = "Wrong aisle. Amie's code pointed to shelf 005.";

    public static final String[][] L3_SHELF_INTRO = {
        {"", "Programming books, dim and dusty. One spine sticks out a finger's width. Its label is peeling and the letters have slipped out of order."},
        {"Arnie (thinking)", "The title is scrambled. Someone wanted me to work for it."}
    };

    public static final String L3_TILES = "PMOCRELI";     // the loose tiles, in this order
    public static final String L3_CATALOG
            = "Catalog printout: \"Shelf 005. Alli's last search: the program that turns your code into machine language.\"";
    public static final String[] L3_HINTS = {
        "It starts with the same letters as computer.",
        "C-O-M-P-I-L-E-R."
    };
    public static final String L3_TILES_WRONG = "That does not spell the title. Rearrange the tiles.";
    public static final String L3_TILES_RIGHT = "The tiles click into place: COMPILER.";

    /**
     * {slow} at the start of a line makes the typewriter slower.
     */
    public static final String[][] L3_FOUND = {
        {"", "The book slides out. Something heavier than paper drops into your hands: a journal."},
        {"Arnie (thinking)", "Hidden on purpose. And meant to be found."},
        {"Arnie (thinking)", "{slow} I know this slant. I've seen it on my own homework."},
        {"Arnie (thinking)", "Alli's, probably. We write alike."},
        {"Evidence", "{sfx:evidence} Scratched into the cover, two tiny letters: A.R."},
        {"Arnie (thinking)", "Probably nothing."}
    };

    // ---------- LEVEL 4: The Journal ----------
    public static final String[][] L4_INTRO = {
        {"", "Six names in blue ink. Three are crossed out so hard the paper tore."},
        {"Arnie (thinking)", "Amiel. Amy. Ami. Amy again. Amariah. Aimee. Why does every name start the same way?"}
    };

    // the choice moment
    public static final String L4_CHOICE_PROMPT = "The ink is pressed hard in places. Do I look closer?";
    public static final String L4_CHOICE_A = "Study the handwriting.";
    public static final String L4_CHOICE_B = "Skip it. Names, not penmanship.";
    public static final String[][] L4_AFTER_A = {
        {"Arnie (thinking)", "{dim} The pen pressed hardest on the cross-outs. The way I press when I'm angry."}
    };
    public static final String[][] L4_AFTER_B = {
        {"Arnie (thinking)", "Penmanship won't find her."}
    };

    // the puzzle
    public static final String L4_PROMPT
            = "Tap a card to flip it. CLEARED only if the fact proves they could not have been near Alli last night.";

    /**
     * Each row: {name, fact on the back, "cleared" or "suspect"}.
     */
    public static final String[][] L4_CARDS = {
        {"Amiel Castro", "Transferred to another campus in August.", "cleared"},
        {"Amy Dizon", "Exchange student in Japan until December.", "cleared"},
        {"Ami Tan", "Alli's cousin. Goes to a school in another city.", "cleared"},
        {"Amy Mercado", "Lost the scholarship slot to Alli, and left a voicemail on Alli's phone.", "suspect"},
        {"Amariah Navarro", "Kicked out of Alli's group project, and has been posting about it.", "suspect"},
        {"Aimee Ferrer", "Copied Alli's notes. Her library username AimeeF is close to AMIE.", "suspect"}
    };

    public static final String L4_RIGHT_CLEARED = " cannot have been near Alli. Cleared.";
    public static final String L4_RIGHT_SUSPECT = " could have been there. Suspect.";
    public static final String L4_WRONG_TO_CLEARED = " is not ruled out by that fact. Flip the card again.";
    public static final String L4_WRONG_TO_SUSPECT = " has a fact that rules them out. Flip the card again.";

    public static final String[] L4_HINTS = {
        "Ask yourself: could this person even be in the building?",
        "Three names have a fact that rules them out. The other three have a reason to be angry."
    };
    public static final String[][] L4_SOLVED = {
        {"Arnie (thinking)", "Three cleared. Three with reasons: Amy, Amariah, Aimee."}
    };
    // ---------- THE SUSPECT BOARD (between Level 4 and Level 5) ----------
    public static final String BOARD_TITLE = "The Suspect Board";

    /**
     * {min} is replaced by the real time left, for example "13 minutes".
     */
    public static final String[][] BOARD_INTRO = {
        {"", "You leave the library and stop by the lockers. About {min} left. You pin three names to the corkboard on the wall."},
        {"Arnie (thinking)", "Three names. Three reasons. I can't chase all of them at once. Who first?"}
    };
    public static final String BOARD_TAP = "Tap a card on the board to open its clue on Alli's phone.";
    public static final String BOARD_ALL_READ = "All three clues read. Close the phone to decide.";

    /**
     * Each row: {name, short reason (on the card), phone app, line under the
     * app, the clue}.
     */
    public static final String[][] BOARD_CARDS = {
        {"Amy Mercado",
            "Lost the scholarship slot to Alli",
            "Voicemail",
            "6:12 PM  |  Amy Mercado",
            "\"Enjoy the scholarship, Alli. Hope it was worth what it cost you.\""},
        {"Amariah Navarro",
            "Kicked out of Alli's group project",
            "Facebook",
            "Alli's feed  |  post tagged to her",
            "\"Some people take credit and call it teamwork.\""},
        {"Aimee Ferrer",
            "Copied Alli's notes",
            "Library log",
            "AimeeF is listed one line above AMIE",
            "login: ALLI\nlogin: AimeeF\nlogin: AMIE"}
    };

    public static final String[][] BOARD_SUMMARY = {
        {"Arnie (thinking)", "Amy left a threat. Amariah left a public jab. Aimee left a typo."}
    };
    public static final String BOARD_WHO = "Who do you question first?";
    public static final String[] BOARD_OPTIONS = {
        "Amy Mercado (strongest lead)", "Amariah Navarro", "Aimee Ferrer"
    };
    public static final String[][] BOARD_FIXED_ORDER = {
        {"Arnie (thinking)", "No. Amy first. A threat on a voicemail is the strongest lead. The others can wait."}
    };
        // ---------- LEVEL 5: Amy Mercado ----------
    public static final String L5_TITLE = "Amy Mercado";
    public static final String ITEM_AMY_CLEARED = "Amy Mercado: cleared";

    /** "Amy [angry]" = Amy speaks, and her picture changes to the angry one. */
    public static final String[][] L5_INTRO = {
        {"", "Amy never leaves the classroom when she's stressed. You push the door open."},
        {"Amy [angry]", "You don't get to walk in here like you own the place."},
        {"Arnie", "Alli is missing. You left her a voicemail."},
        {"Amy [angry]", "I was angry. She got the slot. But I'd never hurt her."},
        {"Amy [neutral]", "I was at the student council meeting all evening. Check it yourself, if you can tell proof from talk."}
    };

    public static final String L5_SLOT_TITLE = "Proof Amy was in the council room at 9:00 PM.";
    public static final String L5_PROMPT =
        "Drag the two cards that nobody could fake into the slot. The camera cut at 9:00 PM, so you need proof for exactly that time.";

    /** Each row: {title, text, "real" or "fake", why it fails (fake cards only)}. */
    public static final String[][] L5_CARDS = {
        {"Door log",
         "Scanned in at 7:01 PM, scanned out at 10:04 PM. No other scans.",
         "real", ""},
        {"Council minutes",
         "Signed by three other members. Amy presented the budget at 9:10 PM.",
         "real", ""},
        {"Selfie",
         "Posted by Amy at 9:02 PM. Timestamp edited by hand.",
         "fake", "The selfie's timestamp was edited by hand. Anyone can fake that."},
        {"Amy's statement",
         "Written by Amy herself.",
         "fake", "Her statement is only her word. She wrote it herself."}
    };

    public static final String L5_TRY_AGAIN = "Both cards are back in the tray. Try again.";
    public static final String L5_RIGHT =
        "The door log and the minutes were made by other people, at the time it happened. Nobody could fake them.";
    public static final String[] L5_HINTS = {
        "Which cards were made by someone else, at the time it happened?",
        "A statement and an edited photo can be faked. The door log and the minutes can't."
    };

    public static final String[][] L5_AFTER = {
        {"Amy [sad]", "I'm a lot of things. Not that."},
        {"Amy [suspicious]", "And wait, you know... you were the last person seen with her."},
        {"Arnie (thinking)", "{shake} I... what?"},
        {"Amy [suspicious]", "Look closer to home."}
    };
    public static final String L5_CLEARED = "Amy Mercado is cleared. No time was deducted.";
    // TODO Phase 6: LEVEL4 names, LEVEL5 riddles, LEVEL6 posts, LEVEL7 Morse
    // TODO Phase 7: LEVEL8 timeline, LEVEL9 storage room, LEVEL10 mirror, endings

    // ============ HINTS (one per puzzle, filled in Phase 3) ============
    // TODO: HINT_CAESAR, HINT_BINARY, HINT_SCRAMBLE, HINT_RIDDLE, HINT_MORSE, HINT_LOCK
    // ============ PUZZLE DATA ============
    // The numbers connect across levels:
    //   shift 14 = the day of the science fair (Level 1)
    //   shelf 005 = the month the fair was held (Level 2)
    //   lock 1405 = day + month, a date only Arnie remembers (Level 9)
    public static final String CAESAR_PLAIN = "meet me here";
    public static final int CAESAR_SHIFT = 14;
    public static final int BINARY_VALUE = 5;
    public static final int BINARY_BITS = 8;
    public static final String SCRAMBLE_WORD = "COMPILER";
    public static final String MORSE_WORD = "NOTES";
    public static final String LOCK_CODE = "1405";

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
    public static final String HINT_CAESAR
            = "Every letter was pushed forward by the same number. Try a date from Arnie's life: the science fair was on the 14th.";
    public static final String HINT_BINARY
            = "Read the digits from the right: 1, 2, 4, 8... Add up the place values that have a 1.";
    public static final String HINT_SCRAMBLE
            = "It starts with the same letters as computer.";
    public static final String HINT_MORSE
            = "A dot is short and a dash is long. The first letter is N: dash, dot.";
    public static final String HINT_LOCK
            = "Two clues you already solved were numbers: the shift and the shelf. Day first, then month.";
    // ============ ENDINGS ============
    public static final String GOOD_LAST_LINE
            = "Some things can't be fixed in a day. But she stayed to listen.";

    public static final String BAD_LAST_LINE = "You lost to yourself.";
}
