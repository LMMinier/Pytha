package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Book
import com.example.data.BookChapter
import com.example.data.BookData
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun BooksScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSandbox: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTier by viewModel.selectedAgeTier.collectAsState()
    
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var activeChapterIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            if (activeChapterIndex != null) {
                                activeChapterIndex = null
                            } else if (selectedBook != null) {
                                selectedBook = null
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("books_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back",
                            tint = TextDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Interactive Library",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsPurple
                        )
                        Text(
                            text = when {
                                activeChapterIndex != null -> selectedBook?.chapters?.getOrNull(activeChapterIndex ?: 0)?.title ?: "Chapter"
                                selectedBook != null -> selectedBook!!.title
                                else -> "Python Bookshelf 📚"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            maxLines = 1
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KidsBg)
                .padding(innerPadding)
        ) {
            when {
                // CHAPTER READER VIEWER
                selectedBook != null && activeChapterIndex != null -> {
                    val chapter = selectedBook!!.chapters[activeChapterIndex!!]
                    ChapterReaderView(
                        chapter = chapter,
                        onTryInSandbox = {
                            viewModel.addXp(30, "Read Chapter Practice")
                            viewModel.updatePlaygroundCode(chapter.tryCode)
                            viewModel.resetResult()
                            onNavigateToSandbox()
                        },
                        onPrevChapter = if (activeChapterIndex!! > 0) {
                            { activeChapterIndex = activeChapterIndex!! - 1 }
                        } else null,
                        onNextChapter = if (activeChapterIndex!! < selectedBook!!.chapters.size - 1) {
                            { activeChapterIndex = activeChapterIndex!! + 1 }
                        } else null
                    )
                }
                
                // BOOK INDEX / CHAPTERS LIST VIEW
                selectedBook != null -> {
                    BookDetailsView(
                        book = selectedBook!!,
                        onSelectChapter = { index -> activeChapterIndex = index }
                    )
                }
                
                // GENERAL BOOKSHELF GRID LIST VIEW
                else -> {
                    BookshelfView(
                        selectedTier = selectedTier ?: "8_10",
                        onSelectBook = { book -> selectedBook = book }
                    )
                }
            }
        }
    }
}

@Composable
fun BookshelfView(
    selectedTier: String,
    onSelectBook: (Book) -> Unit
) {
    val tierFriendlyName = when (selectedTier) {
        "8_10" -> "Cyberpunk Novice (Shell Access)"
        "11_13" -> "Scripting Apprentice (Automation Specialist)"
        "14_17" -> "AI Apprentice (Neural Net Developer)"
        else -> "Mainframe Architect (System Engineer)"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F0FC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tailored for Your Age: $tierFriendlyName 🎯",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsPurple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We have arranged these open-source books to perfect your learning speed. Tap any book below to start reading chapters offline!",
                        fontSize = 12.sp,
                        color = TextLight,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(BookData.books) { book ->
            val isRecommended = when (selectedTier) {
                "8_10" -> book.id == "games_python"
                "11_13" -> book.id == "games_python" || book.id == "automate_boring"
                "14_17" -> book.id == "automate_boring" || book.id == "think_python"
                else -> book.id == "think_python" || book.id == "python_everybody"
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isRecommended) 2.dp else 1.dp,
                    color = if (isRecommended) KidsOrange else KidsBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectBook(book) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isRecommended) Color(0xFFFFF2E6) else Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = book.coverEmoji, fontSize = 36.sp)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        if (isRecommended) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(KidsOrange)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⭐️ RECOMMENDED FOR YOU",
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Text(
                            text = book.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            lineHeight = 22.sp
                        )
                        Text(
                            text = "by ${book.author}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = book.description,
                            fontSize = 12.sp,
                            color = TextLight,
                            lineHeight = 16.sp,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = book.recommendedAge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KidsPurple
                            )
                            Text(
                                text = "CC License",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookDetailsView(
    book: Book,
    onSelectChapter: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = book.coverEmoji, fontSize = 56.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = book.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "by ${book.author}",
                    fontSize = 14.sp,
                    color = TextLight,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, KidsBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = book.ccLicense,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Text(
                text = "Chapters in this Book:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        items(book.chapters.indices.toList()) { index ->
            val chapter = book.chapters[index]
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, KidsBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectChapter(index) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEDE9FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsPurple
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = chapter.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Read",
                        tint = KidsPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterReaderView(
    chapter: BookChapter,
    onTryInSandbox: () -> Unit,
    onPrevChapter: (() -> Unit)?,
    onNextChapter: (() -> Unit)?
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // CONTENT CARD
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, KidsBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = chapter.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = chapter.content,
                    fontSize = 15.sp,
                    color = TextDark,
                    lineHeight = 24.sp
                )
            }
        }

        // INTERACTIVE SPELL CARD / EXPERIMENT
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF282535)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Interactive Practice Code:",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = chapter.tryCode,
                        color = Color(0xFFC9C4E0),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onTryInSandbox,
                    colors = ButtonDefaults.buttonColors(containerColor = KidsOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Sandbox",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TRY IN SANDBOX! 🎈", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // PREV / NEXT NAVIGATION CONTROLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onPrevChapter?.invoke() },
                enabled = onPrevChapter != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KidsPurple,
                    disabledContainerColor = KidsBorder
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.width(130.dp)
            ) {
                Text("Previous", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onNextChapter?.invoke() },
                enabled = onNextChapter != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KidsPurple,
                    disabledContainerColor = KidsBorder
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.width(130.dp)
            ) {
                Text("Next", fontWeight = FontWeight.Bold)
            }
        }
    }
}
