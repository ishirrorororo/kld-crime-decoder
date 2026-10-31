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
        {"Evidence", "Scratched into the cover, two tiny letters: A.R."},
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
    // ---------- LEVEL 6: Amariah Navarro ----------
    public static final String L6_TITLE = "Amariah Navarro";
    public static final String ITEM_AMARIAH_CLEARED = "Amariah Navarro: cleared";
    public static final String ITEM_CLUE_CLOSET = "Clue: the janitor's closet";

    public static final String[][] L6_INTRO = {
        {"", "Amy's door closes behind you. In the hallway, Alli's phone buzzes with a new post. It's Amariah, tagged to Alli. She's leaning against the lockers, scrolling."},
        {"Amariah", "You're here about the posts, aren't you?"},
        {"Arnie", "Alli kicked you out of the group project."},
        {"Amariah", "She did. I was mad. I posted some things I regret, and then I posted some kinder ones. I never sent a message. People tell me it spells something."},
        {"", "Open the phone. Her last five posts glow on the Facebook feed."}
    };

    public static final String L6_PHONE_TITLE = "Alli's feed  |  Amariah Navarro";
    public static final String L6_PROMPT = "Type the word the posts spell:";

    /** Her last five posts. The first letter of each one spells the answer. */
    public static final String[] L6_POSTS = {
        "Some days I hate group work.",
        "Often I speak before I think.",
        "Rumors say I quit. I didn't.",
        "Really, I'm not angry anymore.",
        "You were right, Alli."
    };
    
    public static final String[] L6_TIMES = {"2 hours ago", "1 hour ago", "45 min ago", "20 min ago", "Just now"};
    public static final String L6_SCROLL_ALL = "Scroll through all five posts first.";
    public static final String L6_ANSWER = "SORRY";

    public static final String[] L6_HINTS = {
        "Look at the first letter of every post. Skip the rest.",
        "S, O, R, R and one more."
    };
    public static final String L6_WRONG = "No. That's not what the page is saying.";
    public static final String L6_RIGHT = "Click. Something falls into place.";

    public static final String[][] L6_AFTER = {
        {"Amariah", "I tried to say it that night. I saw her walking toward the janitor's closet at the end of the hall."},
        {"Amariah", "You seem tired, Arnie. Like you're somewhere else."},   // foreshadowing
        {"Arnie", "Exams. That's all."},
        {"", "Amariah is cleared. Clue added: the janitor's closet."}
    };
    
    // ---------- LEVEL 7: Aimee Ferrer ----------
    public static final String L7_TITLE = "Aimee Ferrer";
    public static final String ITEM_AIMEE_CLEARED = "Aimee Ferrer: cleared";
    public static final String ITEM_COMP7_LOG = "Evidence: Computer 7 log";

    public static final String[][] L7_INTRO = {
        {"", "Two names down. You go back to the library, to the terminal row. Aimee is at Computer 4, hunched over a laptop."},
        {"Aimee", "It was just her notes! I didn't touch anything else, I swear."},
        {"Arnie", "I don't care about notes. I need the login log."},
        {"Aimee", "My username is AimeeF. The system keeps mixing it up with Amie. That's why I got flagged."},
        {"Aimee", "But you can tell us apart. Look at where we sit."}
    };

    public static final String L7_LOG_TITLE = "Library terminal log  |  who logged in?";
    public static final String L7_STICKY_AIMEE =
        "<html><center>AimeeF always sits by the window, and always logs out.</center></html>";
    public static final String L7_STICKY_AMIE =
        "<html><center>AMIE never logs out.</center></html>";
    public static final String L7_COL_AIMEE = "AimeeF";
    public static final String L7_COL_AMIE  = "AMIE";
    public static final String L7_PROMPT = "Drag each tag onto the sheet, under AimeeF or AMIE, then press Check.";

    // the six entries, in the order shown in the tray (mixed on purpose)
    // last column: a session length means the person logged out; "no logout" means they never did
    public static final String[] L7_ENTRIES = {
        "Computer 4 | by the window | 8:05 PM | 35 min",
        "Computer 7 | back wall | 9:40 PM | no logout",
        "Computer 7 | back wall | 6:10 PM | no logout",
        "Computer 4 | by the window | 8:50 PM | 25 min",
        "Computer 4 | by the window | 7:20 PM | 40 min",
        "Computer 7 | back wall | 4:35 PM | no logout"
    };
    // 1 = AimeeF, 2 = AMIE (same order as L7_ENTRIES)
    public static final int[] L7_CORRECT = {1, 2, 2, 1, 1, 2};

    public static final String L7_COLUMN_FULL = "That column only has three slots.";
    public static final String L7_NOT_ALL = "Sort all six entries first.";
    public static final String L7_WRONG = "Some entries don't match the sticky note. Wrong ones went back to the tray.";
    public static final String L7_RIGHT = "Every entry fits the sticky note.";
    public static final String[] L7_HINTS = {
        "Check the two rules on the sticky note.",
        "A session length means the person logged out. \"No logout\" means they never did."
    };

    public static final String[][] L7_AFTER = {
        {"Aimee", "So the real Amie was at Computer 7 the whole time. Even before last night."},
        {"Arnie (thinking)", "{slow} Computer seven. That's the one by the back wall. That's where I always sit."},
        {"Arnie (thinking)", "{dim} Lots of people sit there."},
        {"", "Aimee is cleared. Evidence added: Computer 7 log."}
    };
    
    // ---------- LEVEL 8: The Front Desk (Joel) ----------
    public static final String L8_TITLE = "The Front Desk";
    public static final String ITEM_CLOSET_CAMERA = "Clue: the closet (camera)";

    // Rows are {speaker, line} or {speaker, line, joelMood}. joelMood = happy / sad / confused / neutral
    // and stays until the next row that sets one. Level8 shows the matching joel_<mood>.png.
    public static final String[][] L8_INTRO = {
        {"", "Three names cleared, and still no Alli. Nine minutes left. The only thing left is the camera feed. You walk to the front desk.", "happy"},
        {"Joel", "Hey detective! Want pandesal? It's the only thing keeping me awake."},
        {"Joel", "I saw Alli last night. Or a shadow. Or a mannequin... or.. jeez, I'm getting chills.", "confused"},
        {"Arnie", "Kuya Joel. Focus."},
        {"Joel", "Right, right. The cameras saw more than I did. Four clips from last night, but the timestamps got wiped.", "neutral"}
    };

    public static final String L8_PROMPT = "Read the wall clock in each clip. Drag clips onto each other to swap them until each sits on its time, then press Check.";

    // clip captions, in order A, B, C, D (the answer order)
    public static final String[] L8_CLIPS = {
        "Clip A: Alli leaves the classroom with a blue folder.",
        "Clip B: Alli goes in the library door.",
        "Clip C: Alli walks toward the closet.",
        "Clip D: static, a shadow steps toward the lens."
    };
    // what the hallway clock in each clip shows (clip D has no readable clock)
    public static final String[] L8_CLOCKS = {"a quarter to eight", "ten past eight", "half past eight", "static"};
    public static final String[] L8_SLOTS = {"7:45 PM", "8:10 PM", "8:30 PM", "9:00 PM"};
    // which clip starts on each screen (screen 0 = 7:45 PM ... screen 3 = 9:00 PM): C, D, A, B. Every clip starts in the wrong place.
    public static final int[] L8_START = {2, 3, 0, 1};

    public static final String L8_WRONG = "The clock hands don't match those times. Keep swapping.";
    public static final String L8_RIGHT = "The timeline fits the clocks.";
    public static final String[] L8_HINTS = {
        "Read the hands on the wall clock in each clip.",
        "A is a quarter to eight. D is the static one, so it goes last."
    };

    // choice moment (after the clips are in order)
    public static final String L8_CHOICE_PROMPT = "Clip D is still playing on the monitor. What do you do?";
    public static final String L8_CHOICE_REWIND = "Rewind clip D.";     // lowers denial, costs time (ScoreManager.onChoiceRead)
    public static final String L8_CHOICE_SKIP   = "Skip it.";           // raises denial (ScoreManager.onChoiceSkip)
    public static final String[][] L8_REWIND = {
        {"Arnie (thinking)", "{flicker} The feed dies at nine, right as someone steps toward the camera. Someone my height."}
    };
    public static final String[][] L8_SKIP = {
        {"Arnie (thinking)", "Cameras can't tell me where she is now."}
    };

    public static final String[][] L8_AFTER = {
        {"Arnie (thinking)", "{dim} Seven forty-five. Eight-ten. Eight-thirty. Every gap in this timeline is a gap in my memory.", "neutral"},
        {"Joel", "You okay? You went kind of pale.", "sad"},
        {"Joel", "Clip C was the last one that showed her. She was walking to the janitor's closet.", "neutral"},
        {"", "Clue added: the closet, confirmed by the camera."}
    };
    // TODO  LEVEL9 storage room, LEVEL10 mirror, endings

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
    
    // ============ WARNINGS (TimerManager and ScoreManager show these) ============
    public static final String WARN_SPEAKER = "Arnie (thinking)";
    public static final String TIMER_5MIN = "The hallway clock is louder than it was a minute ago.";
    public static final String TIMER_2MIN = "{dim} Stop reading. Move.";
    /** Plays once, the first time the hidden Denial meter goes above 30. Spoken as narration (no name plate). */
    public static final String DENIAL_WARNING = "A different voice, same handwriting: \"You don't need the rest of that page.\"";

    // ============ RANKS (final screen). Matches ScoreManager.RANK_MIN_SCORE ============
    public static final String[] RANK_NAMES = {
        "Trainee", "Junior Detective", "Detective", "Chief Inspector"
    };
    
    // ============ ENDINGS ============
    public static final String GOOD_LAST_LINE
            = "Some things can't be fixed in a day. But she stayed to listen.";

    public static final String BAD_LAST_LINE = "You lost to yourself.";
}
