package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Maqam
import com.example.model.MaqamFamily
import com.example.ui.theme.QuarterToneAccent
import com.example.viewmodel.PlaybackUiState
import com.example.viewmodel.QuarterToneFilter

@Composable
fun EncyclopediaScreen(
    maqamat: List<Maqam>,
    featuredMaqam: Maqam,
    searchQuery: String,
    selectedFamily: MaqamFamily?,
    quarterToneFilter: QuarterToneFilter,
    onlyFavorites: Boolean,
    favoriteIds: Set<String>,
    playbackState: PlaybackUiState,
    onSearchChange: (String) -> Unit,
    onSelectFamily: (MaqamFamily?) -> Unit,
    onSelectQuarterToneFilter: (QuarterToneFilter) -> Unit,
    onToggleOnlyFavorites: () -> Unit,
    onClearFilters: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPlayPreview: (Maqam) -> Unit,
    onMaqamClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = if (maxWidth >= 680.dp) 2 else 1

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxSize()
                .testTag("encyclopedia_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Hero Banner
            item(span = { GridItemSpan(columns) }) {
                HeroBannerCard(
                    featuredMaqam = featuredMaqam,
                    isPlayingFeatured = playbackState.isPlaying && playbackState.sourceTag == "preview_${featuredMaqam.id}",
                    onPlayFeatured = { onPlayPreview(featuredMaqam) },
                    onOpenFeatured = { onMaqamClick(featuredMaqam.id) }
                )
            }

            // 2. Search Bar & Filters
            item(span = { GridItemSpan(columns) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_maqam_input"),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_hint),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchChange("") },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "مسح البحث"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                    )

                    // Quarter-tone & favorites filter row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = onlyFavorites,
                            onClick = onToggleOnlyFavorites,
                            label = { Text("المفضلة (${favoriteIds.size})") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (onlyFavorites) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.testTag("filter_favorites")
                        )

                        QuarterToneFilter.entries.forEach { qt ->
                            FilterChip(
                                selected = quarterToneFilter == qt,
                                onClick = { onSelectQuarterToneFilter(qt) },
                                label = { Text(qt.label) },
                                modifier = Modifier.testTag("filter_qt_${qt.name.lowercase()}")
                            )
                        }
                    }

                    // 9 Maqam Families Filter Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selectedFamily == null,
                            onClick = { onSelectFamily(null) },
                            label = { Text("كل العائلات (${maqamat.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("family_chip_all")
                        )

                        MaqamFamily.entries.forEach { family ->
                            val selected = selectedFamily == family
                            val familyColor = Color(family.accentHex)
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectFamily(family) },
                                label = { Text(family.arabicName) },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(if (selected) Color.White else familyColor)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = familyColor,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("family_chip_${family.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // 3. Empty State or Maqam Cards
            if (maqamat.isEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    EmptyFilterCard(onClearFilters = onClearFilters)
                }
            } else {
                items(maqamat, key = { it.id }) { maqam ->
                    val isPlayingThis = playbackState.isPlaying &&
                        playbackState.sourceTag == "preview_${maqam.id}"
                    MaqamEncyclopediaCard(
                        maqam = maqam,
                        isFavorite = maqam.id in favoriteIds,
                        isPlaying = isPlayingThis,
                        onToggleFavorite = { onToggleFavorite(maqam.id) },
                        onPlayPreview = { onPlayPreview(maqam) },
                        onClick = { onMaqamClick(maqam.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCard(
    featuredMaqam: Maqam,
    isPlayingFeatured: Boolean,
    onPlayFeatured: () -> Unit,
    onOpenFeatured: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_maqam_hero),
                contentDescription = stringResource(R.string.hero_banner_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC1B120B),
                                Color(0x991B120B),
                                Color(0xEB150E09)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFE2B052).copy(alpha = 0.22f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.border(
                            1.dp,
                            Color(0xFFE2B052).copy(alpha = 0.55f),
                            RoundedCornerShape(50)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = Color(0xFFF6D58E),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "موسوعة المقامات العربية",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFF8E7C2)
                            )
                        }
                    }

                    Text(
                        text = "٩ عائلات • ٢٣ مقاماً",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFD8C7AE)
                    )
                }

                Column {
                    Text(
                        text = "نغمات الشرق، نوتاتها وأصواتها الحية",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFF5E4)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "استكشف المقامات بالمدرج الموسيقي، ربع التون، الأجناس والأمثلة اللحنية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0D1BC),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onPlayFeatured,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE2B052),
                            contentColor = Color(0xFF281900)
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("hero_play_featured")
                    ) {
                        Icon(
                            imageVector = if (isPlayingFeatured) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlayingFeatured) "إيقاف الراست" else "اسمع ${featuredMaqam.name}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenFeatured,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFF3DF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2B052).copy(alpha = 0.65f)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("hero_open_featured")
                    ) {
                        Text(
                            text = "نوتات المقام",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaqamEncyclopediaCard(
    maqam: Maqam,
    isFavorite: Boolean,
    isPlaying: Boolean,
    onToggleFavorite: () -> Unit,
    onPlayPreview: () -> Unit,
    onClick: () -> Unit
) {
    val familyColor = Color(maqam.family.accentHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("maqam_card_${maqam.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isPlaying) 1.8.dp else 1.dp,
            color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = familyColor.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = maqam.family.arabicName,
                                style = MaterialTheme.typography.labelSmall,
                                color = familyColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (maqam.hasQuarterTone) {
                            Surface(
                                color = QuarterToneAccent.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "ربع تون 𝄳",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = QuarterToneAccent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = maqam.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${maqam.englishName}  •  الركوز: ${maqam.tonicName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("fav_btn_${maqam.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_toggle),
                        tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = maqam.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Scale notes preview strip
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = maqam.ascendingScale.joinToString("  •  ") {
                        it.arabicName
                            .replace(" قرار", "")
                            .replace(" جواب", "")
                            .replace(" نصف بيمول", "𝄳")
                            .replace(" بيمول", "♭")
                            .replace(" دييز", "♯")
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    maqam.moodTags.take(2).forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = onPlayPreview,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("play_preview_${maqam.id}")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "إيقاف" else "استمع للسلم",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFilterCard(onClearFilters: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            )
            Text(
                text = "لم يتم العثور على مقامات مطابقة",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "جرّب البحث باسم مقام آخر أو إعادة ضبط الفلاتر لعرض جميع المقامات.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onClearFilters,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("عرض كل المقامات")
            }
        }
    }
}
