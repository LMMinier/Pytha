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
            title = "Boot Up the AI Core!",
            levelName = "Level 1: System Boot",
            icon = "🤖",
            description = "Run your first Python output command to initialize the system console!",
            story = "Welcome to the Python Quest Cyber Terminal. You are initiating standard mainframe terminal access. In Python, the print() function sends data out to the display buffer. Let's start by waking up the primary AI Daemon (represented by 🤖) to verify system-wide visual response.",
            instruction = "Tap the puzzle blocks to write print(\"🤖\") and execute the runtime!",
            startingCode = "# Wake up the AI Daemon!\n",
            blocks = listOf("print(", "\"🤖\"", "\"💥\"", "\"⚡\"", ")"),
            targetKeyword = "print",
            targetEmoji = "🤖",
            conceptTitle = "Standard Output",
            conceptExplanation = "The print() function is the fundamental way to communicate with developers. It evaluates the expression inside the parentheses (arguments) and writes it to the standard output terminal. String literals are enclosed in quotes."
        ),
        Quest(
            id = "quest_2",
            title = "Memory Allocation Key",
            levelName = "Level 2: Variables",
            icon = "🔑",
            description = "Discover Variables - named memory locations used to store program data dynamically!",
            story = "Console initialized. Next, we must register credentials. In programming, we store values in memory using 'variables' (custom labeled containers). By assigning an access token (like a key \"🔑\") to a variable name, we can refer back to it and execute authorization scripts seamlessly!",
            instruction = "Create a box named 'token' and store \"🔑\" in it. Then, print(token) to request node access!",
            startingCode = "token = \"🔑\"\n# Write print(token) below to load the credentials:\n",
            blocks = listOf("print(", "token", "\"🔑\"", ")"),
            targetKeyword = "print(token)",
            targetEmoji = "🔑",
            conceptTitle = "Variable Assignment",
            conceptExplanation = "Variables hold values so your programs can remember and manipulate data. Writing token = \"🔑\" assigns the string \"🔑\" to the variable named 'token'. Calling print(token) retrieves whatever is currently stored in that address."
        ),
        Quest(
            id = "quest_3",
            title = "Threat Mitigation Loop",
            levelName = "Level 3: Automation Loops",
            icon = "🛡️",
            description = "Automate security loops to clear firewall blocks with repeated shield pings!",
            story = "Alert! Minor intrusion detected in firewall sector 3. To defend our subnet, we need to deploy three visual defensive shields. Instead of writing print statements over and over, professional developers automate execution using a 'for loop'!",
            instruction = "Write an automated loop to print(\"🛡️\") three times and secure the sector!",
            startingCode = "for i in range(3):\n    # Type print(\"🛡️\") below (make sure to indent it with spaces!):\n    ",
            blocks = listOf("for i in range(3):", "  print(", "\"🛡️\"", ")"),
            targetKeyword = "range(3)",
            targetEmoji = "🛡️",
            conceptTitle = "For Loops",
            conceptExplanation = "A 'for' loop tells Python to repeat a block of code. 'for i in range(3):' means 'repeat the indented lines under me exactly 3 times!'. Python uses indentation (spaces) to define block scoping."
        ),
        Quest(
            id = "quest_4",
            title = "The Decryption Branch Gate",
            levelName = "Level 4: Boolean Conditions",
            icon = "🔓",
            description = "Design an active decider that branches execution based on system conditions!",
            story = "Excellent progress, Operator. We're at the bypass router! Sometimes programs make logical choices dynamically based on variables. If system power is optimal, output '🔓' to bypass the node; otherwise (else), return the alert '🚨'. Let's write the decider!",
            instruction = "Complete the decider block! If system power is high, output the bypass signal \"🔓\"!",
            startingCode = "power = 10\nif power > 5:\n    # Print the bypass success signal '🔓' here!\n    \nelse:\n    print(\"🚨\")\n",
            blocks = listOf("print(", "\"🔓\"", "\"🚨\"", ")"),
            targetKeyword = "if",
            targetEmoji = "🔓",
            conceptTitle = "Conditional Logic",
            conceptExplanation = "An 'if' statement tests a boolean expression. If that condition is true, only the block indented under 'if' runs. Otherwise, execution shifts to the 'else:' block. This is how programs make adaptive decisions."
        )
    )
}
