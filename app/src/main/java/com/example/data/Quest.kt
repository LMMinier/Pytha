package com.example.data

data class Quest(
    val id: String,
    val title: String,
    val levelName: String,
    val icon: String,
    val description: String,
    val story: String,
    val instruction: String,
    val startingCode: String,
    val blocks: List<String>,
    val targetKeyword: String,
    val targetEmoji: String?,
    val conceptTitle: String,
    val conceptExplanation: String
)

object QuestData {
    val quests = listOf(
        Quest(
            id = "quest_1",
            title = "Summon Your First Animal!",
            levelName = "Level 1: Say Hello!",
            icon = "🐍",
            description = "Learn how to use Python's print() magic spell to display characters on the screen!",
            story = "Hi there, aspiring coder! I'm Monty, your Python snake friend. 🐍 In Python, we have a magic command called print(). Whatever you put inside this command, Python will show it on the screen! Today, a lonely lion wants to come out and play on our meadow. Can you help me summon him?",
            instruction = "Tap the puzzle blocks to write print(\"🦁\") and click the RUN button to summon your lion!",
            startingCode = "# Summon a lion here!\n",
            blocks = listOf("print(", "\"🦁\"", "\"🍕\"", "\"🚀\"", ")"),
            targetKeyword = "print",
            targetEmoji = "🦁",
            conceptTitle = "The print() Spell",
            conceptExplanation = "In Python, print() is a built-in function. It takes whatever is inside the parentheses (like text or an emoji) and displays it on the screen! Strings are written inside quotes (like \"🦁\") so Python knows it is text."
        ),
        Quest(
            id = "quest_2",
            title = "The Magical Variable Box",
            levelName = "Level 2: Variables!",
            icon = "🎁",
            description = "Discover Variables - special named containers where you can store values!",
            story = "Woah! You're a natural! Now, imagine you have a secret storage box. 🎁 In Python, we call this a variable. You can give the box any name you like, and put a value (like a balloon \"🎈\") inside it! Later, when you print the name of the variable, Python will retrieve the value!",
            instruction = "Create a box named 'toy' and store \"🎈\" in it. Then, print(toy) to let it float!",
            startingCode = "toy = \"🎈\"\n# Write print(toy) below:\n",
            blocks = listOf("print(", "toy", "\"🎈\"", ")"),
            targetKeyword = "print(toy)",
            targetEmoji = "🎈",
            conceptTitle = "Variables (Data Containers)",
            conceptExplanation = "A variable is like a named box in computer memory. Here, 'toy' is the name of the box, and we used '=' to put the balloon '🎈' inside it. When you run print(toy), Python looks inside the box named 'toy' and displays the balloon!"
        ),
        Quest(
            id = "quest_3",
            title = "The Copy Machine",
            levelName = "Level 3: Super Loops!",
            icon = "🔄",
            description = "Use loops to repeat your commands and spawn a whole shower of stars!",
            story = "Amazing! Now, what if you wanted to summon 3 shiny stars? ⭐⭐⭐ Typing print() three times is too tiring! Instead, we can use a loop. A loop is like an automated machine that repeats your commands as many times as you want!",
            instruction = "Write a loop to print(\"⭐\") three times!",
            startingCode = "for i in range(3):\n    # Type print(\"⭐\") below (make sure to indent it with spaces!):\n    ",
            blocks = listOf("for i in range(3):", "  print(", "\"⭐\"", ")"),
            targetKeyword = "range(3)",
            targetEmoji = "⭐",
            conceptTitle = "For Loops",
            conceptExplanation = "A 'for' loop tells Python to repeat a block of code. 'for i in range(3):' means 'repeat the indented lines under me exactly 3 times!'. Python uses indentation (spaces) to know which lines belong inside the loop."
        ),
        Quest(
            id = "quest_4",
            title = "The Decision Maker",
            levelName = "Level 4: If Statements!",
            icon = "🚦",
            description = "Teach Python how to make smart choices using true/false conditions!",
            story = "You are practically a Python Master now! 🎓 Sometimes, computer programs need to make choices based on conditions. Like: If it is sunny, show a sun! Otherwise (else), show rain! We can do this using 'if' and 'else'. Let's see if we can reward a high score!",
            instruction = "Make Python choose what to print! If the score is high, print a trophy \"🏆\"!",
            startingCode = "score = 10\nif score > 5:\n    # Print a trophy here!\n    \nelse:\n    print(\"🌧️\")\n",
            blocks = listOf("print(", "\"🏆\"", "\"😢\"", ")"),
            targetKeyword = "if",
            targetEmoji = "🏆",
            conceptTitle = "If-Else Statements",
            conceptExplanation = "An 'if' statement tests a condition (like 'is the score greater than 5?'). If that condition is true, Python runs the code block directly under 'if'. If it is false, Python skips to the 'else:' block!"
        )
    )
}
