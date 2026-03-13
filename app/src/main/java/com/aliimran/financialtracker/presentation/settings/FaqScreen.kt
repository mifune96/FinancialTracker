package com.aliimran.financialtracker.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.presentation.theme.PrimaryYellow

private data class FaqItem(val question: String, val answer: String)

private val faqList = listOf(
    FaqItem(
        question = "Bagaimana cara menambah transaksi?",
        answer = "Tekan tombol \"+\" kuning di pojok kanan bawah layar. " +
                "Pilih tipe transaksi (Pengeluaran atau Pemasukan), pilih kategori, " +
                "masukkan nominal, tambahkan catatan jika perlu, lalu tekan \"Simpan\".",
    ),
    FaqItem(
        question = "Bagaimana cara mengedit atau menghapus transaksi?",
        answer = "Buka tab \"Riwayat\", lalu tekan transaksi yang ingin diubah. " +
                "Di halaman detail, kamu bisa mengedit atau menghapus transaksi tersebut.",
    ),
    FaqItem(
        question = "Bagaimana cara mengekspor data ke CSV?",
        answer = "Buka menu \"Saya\" → \"Pengaturan\" → \"Ekspor Data\". " +
                "File CSV akan dibuat dan kamu bisa membagikannya ke aplikasi lain " +
                "seperti WhatsApp, Email, atau Google Drive.",
    ),
    FaqItem(
        question = "Bagaimana cara mengelola kategori?",
        answer = "Buka \"Saya\" → \"Pengaturan\" → \"Pengaturan Kategori\". " +
                "Di sana kamu bisa menambah, mengedit, atau menghapus kategori " +
                "untuk pengeluaran maupun pemasukan.",
    ),
    FaqItem(
        question = "Apakah data saya aman?",
        answer = "Ya! Semua data disimpan secara lokal di perangkat kamu. " +
                "Tidak ada data yang dikirim ke server manapun. " +
                "Pastikan untuk rutin mengekspor data sebagai cadangan.",
    ),
    FaqItem(
        question = "Bagaimana cara menghapus semua data?",
        answer = "Buka \"Saya\" → \"Pengaturan\" → \"Hapus Semua Data\". " +
                "Perhatian: tindakan ini tidak dapat dibatalkan dan akan menghapus " +
                "semua transaksi secara permanen.",
    ),
    FaqItem(
        question = "Apa fungsi Pengingat Harian?",
        answer = "Jika diaktifkan, kamu akan menerima notifikasi setiap malam jam 20:00 " +
                "untuk mengingatkan mencatat pengeluaran harian. " +
                "Aktifkan di \"Pengaturan\" → \"Pengingat Harian\".",
    ),
    FaqItem(
        question = "Bagaimana cara melihat grafik pengeluaran?",
        answer = "Buka tab \"Grafik\" di navigasi bawah. Di sana kamu bisa melihat " +
                "ringkasan pengeluaran dan pemasukan dalam bentuk grafik per bulan.",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    onNavigateBack: () -> Unit,
) {
    val expandedItems = remember { mutableStateListOf<Int>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bantuan & FAQ", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        ) {
            item {
                Text(
                    text     = "Pertanyaan yang sering ditanyakan",
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    faqList.forEachIndexed { index, faq ->
                        val isExpanded = index in expandedItems

                        // Question row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isExpanded) expandedItems.remove(index)
                                    else expandedItems.add(index)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QuestionAnswer,
                                contentDescription = null,
                                tint     = PrimaryYellow,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text       = faq.question,
                                style      = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier   = Modifier.weight(1f),
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Outlined.ExpandLess
                                              else Icons.Outlined.ExpandMore,
                                contentDescription = null,
                                tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        // Answer (animated)
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter   = expandVertically(),
                            exit    = shrinkVertically(),
                        ) {
                            Text(
                                text     = faq.answer,
                                style    = MaterialTheme.typography.bodySmall,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        PrimaryYellow.copy(alpha = 0.06f),
                                    )
                                    .padding(start = 48.dp, end = 16.dp, top = 4.dp, bottom = 14.dp),
                            )
                        }

                        // Divider (except last)
                        if (index < faqList.lastIndex) {
                            HorizontalDivider(
                                modifier  = Modifier.padding(start = 48.dp),
                                color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 0.5.dp,
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp).navigationBarsPadding()) }
        }
    }
}
