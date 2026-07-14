package com.example.data

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val recommendedAge: String,
    val coverEmoji: String,
    val ccLicense: String,
    val chapters: List<BookChapter>
)

data class BookChapter(
    val title: String,
    val content: String,
    val tryCode: String
)

object BookData {
    val books = listOf(
        Book(
            id = "games_python",
            title = "Invent Your Own Computer Games with Python",
            author = "Al Sweigart",
            description = "Learn how to program by making fun, interactive text-based computer games! Ideal for visual thinkers and younger learners.",
            recommendedAge = "Ages 8-13 (Young Wizards & Junior Coders)",
            coverEmoji = "🎮",
            ccLicense = "Licensed under CC BY-NC-SA 3.0",
            chapters = listOf(
                BookChapter(
                    title = "Chapter 1: The Magic of Print",
                    content = "In game programming, the computer needs to speak to the player. We do this by printing text to the screen.\n\nIn Python, the `print()` function displays whatever is between the quotation marks. E.g., `print(\"Hello, Player!\")` outputs the text on the screen.\n\nTry writing a greeting to welcome players to your first game adventure!",
                    tryCode = "# Start your game adventure!\nprint(\"🎮 Welcome to the Dragon's Castle! 🐉\")\nprint(\"Are you ready to play?\")"
                ),
                BookChapter(
                    title = "Chapter 2: Storing Game Scores with Variables",
                    content = "To make a game interactive, we need to remember things like the player's name, health, or score. We store these in named boxes called variables!\n\nIn Python, you create a variable by typing its name, an equal sign `=`, and the value you want to store.\n\nFor example:\n`score = 100`\n`player_name = \"Hero\"`\n\nLet's print out our game stats!",
                    tryCode = "player_name = \"Aventurer\"\nscore = 150\nprint(\"Player:\")\nprint(player_name)\nprint(\"Current Score:\")\nprint(score)"
                ),
                BookChapter(
                    title = "Chapter 3: Dragon Game Decisions (If/Else)",
                    content = "Every good game has choices. In our dragon game, if the player chooses the friendly cave, they get gold! Otherwise, they meet a hungry dragon.\n\nWe write these decisions using `if` and `else` statements.\n\nLet's test if the player survives!",
                    tryCode = "cave_choice = 2\nif cave_choice == 1:\n    print(\"You found a treasury of gold! 🏆\")\nelse:\n    print(\"Oh no! A dragon breathes fire! 🐉🔥\")"
                )
            )
        ),
        Book(
            id = "automate_boring",
            title = "Automate the Boring Stuff with Python",
            author = "Al Sweigart",
            description = "A practical guide to programming for total beginners. Learn how to write simple scripts to do tasks that would take hours to do manually.",
            recommendedAge = "Ages 11-17 (Junior Coders & Apprentice Engineers)",
            coverEmoji = "🤖",
            ccLicense = "Licensed under CC BY-NC-SA 3.0",
            chapters = listOf(
                BookChapter(
                    title = "Chapter 1: Python Basics & Automation",
                    content = "Python is an incredibly powerful language because it allows you to automate repetitive tasks. Before automating, you need to understand expressions and data types.\n\nPython can perform arithmetic just like a calculator! E.g. `2 + 3 * 4` evaluates to `14`.\n\nTry running basic math expressions to see Python calculate them instantly!",
                    tryCode = "# Python as a super calculator!\napples = 5\noranges = 3\ntotal_fruit = apples + oranges\nprint(\"Total fruits calculated by Python:\")\nprint(total_fruit)"
                ),
                BookChapter(
                    title = "Chapter 2: Control Flow Loops",
                    content = "To automate things, you need to execute code blocks multiple times. This is done with loops!\n\nA `while` loop keeps executing as long as a condition is True. A `for` loop repeats a specific number of times using the `range()` function.\n\nLet's make Python count to 5 automatically!",
                    tryCode = "print(\"Starting automation task...\")\nfor i in range(5):\n    print(\"🤖 File backup completed!\")"
                )
            )
        ),
        Book(
            id = "think_python",
            title = "Think Python: How to Think Like a Computer Scientist",
            author = "Allen B. Downey",
            description = "An introduction to Python programming that emphasizes design, debugging, and computational thinking.",
            recommendedAge = "Ages 14+ (Apprentice Engineers & Curious Minds)",
            coverEmoji = "🎓",
            ccLicense = "Licensed under CC BY-NC-SA 4.0",
            chapters = listOf(
                BookChapter(
                    title = "Chapter 1: The Way of the Program",
                    content = "A computer scientist thinks like an engineer, a mathematician, and an artist. The most important skill is problem solving.\n\nIn this chapter, we explore how algorithms are formulated and expressed in Python. We use the formal syntax of the interpreter to run commands.\n\nLet's verify Python's type system and execution flow.",
                    tryCode = "# In Python, variables are dynamically typed\nx = 42\nprint(\"Value of x:\")\nprint(x)\nx = \"Now I'm a string!\"\nprint(x)"
                ),
                BookChapter(
                    title = "Chapter 2: Functions and Interface Design",
                    content = "A function is a named sequence of statements that performs a computation. When you define a function, you specify the name and the sequence of statements.\n\nLater, you can call the function by name to run those statements.",
                    tryCode = "# Let's simulate a basic custom routine!\nprint(\"Step 1: Wake up\")\nprint(\"Step 2: Learn Python\")\nprint(\"Step 3: Repeat!\")"
                )
            )
        ),
        Book(
            id = "python_everybody",
            title = "Python for Everybody",
            author = "Charles R. Severance",
            description = "An excellent, zero-assumptions guide to programming using Python, focusing on informatics and processing data.",
            recommendedAge = "Ages 18+ (Curious Minds & Aspiring Developers)",
            coverEmoji = "🌍",
            ccLicense = "Licensed under CC BY-NC-SA 3.0",
            chapters = listOf(
                BookChapter(
                    title = "Chapter 1: Why Should You Learn to Write Code?",
                    content = "Computers are built to help us do tasks, but they lack creativity. By learning Python, we gain the capability to instruct the computer to perform complex data extraction, calculations, and networking tasks.\n\nLet's write a simple program that greets the user and performs a simple calculation.",
                    tryCode = "# A friendly introduction program\nuser = \"Curious Mind\"\nprint(\"Hello, Welcome to Python for Everybody, \")\nprint(user)\nyears = 5\nseconds_in_year = 365 * 24 * 60 * 60\nprint(\"Seconds in 5 years:\")\nprint(years * seconds_in_year)"
                )
            )
        )
    )
}
