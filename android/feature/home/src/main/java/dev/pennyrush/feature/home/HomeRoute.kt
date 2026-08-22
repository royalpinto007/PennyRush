package dev.pennyrush.feature.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.Toast
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import dev.pennyrush.core.designsystem.LocalIsDarkTheme
import dev.pennyrush.core.designsystem.LocalMoneyColors
import dev.pennyrush.core.designsystem.MoneyTextStyle
import dev.pennyrush.core.designsystem.Radius
import dev.pennyrush.core.designsystem.Sizing
import dev.pennyrush.core.designsystem.Space
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dev.pennyrush.core.common.MoneyFormatter
import dev.pennyrush.core.designsystem.AppPreferences
import dev.pennyrush.core.designsystem.ThemeMode
import dev.pennyrush.core.designsystem.ThemePreferences
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Currency
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ─── Design tokens ─────────────────────────────────────────────────────────────

private val CardShape = RoundedCornerShape(Radius.card)
private val TileShape = RoundedCornerShape(Radius.tile)
private val ButtonShape = RoundedCornerShape(Radius.chip)
private val ChipShape = RoundedCornerShape(Radius.chip)
private val InputShape = RoundedCornerShape(Radius.field)
private val ButtonHeight = Sizing.button
private const val PlanningPrefs = "pennyrush_planning"
private const val PlanningGoalsKey = "goals"
private val CurrencyChoices = listOf("INR", "USD", "EUR", "GBP", "AED", "SGD", "AUD", "CAD")

private data class FinancePalette(
    val income: Color,
    val expense: Color,
    val sky: Color,
    val violet: Color,
    val amber: Color,
    val transfer: Color,
    val slate: Color,
    val darkMode: Boolean,
)

/*
 * Kept as a struct so every existing screen picks up the new look without being
 * rewritten, but the values now come from the theme instead of being hardcoded
 * twice. income and expense are the real signals; the rest are a tonal ramp,
 * not a rainbow, so category dots read as one family.
 */
@Composable
private fun financePalette(): FinancePalette {
    val money = LocalMoneyColors.current
    val ramp = money.ramp
    return FinancePalette(
        income = money.income,
        expense = money.expense,
        sky = ramp[2],
        violet = ramp[4],
        amber = money.warning,
        transfer = ramp[5],
        slate = money.neutral,
        darkMode = LocalIsDarkTheme.current,
    )
}

@Composable
private fun appSurface(): Color = MaterialTheme.colorScheme.surface

@Composable
private fun softSurface(): Color = MaterialTheme.colorScheme.surfaceContainer

private fun money(amount: Double, showSign: Boolean = false): String =
    MoneyFormatter.format(
        amount = amount,
        currencyCode = AppPreferences.currencyCode,
        showSign = showSign,
    )

private fun compactMoney(amount: Double): String =
    MoneyFormatter.compact(amount, AppPreferences.currencyCode)

@Composable
private fun Modifier.enterMotion(delayMillis: Int = 0): Modifier {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delayMillis > 0) delay(delayMillis.toLong())
        visible = true
    }
    // Spring rather than a fixed tween: content that settles reads as physical,
    // and staggering by index makes a list arrive instead of blinking on.
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "entryAlpha",
    )
    val y by animateFloatAsState(
        targetValue = if (visible) 0f else 14f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "entryY",
    )
    return graphicsLayer {
        this.alpha = alpha
        translationY = y
    }
}

// ─── Reusable primitives ───────────────────────────────────────────────────────

@Composable
private fun PrCard(
    modifier: Modifier = Modifier,
    padding: Int = 20,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Depth comes from tone, not from an outline and a drop shadow stacked on
    // every card. The old version had both on all 40-odd surfaces, which is
    // what made the app read as a grid of boxes rather than a page.
    val containerColor = if (color == MaterialTheme.colorScheme.surface) {
        if (LocalIsDarkTheme.current) MaterialTheme.colorScheme.surfaceContainer else appSurface()
    } else {
        color
    }
    Surface(
        modifier = modifier,
        shape = CardShape,
        color = containerColor,
        shadowElevation = if (LocalIsDarkTheme.current) 0.dp else 1.dp,
    ) {
        Column(modifier = Modifier.padding(padding.dp), content = content)
    }
}

private fun isValidCurrencyCode(value: String): Boolean {
    val normalized = value.trim().uppercase()
    return normalized.length == 3 && runCatching { Currency.getInstance(normalized) }.isSuccess
}

@Composable
private fun AppFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = softSurface(),
    unfocusedContainerColor = softSurface(),
    disabledContainerColor = softSurface(),
    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.58f),
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
)

@Composable
private fun ScreenTitle(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        subtitle?.let {
            Spacer(Modifier.height(4.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SearchPill(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        singleLine = true,
        shape = InputShape,
        colors = AppFieldColors(),
    )
}

@Composable
private fun PrButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(ButtonHeight),
        shape = ButtonShape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PrSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(ButtonHeight),
        shape = ButtonShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = softSurface(),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun KindChip(kind: TransactionKind) {
    val (label, tint) = kindStyle(kind)
    Surface(
        shape = ChipShape,
        color = tint.copy(alpha = 0.12f),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            color = tint,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
            ),
        )
    }
}

@Composable
private fun kindStyle(kind: TransactionKind): Pair<String, Color> {
    val palette = financePalette()
    return when (kind) {
        TransactionKind.UPI -> "UPI" to palette.violet
        TransactionKind.Card -> "CARD" to palette.sky
        TransactionKind.Transfer -> "TRANSFER" to palette.transfer
        TransactionKind.ATM -> "ATM" to palette.amber
        TransactionKind.Salary -> "SALARY" to palette.income
        TransactionKind.Bill -> "BILL" to palette.expense
        TransactionKind.Cash -> "CASH" to palette.slate
        TransactionKind.Other -> "OTHER" to palette.slate
    }
}

@Composable
private fun accentForKind(kind: TransactionKind): Color = kindStyle(kind).second

// ─── Route ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    userEmail: String? = null,
    userName: String? = null,
    userAvatarUrl: String? = null,
    appVersion: String = "1.0.0",
    sync: TransactionsSync = TransactionsSync(),
    planningSync: PlanningSync = PlanningSync(),
    canUseAppLock: () -> Boolean = { true },
    onDeleteAccount: suspend () -> Unit = {},
    onSignOut: suspend () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val palette = financePalette()
    var selectedDestination by rememberSaveable { mutableStateOf(HomeDestination.Home) }
    var showAddSheet by remember { mutableStateOf(false) }
    var preview by remember { mutableStateOf<StatementPreviewState?>(null) }
    var receiptScan by remember { mutableStateOf<ReceiptScanState?>(null) }
    var pendingReceiptCameraUri by remember { mutableStateOf<Uri?>(null) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var isLoading by remember { mutableStateOf(sync.enabled) }
    var notificationsAllowed by remember { mutableStateOf(BudgetAlertNotifier.canPost(context)) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationsAllowed = granted || BudgetAlertNotifier.canPost(context)
        Toast.makeText(
            context,
            if (notificationsAllowed) "Budget alerts enabled" else "Notifications are off. You can enable them in Android settings.",
            Toast.LENGTH_LONG,
        ).show()
    }

    val requestNotifications: () -> Unit = {
        BudgetAlertNotifier.ensureChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !BudgetAlertNotifier.canPost(context)) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notificationsAllowed = true
            Toast.makeText(context, "Budget alerts are ready", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        BudgetAlertNotifier.ensureChannel(context)
        notificationsAllowed = BudgetAlertNotifier.canPost(context)
    }

    LaunchedEffect(sync) {
        if (!sync.enabled) {
            isLoading = false
            return@LaunchedEffect
        }
        runCatching { sync.loadAll() }
            .onSuccess { TransactionsStore.replaceAll(it) }
            .onFailure {
                Toast.makeText(
                    context,
                    "Couldn't load your activity: ${it.message ?: "unknown error"}",
                    Toast.LENGTH_LONG,
                ).show()
            }
        isLoading = false
    }

    val statementPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = context.displayNameFor(uri)
        preview = StatementPreviewState.Loading(name)
        scope.launch {
            preview = parseStatement(context, uri, name)
        }
    }

    val openImport: () -> Unit = {
        statementPicker.launch(
            arrayOf(
                "text/csv",
                "text/comma-separated-values",
                "application/pdf",
                "text/*",
                "application/*",
            ),
        )
    }

    val receiptImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = context.displayNameFor(uri)
        receiptScan = ReceiptScanState.Loading(name, uri)
        scope.launch {
            receiptScan = scanReceiptImage(context, uri, name)
        }
    }

    val openReceiptImage: () -> Unit = {
        receiptImagePicker.launch(arrayOf("image/*"))
    }

    val receiptCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { captured ->
        val uri = pendingReceiptCameraUri
        if (captured && uri != null) {
            receiptScan = ReceiptScanState.Loading("Camera receipt", uri)
            scope.launch {
                receiptScan = scanReceiptImage(context, uri, "Camera receipt")
                pendingReceiptCameraUri = null
            }
        } else {
            pendingReceiptCameraUri = null
        }
    }

    val openReceiptScan: () -> Unit = {
        val uri = runCatching { context.createReceiptPhotoUri() }.getOrElse {
            Toast.makeText(
                context,
                "Couldn't open camera. Choose an existing image instead.",
                Toast.LENGTH_LONG,
            ).show()
            openReceiptImage()
            null
        }
        if (uri != null) {
            pendingReceiptCameraUri = uri
            runCatching { receiptCamera.launch(uri) }
                .onFailure {
                    pendingReceiptCameraUri = null
                    Toast.makeText(
                        context,
                        "Couldn't open camera. Choose an existing image instead.",
                        Toast.LENGTH_LONG,
                    ).show()
                    openReceiptImage()
                }
        }
    }

    receiptScan?.let { state ->
        ReceiptScanScreen(
            state = state,
            onCancel = { receiptScan = null },
            onRetakePhoto = openReceiptScan,
            onChooseImage = openReceiptImage,
            onSave = { transaction ->
                scope.launch {
                    val saved = if (sync.enabled) {
                        runCatching { sync.persistOne(transaction) }
                            .getOrElse {
                                Toast.makeText(
                                    context,
                                    "Couldn't save scan: ${it.message ?: "unknown error"}",
                                    Toast.LENGTH_LONG,
                                ).show()
                                return@launch
                            }
                    } else {
                        transaction
                    }
                    TransactionsStore.add(saved)
                    BudgetAlertNotifier.notifyLargeTransactions(context, listOf(saved))
                    receiptScan = null
                    selectedDestination = HomeDestination.Transactions
                    Toast.makeText(context, "Receipt saved to Activity", Toast.LENGTH_LONG).show()
                }
            },
        )
        return
    }

    preview?.let { state ->
        StatementPreviewScreen(
            state = state,
            onCancel = { preview = null },
            onImport = { transactions ->
                scope.launch {
                    val existing = TransactionsStore.transactions
                    val seen = existing.mapTo(HashSet()) {
                        "${it.date}|${it.amount}|${it.description.lowercase().trim()}"
                    }
                    val (toInsert, duplicates) = transactions.partition {
                        "${it.date}|${it.amount}|${it.description.lowercase().trim()}" !in seen
                    }
                    val persisted = if (sync.enabled && toInsert.isNotEmpty()) {
                        runCatching { sync.persistBatch(toInsert) }
                            .getOrElse {
                                Toast.makeText(
                                    context,
                                    "Couldn't save import: ${it.message ?: "unknown error"}",
                                    Toast.LENGTH_LONG,
                                ).show()
                                return@launch
                            }
                    } else {
                        toInsert
                    }
                    TransactionsStore.addAll(persisted)
                    BudgetAlertNotifier.notifyLargeTransactions(context, persisted)
                    val msg = buildString {
                        append("Imported ${persisted.size} ")
                        append(if (persisted.size == 1) "entry" else "entries")
                        if (duplicates.isNotEmpty()) {
                            append(" · ${duplicates.size} duplicate")
                            if (duplicates.size != 1) append("s")
                            append(" skipped")
                        }
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    preview = null
                }
            },
        )
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HomeBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it },
            )
        },
    ) { padding ->
        val screenModifier = Modifier
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
        when (selectedDestination) {
            HomeDestination.Home -> HomeContent(
                userEmail = userEmail,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                isSyncing = isLoading,
                onImportStatement = openImport,
                onScanReceipt = openReceiptScan,
                onAddManually = { showAddSheet = true },
                onNavigateToMore = { selectedDestination = HomeDestination.Account },
                onViewPlan = { selectedDestination = HomeDestination.Plan },
                onSearchActivity = { selectedDestination = HomeDestination.Transactions },
                onTransactionSelected = { selectedTransaction = it },
                onSignOut = onSignOut,
                modifier = screenModifier,
            )
            HomeDestination.Transactions -> TransactionsContent(
                onTransactionSelected = { selectedTransaction = it },
                modifier = screenModifier,
            )
            HomeDestination.Plan -> PlanningContent(
                planningSync = planningSync,
                notificationsAllowed = notificationsAllowed,
                onRequestNotifications = requestNotifications,
                modifier = screenModifier,
            )
            HomeDestination.Insights -> InsightsContent(modifier = screenModifier)
            HomeDestination.Account -> AccountContent(
                userEmail = userEmail,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                appVersion = appVersion,
                sync = sync,
                canUseAppLock = canUseAppLock,
                onImportStatement = openImport,
                onScanReceipt = openReceiptScan,
                notificationsAllowed = notificationsAllowed,
                onRequestNotifications = requestNotifications,
                onDeleteAccount = onDeleteAccount,
                onSignOut = onSignOut,
                modifier = screenModifier,
            )
        }
    }

    if (showAddSheet) {
        QuickAddSheet(
            onScanReceipt = {
                showAddSheet = false
                openReceiptScan()
            },
            onImportStatement = {
                showAddSheet = false
                openImport()
            },
            onSave = { transaction ->
                scope.launch {
                    val saved = if (sync.enabled) {
                        runCatching { sync.persistOne(transaction) }
                            .getOrElse {
                                Toast.makeText(
                                    context,
                                    "Couldn't save: ${it.message ?: "unknown error"}",
                                    Toast.LENGTH_LONG,
                                ).show()
                                return@launch
                            }
                    } else {
                        transaction
                    }
                    TransactionsStore.add(saved)
                    BudgetAlertNotifier.notifyLargeTransactions(context, listOf(saved))
                    showAddSheet = false
                }
            },
            onDismiss = { showAddSheet = false },
        )
    }

    selectedTransaction?.let { transaction ->
        TransactionDetailSheet(
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onSave = { updated ->
                scope.launch {
                    val saved = if (sync.enabled) {
                        runCatching { sync.updateOne(updated) }
                            .getOrElse {
                                Toast.makeText(
                                    context,
                                    "Couldn't update: ${it.message ?: "unknown error"}",
                                    Toast.LENGTH_LONG,
                                ).show()
                                return@launch
                            }
                    } else {
                        updated
                    }
                    TransactionsStore.update(saved)
                    BudgetAlertNotifier.notifyLargeTransactions(context, listOf(saved))
                    selectedTransaction = null
                }
            },
            onDelete = { id ->
                scope.launch {
                    if (sync.enabled) {
                        runCatching { sync.deleteOne(id) }
                            .getOrElse {
                                Toast.makeText(
                                    context,
                                    "Couldn't delete: ${it.message ?: "unknown error"}",
                                    Toast.LENGTH_LONG,
                                ).show()
                                return@launch
                            }
                    }
                    TransactionsStore.delete(id)
                    selectedTransaction = null
                }
            },
        )
    }
}

// ─── Home tab ──────────────────────────────────────────────────────────────────

@Composable
private fun HomeContent(
    userEmail: String?,
    userName: String?,
    userAvatarUrl: String?,
    isSyncing: Boolean,
    onImportStatement: () -> Unit,
    onScanReceipt: () -> Unit,
    onAddManually: () -> Unit,
    onNavigateToMore: () -> Unit,
    onViewPlan: () -> Unit,
    onSearchActivity: () -> Unit,
    onTransactionSelected: (Transaction) -> Unit,
    onSignOut: suspend () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transactions = TransactionsStore.transactions
    val isEmpty = transactions.isEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Space.lg),
        verticalArrangement = Arrangement.spacedBy(Space.lg),
        // Scaffold reserves room for the bottom bar but not for a floating
        // action button, so without this the FAB covers the last row's amount.
        // Clears the floating bottom bar so the last row is never under it.
        contentPadding = PaddingValues(bottom = Space.xxl),
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            HomeSearchHeader(
                userEmail = userEmail,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                onNavigateToMore = onNavigateToMore,
                onSearchActivity = onSearchActivity,
                onSignOut = onSignOut,
                modifier = Modifier.enterMotion(),
            )
        }
        if (isSyncing) {
            item {
                SyncStatusCard(modifier = Modifier.enterMotion(40))
            }
        }
        if (isEmpty) {
            item {
                if (isSyncing) {
                    HomeLoadingCard(modifier = Modifier.enterMotion(70))
                } else {
                    EmptyHomeCard(
                        onImport = onImportStatement,
                        onScan = onScanReceipt,
                        onAddManually = onAddManually,
                        modifier = Modifier.enterMotion(70),
                    )
                }
            }
        } else {
            item { WalletHero(transactions, modifier = Modifier.enterMotion(70)) }
            item {
                QuickActions(
                    onAdd = onAddManually,
                    onScan = onScanReceipt,
                    onImport = onImportStatement,
                    onPlan = onViewPlan,
                    modifier = Modifier.enterMotion(130),
                )
            }
            item {
                TopMerchantsStrip(
                    transactions = transactions,
                    onOpenActivity = onSearchActivity,
                    modifier = Modifier.enterMotion(180),
                )
            }
            item {
                RecentActivityCard(
                    transactions = transactions,
                    onViewAll = onSearchActivity,
                    onTransactionSelected = onTransactionSelected,
                    modifier = Modifier.enterMotion(230),
                )
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

private fun bucketLabel(date: LocalDate, today: LocalDate): String = when {
    date == today -> "Today"
    date == today.minusDays(1) -> "Yesterday"
    date.isAfter(today.minusDays(7)) -> "This week"
    date.isAfter(today.minusDays(30)) -> "This month"
    else -> date.format(DateTimeFormatter.ofPattern("MMM yyyy"))
}

@Composable
private fun HomeSearchHeader(
    userEmail: String?,
    userName: String?,
    userAvatarUrl: String?,
    onNavigateToMore: () -> Unit,
    onSearchActivity: () -> Unit,
    onSignOut: suspend () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            color = softSurface(),
            onClick = onSearchActivity,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // The signed-in name used to sit at the right-hand end of this
                // field, which read as text somebody had typed into the search
                // box. The avatar beside it already says whose account this is.
                Text(
                    text = "Search activity",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                )
            }
        }
        ProfileAvatar(
            userEmail = userEmail,
            userName = userName,
            userAvatarUrl = userAvatarUrl,
            onNavigateToMore = onNavigateToMore,
            onSignOut = onSignOut,
        )
    }
}

@Composable
private fun SyncStatusCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = softSurface(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Updating your money data",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "Your latest activity, plans, and goals are loading.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileAvatar(
    userEmail: String?,
    userName: String?,
    userAvatarUrl: String?,
    onNavigateToMore: () -> Unit,
    onSignOut: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var sheetOpen by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .semantics {
                contentDescription = "Open profile and settings"
                role = Role.Button
            },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = { sheetOpen = true },
    ) {
        if (!userAvatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = userAvatarUrl,
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape),
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initialsFor(userName, userEmail),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (sheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { sheetOpen = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        if (!userAvatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = userAvatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp).clip(CircleShape),
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initialsFor(userName, userEmail),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName ?: "Signed in",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                        userEmail?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }

                SettingsGroup {
                    SettingsRow(
                        icon = Icons.Rounded.Settings,
                        title = "Settings",
                        subtitle = "Currency, theme, notifications",
                        onClick = {
                            sheetOpen = false
                            onNavigateToMore()
                        },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        title = "Sign out",
                        subtitle = userEmail,
                        onClick = {
                            sheetOpen = false
                            scope.launch { onSignOut() }
                        },
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EmptyHomeCard(
    onImport: () -> Unit,
    onScan: () -> Unit,
    onAddManually: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(palette.income.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = null,
                tint = palette.income,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Capture your first spend",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Open the camera, review the detected amount, and save it. No dashboard noise before you have real activity.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(18.dp))
        OnboardingStep("1", "Scan a receipt")
        Spacer(Modifier.height(8.dp))
        OnboardingStep("2", "Review merchant and amount")
        Spacer(Modifier.height(8.dp))
        OnboardingStep("3", "Save to Activity")
        Spacer(Modifier.height(22.dp))
        PrButton("Scan receipt", onClick = onScan, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PrSecondaryButton("Add", onClick = onAddManually, modifier = Modifier.weight(1f))
            PrSecondaryButton("Import", onClick = onImport, modifier = Modifier.weight(1f))
        }
        }
    }
}

@Composable
private fun HomeLoadingCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Bringing your wallet up to date",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Your dashboard will appear as soon as your latest activity is ready.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun OnboardingStep(number: String, label: String) {
    Surface(
        shape = ButtonShape,
        color = softSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                modifier = Modifier.size(26.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun WalletHero(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    accountName: String = "Primary balance",
) {
    val palette = financePalette()
    val net = transactions.sumOf { it.amount }
    val now = YearMonth.now()
    val thisMonth = transactions.filter { YearMonth.from(it.date) == now }
    val income = thisMonth.filter { it.amount > 0 }.sumOf { it.amount }
    val expenses = thisMonth.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    val delta = income - expenses
    val positive = delta >= 0
    val savingsRate = if (income > 0) (delta / income).coerceIn(0.0, 1.0) else 0.0
    val savingsProgress by animateFloatAsState(
        targetValue = savingsRate.toFloat().coerceIn(0.02f, 1f),
        animationSpec = tween(durationMillis = 760, easing = FastOutSlowInEasing),
        label = "savingsProgress",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
        shadowElevation = if (palette.darkMode) 0.dp else 2.dp,
    ) {
        Column {
            // The old hero opened with a 5dp green stripe across the top. It
            // dated the whole screen and said nothing the balance below does
            // not already say.
            Column(
                modifier = Modifier.padding(Space.xl),
                verticalArrangement = Arrangement.spacedBy(Space.lg),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = accountName.uppercase(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Surface(
                        shape = ChipShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            // Was hardcoded to INR, so it lied for anyone who
                            // changed the currency in settings.
                            text = AppPreferences.currencyCode,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp,
                            ),
                        )
                    }
                }

                Text(
                    text = money(net),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayMedium.merge(MoneyTextStyle).merge(MoneyTextStyle),
                    maxLines = 1,
                )

                if (thisMonth.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassStat(
                            label = "Month net",
                            value = money(delta, showSign = true),
                            tint = if (positive) palette.income else palette.expense,
                            modifier = Modifier.weight(1f),
                        )
                        GlassStat(
                            label = "Spent",
                            value = money(expenses),
                            tint = palette.expense,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (income > 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Saved this month",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                                Text(
                                    text = "${(savingsRate * 100).toInt()}%",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(
                                        softSurface(),
                                        RoundedCornerShape(999.dp),
                                    ),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(savingsProgress)
                                        .height(6.dp)
                                        .background(palette.income, RoundedCornerShape(999.dp)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroMetric(
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = tint.copy(alpha = 0.1f),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = value,
                color = tint,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun QuickActions(
    onAdd: () -> Unit,
    onScan: () -> Unit,
    onImport: () -> Unit,
    onPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickActionTile("Add", Icons.Rounded.Add, palette.income, Modifier.weight(1f), onAdd)
        QuickActionTile("Scan", Icons.Rounded.CameraAlt, palette.sky, Modifier.weight(1f), onScan)
        QuickActionTile("Import", Icons.Rounded.FileDownload, palette.amber, Modifier.weight(1f), onImport)
        QuickActionTile("Plan", Icons.Rounded.BarChart, palette.violet, Modifier.weight(1f), onPlan)
    }
}

@Composable
private fun QuickActionTile(
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing),
        label = "quickActionPress",
    )
    Surface(
        modifier = modifier
            .heightIn(min = 84.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(TileShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(vertical = 2.dp),
        shape = TileShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Space.sm, vertical = Space.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Space.sm),
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(Sizing.icon),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

/**
 * Where this month's spending actually went.
 *
 * This strip used to list the most recent distinct merchants as initials in
 * circles, directly above a list of the most recent transactions. It was the
 * same data twice, and the top copy carried less of it: no amounts, no dates,
 * and a tap target that went to the same place as the list below. It occupied
 * the best strip on the screen and told you nothing.
 *
 * Ranking by what was spent this month is information the list underneath
 * cannot give, because the list is ordered by time. The two now answer
 * different questions: what happened last, and what is costing you most.
 */
@Composable
private fun TopMerchantsStrip(
    transactions: List<Transaction>,
    onOpenActivity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    // Ranked over the most recent month that actually has spending, not
    // strictly the current one. Somebody who imports a statement on the 2nd of
    // the month would otherwise open the app to a blank strip where the
    // headline figure should be, which reads as the app having lost their data.
    val spending = remember(transactions) { transactions.filter { it.amount < 0 } }
    val month = remember(spending) { spending.maxOfOrNull { YearMonth.from(it.date) } }
    val top = remember(spending, month) {
        if (month == null) {
            emptyList()
        } else {
            spending
                .filter { YearMonth.from(it.date) == month }
                .groupBy { it.merchant.trim().ifBlank { "Unknown" } }
                .map { (merchant, rows) -> merchant to rows.sumOf { abs(it.amount) } }
                .sortedByDescending { it.second }
                .take(8)
        }
    }
    // A ranking of one merchant is not a ranking, and the activity list below
    // already says everything there is to say about a month with two entries.
    if (month == null || top.size < 3) return
    val heading = if (month == YearMonth.now()) {
        "Top this month"
    } else {
        // Fully qualified: Compose has a TextStyle of its own and it is imported here.
        "Top in ${month.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())}"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = heading,
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(onClick = onOpenActivity) {
                Text("See all", fontWeight = FontWeight.SemiBold)
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(top, key = { it.first }) { (merchant, spent) ->
                TopMerchantChip(
                    merchant = merchant,
                    spent = spent,
                    tint = palette.expense,
                    onClick = onOpenActivity,
                )
            }
        }
    }
}

@Composable
private fun TopMerchantChip(
    merchant: String,
    spent: Double,
    tint: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(84.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = tint.copy(alpha = 0.12f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = merchant.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString() ?: "?",
                    color = tint,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Text(
            text = merchant,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        // The amount is the reason this strip exists, so it is never dropped.
        Text(
            text = money(spent),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium.merge(MoneyTextStyle),
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RecentActivityCard(
    transactions: List<Transaction>,
    onViewAll: () -> Unit,
    onTransactionSelected: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val recent = transactions.sortedByDescending { it.date }.take(4)
    if (recent.isEmpty()) return

    PrCard(modifier = modifier, padding = 18) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Latest activity",
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(onClick = onViewAll) {
                Text("See all", fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(4.dp))
        recent.forEachIndexed { index, transaction ->
            CompactActivityRow(
                transaction = transaction,
                onClick = { onTransactionSelected(transaction) },
            )
            if (index != recent.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                )
            }
        }
    }
}

@Composable
private fun CompactActivityRow(
    transaction: Transaction,
    onClick: () -> Unit,
) {
    val palette = financePalette()
    val accent = accentForKind(transaction.kind)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = accent.copy(alpha = 0.12f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = transaction.merchant.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString() ?: "?",
                    color = accent,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f).padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = transaction.merchant,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${CategorizationRules.categoryNameFor(transaction)} · ${transaction.date.format(DateTimeFormatter.ofPattern("MMM d"))}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = money(transaction.amount, showSign = true),
            color = if (transaction.amount >= 0) palette.income else palette.expense,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold).merge(MoneyTextStyle),
            maxLines = 1,
        )
    }
}

@Composable
private fun StatsStrip(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    val now = YearMonth.now()
    val thisMonth = transactions.filter { YearMonth.from(it.date) == now }
    val income = thisMonth.filter { it.amount > 0 }.sumOf { it.amount }
    val expenses = thisMonth.filter { it.amount < 0 }.sumOf { abs(it.amount) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            label = "Income",
            amount = income,
            accent = palette.income,
            icon = Icons.Rounded.ArrowDownward,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = "Spend",
            amount = expenses,
            accent = palette.expense,
            icon = Icons.Rounded.ArrowUpward,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    amount: Double,
    accent: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CardShape,
        color = appSurface(),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.18f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = accent,
                        )
                    }
                }
                Text(
                    text = label.uppercase(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            Text(
                text = compactMoney(amount),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                ).merge(MoneyTextStyle),
                maxLines = 2,
            )
            Text(
                text = "this month",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun SpendingBreakdown(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    val byKind = transactions
        .filter { it.amount < 0 }
        .groupBy { it.kind }
        .map { (kind, txns) -> kind to txns.sumOf { abs(it.amount) } }
        .sortedByDescending { it.second }
        .take(6)
    val total = byKind.sumOf { it.second }
    if (byKind.isEmpty() || total == 0.0) return
    val kindAccents = byKind.associate { (kind, _) -> kind to accentForKind(kind) }
    val fallbackAccent = MaterialTheme.colorScheme.primary

    PrCard(modifier = modifier, padding = 20) {
        Text(
            text = "Where your money went",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            ),
        )
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(132.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(132.dp)) {
                    val strokeWidth = 22.dp.toPx()
                    val gap = 2f
                    var startAngle = -90f
                    byKind.forEach { (kind, amount) ->
                        val sweep = ((amount / total) * 360.0).toFloat() - gap
                        drawArc(
                            color = kindAccents[kind] ?: fallbackAccent,
                            startAngle = startAngle,
                            sweepAngle = sweep.coerceAtLeast(0f),
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        )
                        startAngle += sweep + gap
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "SPENT",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Text(
                        compactMoney(total),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp,
                        ),
                    )
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                byKind.forEach { (kind, amount) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(kindAccents[kind] ?: fallbackAccent, CircleShape),
                        )
                        Text(
                            text = kindStyle(kind).first
                                .lowercase()
                                .replaceFirstChar { it.uppercase() },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = compactMoney(amount),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ).merge(MoneyTextStyle),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    onClick: (() -> Unit)? = null,
) {
    val palette = financePalette()
    val accent = accentForKind(transaction.kind)
    val rowModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 78.dp)
        .then(
            if (onClick != null) {
                Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .clickable(role = Role.Button, onClick = onClick)
            } else {
                Modifier
            },
        )
    Surface(
        modifier = rowModifier,
        shape = RoundedCornerShape(22.dp),
        color = appSurface(),
        shadowElevation = if (palette.darkMode) 0.dp else 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(accent.copy(alpha = 0.13f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = transaction.merchant.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString() ?: "·",
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = transaction.merchant,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    KindChip(transaction.kind)
                    Text(
                        text = "${CategorizationRules.categoryNameFor(transaction)} · ${transaction.date.format(DateTimeFormatter.ofPattern("MMM d"))}",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                    )
                }
            }
            Text(
                text = money(transaction.amount, showSign = true),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                ).merge(MoneyTextStyle),
                color = if (transaction.amount > 0) palette.income else palette.expense,
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    // Caps and tracking, used only here. A section header that looks like body
    // text makes a long scroll read as one undifferentiated column.
    Text(
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelSmall,
    )
}

// ─── Other tabs ────────────────────────────────────────────────────────────────

@Composable
private fun HomeBottomBar(
    selectedDestination: HomeDestination,
    onDestinationSelected: (HomeDestination) -> Unit,
) {
    /*
     * A floating pill rather than a full-width bar welded to the bottom edge.
     * The selected tab gets a filled capsule that carries its label; the rest
     * are icon-only. That is the current Material direction and it also means
     * the bar stops competing with the content above it.
     */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Space.lg, vertical = Space.md),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(Radius.chip),
            color = if (LocalIsDarkTheme.current) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surface
            },
            shadowElevation = if (LocalIsDarkTheme.current) 0.dp else 6.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HomeDestination.entries.forEach { destination ->
                    val selected = destination == selectedDestination
                    val containerColor by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        animationSpec = tween(220),
                        label = "tabContainer",
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        animationSpec = tween(220),
                        label = "tabContent",
                    )
                    Surface(
                        shape = RoundedCornerShape(Radius.chip),
                        color = containerColor,
                        modifier = Modifier
                            .heightIn(min = Sizing.touchTarget)
                            .clip(RoundedCornerShape(Radius.chip))
                            .clickable(role = Role.Tab) { onDestinationSelected(destination) },
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = if (selected) 16.dp else 14.dp,
                                vertical = 12.dp,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Space.sm),
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                tint = contentColor,
                                modifier = Modifier.size(Sizing.icon),
                            )
                            // Only the active tab is labelled. Five permanent
                            // labels at 11sp is noise nobody reads after day one.
                            AnimatedVisibility(visible = selected) {
                                Text(
                                    text = destination.label,
                                    color = contentColor,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionsContent(
    onTransactionSelected: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val transactions = TransactionsStore.transactions
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(TransactionFilter.All) }
    val filtered = transactions
        .filter { transaction ->
            val matchesQuery = query.isBlank() ||
                transaction.merchant.contains(query, ignoreCase = true) ||
                transaction.description.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                TransactionFilter.All -> true
                TransactionFilter.Income -> transaction.amount > 0
                TransactionFilter.Expenses -> transaction.amount < 0
            }
            matchesQuery && matchesFilter
        }
        .sortedByDescending { it.date }
    val today = LocalDate.now()
    val grouped = filtered.groupBy { bucketLabel(it.date, today) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            ScreenTitle(
                title = "Activity",
                subtitle = "Search and review every money movement",
                modifier = Modifier.enterMotion(),
            )
        }
        item {
            SearchPill(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Search activity",
                modifier = Modifier.enterMotion(50),
            )
        }
        item {
            Row(
                modifier = Modifier.enterMotion(90),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TransactionFilter.entries.forEach { option ->
                    FilterPill(
                        text = option.label,
                        selected = option == filter,
                        modifier = Modifier.weight(1f),
                        onClick = { filter = option },
                    )
                }
            }
        }
        item {
            TransactionsSummaryCard(transactions, modifier = Modifier.enterMotion(130))
        }
        if (transactions.isEmpty()) {
            item {
                EmptyActivityCard(
                    "No activity yet",
                    "Scan a receipt, add an entry, or import a bank statement.",
                    modifier = Modifier.enterMotion(170),
                )
            }
        } else if (filtered.isEmpty()) {
            item {
                EmptyActivityCard(
                    "No matches",
                    "Try a different search or switch filters.",
                    modifier = Modifier.enterMotion(170),
                )
            }
        } else {
            grouped.forEach { (label, rows) ->
                item {
                    SectionLabel(label)
                }
                items(rows) { transaction ->
                    TransactionRow(transaction, onClick = { onTransactionSelected(transaction) })
                }
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

private enum class TransactionFilter(val label: String) {
    All("All"),
    Income("Income"),
    Expenses("Spend"),
}

// ─── Plan tab ─────────────────────────────────────────────────────────────────

@Composable
private fun PlanningContent(
    planningSync: PlanningSync,
    notificationsAllowed: Boolean,
    onRequestNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val transactions = TransactionsStore.transactions
    var budgetLimits by remember { mutableStateOf(context.loadBudgetLimits()) }
    var goals by remember { mutableStateOf(context.loadGoals()) }
    var editingBudget by remember { mutableStateOf<BudgetProgress?>(null) }
    var editingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalDialogOpen by remember { mutableStateOf(false) }
    var syncState by remember { mutableStateOf(if (planningSync.enabled) "Updating plan..." else "Saved here") }
    val budgetRows = buildBudgetProgress(transactions, budgetLimits)
    val recurring = detectRecurringMerchants(transactions)

    LaunchedEffect(planningSync) {
        if (!planningSync.enabled) return@LaunchedEffect
        val remoteBudgets = runCatching { planningSync.loadBudgets() }
        val remoteGoals = runCatching { planningSync.loadGoals() }
        remoteBudgets.onSuccess { rows ->
            if (rows.isNotEmpty()) {
                rows.forEach { context.saveBudgetLimit(it.category, it.limit) }
                budgetLimits = context.loadBudgetLimits()
            }
        }
        remoteGoals.onSuccess { rows ->
            context.saveGoals(rows)
            goals = rows
        }
        syncState = if (remoteBudgets.isSuccess && remoteGoals.isSuccess) {
            "Up to date"
        } else {
            "Changes saved here"
        }
    }

    LaunchedEffect(budgetRows, notificationsAllowed, AppPreferences.budgetAlerts) {
        BudgetAlertNotifier.notifyBudgetThresholds(context, budgetRows)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            ScreenTitle(
                title = "Plan",
                subtitle = syncState,
                modifier = Modifier.enterMotion(),
            )
        }
        if (!notificationsAllowed && (AppPreferences.budgetAlerts || AppPreferences.largeTransactions)) {
            item {
                NotificationPermissionCard(
                    onRequestNotifications = onRequestNotifications,
                    modifier = Modifier.enterMotion(50),
                )
            }
        }
        if (transactions.isEmpty()) {
            item {
                EmptyActivityCard(
                    "No plan yet",
                    "Import or scan activity first. PennyRush will turn it into budgets and recurring spend automatically.",
                    modifier = Modifier.enterMotion(70),
                )
            }
        } else {
            item {
                PlanHero(budgetRows, modifier = Modifier.enterMotion(70))
            }
            item {
                PlanHealthStrip(
                    budgetRows = budgetRows,
                    goalCount = goals.size,
                    recurringCount = recurring.size,
                    modifier = Modifier.enterMotion(105),
                )
            }
            item {
                BudgetOverviewCard(
                    rows = budgetRows,
                    onEdit = { editingBudget = it },
                    modifier = Modifier.enterMotion(145),
                )
            }
            item {
                SavingsGoalsCard(
                    transactions = transactions,
                    goals = goals,
                    onAddGoal = {
                        editingGoal = null
                        goalDialogOpen = true
                    },
                    onEditGoal = {
                        editingGoal = it
                        goalDialogOpen = true
                    },
                    modifier = Modifier.enterMotion(190),
                )
            }
            item {
                RecurringSpendCard(recurring, modifier = Modifier.enterMotion(235))
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    editingBudget?.let { budget ->
        BudgetLimitDialog(
            budget = budget,
            onDismiss = { editingBudget = null },
            onSave = { category, limit ->
                context.saveBudgetLimit(category, limit)
                val nextLimits = context.loadBudgetLimits()
                budgetLimits = nextLimits
                editingBudget = null
                BudgetAlertNotifier.notifyBudgetThresholds(context, buildBudgetProgress(transactions, nextLimits))
                if (planningSync.enabled) {
                    scope.launch {
                        runCatching { planningSync.saveBudget(BudgetLimit(category, limit)) }
                            .onSuccess { syncState = "Budget saved" }
                            .onFailure { syncState = "Budget saved here" }
                    }
                }
            },
        )
    }

    if (goalDialogOpen) {
        GoalEditorDialog(
            goal = editingGoal,
            onDismiss = { goalDialogOpen = false },
            onDelete = { goal ->
                val next = goals.filterNot { it.id == goal.id }
                context.saveGoals(next)
                goals = next
                goalDialogOpen = false
                syncState = if (planningSync.enabled) "Removing goal..." else "Goal removed"
                if (planningSync.enabled) {
                    scope.launch {
                        runCatching { planningSync.deleteGoal(goal.id) }
                            .onSuccess { syncState = "Goal removed" }
                            .onFailure { syncState = "Goal removed here" }
                    }
                }
            },
            onSave = { goal ->
                val next = (goals.filterNot { it.id == goal.id } + goal)
                    .sortedBy { it.targetDate ?: LocalDate.MAX }
                context.saveGoals(next)
                goals = next
                goalDialogOpen = false
                if (planningSync.enabled) {
                    scope.launch {
                        runCatching { planningSync.saveGoal(goal) }
                            .onSuccess { saved ->
                                val savedGoals = (goals.filterNot { it.id == saved.id } + saved)
                                    .sortedBy { it.targetDate ?: LocalDate.MAX }
                                context.saveGoals(savedGoals)
                                goals = savedGoals
                                syncState = "Goal saved"
                            }
                            .onFailure { syncState = "Goal saved here" }
                    }
                }
            },
        )
    }
}

@Composable
private fun PlanHero(
    rows: List<BudgetProgress>,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    val spent = rows.sumOf { it.spent }
    val limit = rows.sumOf { it.limit }.coerceAtLeast(1.0)
    val remaining = limit - spent
    val overPlan = remaining < 0
    val progress by animateFloatAsState(
        targetValue = (spent / limit).toFloat().coerceIn(0.04f, 1f),
        animationSpec = tween(durationMillis = 780, easing = FastOutSlowInEasing),
        label = "planProgress",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
        shadowElevation = if (palette.darkMode) 0.dp else 2.dp,
        tonalElevation = if (palette.darkMode) 2.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "MONTHLY PLAN",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                    ),
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (overPlan) palette.expense.copy(alpha = 0.12f) else palette.income.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, if (overPlan) palette.expense.copy(alpha = 0.18f) else palette.income.copy(alpha = 0.18f)),
                ) {
                    Text(
                        text = if (overPlan) "Over" else "On track",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = if (overPlan) palette.expense else palette.income,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
            Text(
                text = if (overPlan) money(abs(remaining)) else money(remaining),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                ),
                maxLines = 2,
            )
            Text(
                text = if (overPlan) "over your monthly plan" else "left in your monthly plan",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(softSurface(), RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .background(
                            if (overPlan) palette.expense else Color.White,
                            RoundedCornerShape(999.dp),
                        ),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassStat("Spent", compactMoney(spent), if (overPlan) palette.expense else MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                GlassStat("Budget", compactMoney(limit), MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NotificationPermissionCard(
    onRequestNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = palette.amber.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, palette.amber.copy(alpha = 0.34f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = palette.amber.copy(alpha = 0.2f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = palette.amber,
                        modifier = Modifier.size(21.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Turn on alerts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "Get budget and big-spend nudges at the right moment.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            TextButton(onClick = onRequestNotifications) {
                Text("Allow", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PlanHealthStrip(
    budgetRows: List<BudgetProgress>,
    goalCount: Int,
    recurringCount: Int,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    val overBudget = budgetRows.count { it.spent > it.limit && it.limit > 0.0 }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PlanHealthTile(
            label = "Watch",
            value = overBudget.toString(),
            tint = if (overBudget > 0) palette.expense else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        PlanHealthTile(
            label = "Goals",
            value = goalCount.toString(),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        PlanHealthTile(
            label = "Recurring",
            value = recurringCount.toString(),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PlanHealthTile(
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = 84.dp),
        shape = TileShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(Space.lg),
            verticalArrangement = Arrangement.spacedBy(Space.xs),
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
            Text(
                text = value,
                color = tint,
                style = MaterialTheme.typography.headlineSmall.merge(MoneyTextStyle),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun BudgetOverviewCard(
    rows: List<BudgetProgress>,
    onEdit: (BudgetProgress) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrCard(modifier = modifier) {
        Text(
            text = "Monthly budgets",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            ),
        )
        Text(
            text = "Tap any category to adjust its limit.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(14.dp))
        rows.take(8).forEachIndexed { index, row ->
            BudgetProgressRow(
                row = row,
                modifier = Modifier.padding(top = if (index == 0) 0.dp else 12.dp),
                onClick = { onEdit(row) },
            )
        }
    }
}

@Composable
private fun BudgetProgressRow(
    row: BudgetProgress,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = financePalette()
    val progressTarget = (row.spent / row.limit.coerceAtLeast(1.0)).toFloat().coerceIn(0.04f, 1f)
    val progress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 620, easing = FastOutSlowInEasing),
        label = "budget${row.category}",
    )
    val over = row.spent > row.limit
    val tint = if (over) palette.expense else accentForCategory(row.category)
    val remaining = row.limit - row.spent

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = softSurface(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(tint, CircleShape),
                )
                Text(
                    text = row.category,
                    modifier = Modifier.weight(1f).padding(start = 10.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                )
                Text(
                    text = "${compactMoney(row.spent)} / ${compactMoney(row.limit)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .background(appSurface(), RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(7.dp)
                        .background(tint, RoundedCornerShape(999.dp)),
                )
            }
            Text(
                text = if (over) {
                    "${compactMoney(abs(remaining))} over"
                } else {
                    "${compactMoney(remaining)} left"
                },
                color = if (over) palette.expense else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun SavingsGoalsCard(
    transactions: List<Transaction>,
    goals: List<SavingsGoal>,
    onAddGoal: () -> Unit,
    onEditGoal: (SavingsGoal) -> Unit,
    modifier: Modifier = Modifier,
) {
    val monthRows = transactions.filter { YearMonth.from(it.date) == YearMonth.now() }
    val income = monthRows.filter { it.amount > 0 }.sumOf { it.amount }
    val spend = monthRows.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    val surplus = income - spend
    val bufferTarget = maxOf(spend * 3.0, 75000.0)
    val cushionTarget = maxOf(spend, 25000.0)
    val bufferMonths = if (surplus > 0.0) kotlin.math.ceil(bufferTarget / surplus).toInt() else null

    PrCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Goals",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                ),
            )
            TextButton(onClick = onAddGoal) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(14.dp))
        if (goals.isEmpty()) {
            Text(
                text = "Create goals for trips, emergency funds, or big purchases. PennyRush keeps them ready across your account.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(14.dp))
        } else {
            goals.forEachIndexed { index, goal ->
                SavedGoalRow(
                    goal = goal,
                    onClick = { onEditGoal(goal) },
                )
                if (index != goals.lastIndex) Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(16.dp))
        }
        Text(
            text = "Suggestions",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(10.dp))
        GoalRow(
            title = "Emergency buffer",
            body = bufferMonths?.let { "At this pace, you can fund ${compactMoney(bufferTarget)} in $it month${if (it == 1) "" else "s"}." }
                ?: "Create a monthly surplus to start building a ${compactMoney(bufferTarget)} buffer.",
            progress = if (surplus > 0) (surplus / bufferTarget).coerceIn(0.04, 1.0).toFloat() else 0.04f,
        )
        Spacer(Modifier.height(12.dp))
        GoalRow(
            title = "Next-month cushion",
            body = "A ${compactMoney(cushionTarget)} cushion covers one average month of spend.",
            progress = if (surplus > 0) (surplus / cushionTarget).coerceIn(0.04, 1.0).toFloat() else 0.04f,
        )
    }
}

@Composable
private fun SavedGoalRow(
    goal: SavingsGoal,
    onClick: () -> Unit,
) {
    val palette = financePalette()
    val progress = if (goal.targetAmount > 0) {
        (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0.04f, 1f)
    } else {
        0.04f
    }
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing),
        label = "savedGoal${goal.id}",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = softSurface(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                    )
                    goal.targetDate?.let {
                        Text(
                            text = "Target ${it.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
                Text(
                    text = "${compactMoney(goal.currentAmount)} / ${compactMoney(goal.targetAmount)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .background(appSurface(), RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animated)
                        .height(7.dp)
                        .background(palette.income, RoundedCornerShape(999.dp)),
                )
            }
        }
    }
}

@Composable
private fun GoalRow(
    title: String,
    body: String,
    progress: Float,
) {
    val palette = financePalette()
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0.04f, 1f),
        animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing),
        label = "goal$title",
    )
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Text(
            text = body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .background(softSurface(), RoundedCornerShape(999.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .height(7.dp)
                    .background(palette.income, RoundedCornerShape(999.dp)),
            )
        }
    }
}

@Composable
private fun RecurringSpendCard(
    rows: List<RecurringMerchant>,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    PrCard(modifier = modifier) {
        Text(
            text = "Recurring spend",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        if (rows.isEmpty()) {
            Text(
                text = "No recurring merchants detected yet. Repeated subscriptions and bills will appear here automatically.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = palette.violet.copy(alpha = 0.16f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = row.merchant.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString() ?: "#",
                                color = palette.violet,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f).padding(start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = row.merchant,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 2,
                        )
                        Text(
                            text = "${row.count} payments · last ${row.lastSeen.format(DateTimeFormatter.ofPattern("MMM d"))}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    Text(
                        text = compactMoney(row.averageAmount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold).merge(MoneyTextStyle),
                    )
                }
                if (index != rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                }
            }
        }
    }
}

@Composable
private fun BudgetLimitDialog(
    budget: BudgetProgress,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit,
) {
    var amount by remember(budget.category) { mutableStateOf("%.0f".format(budget.limit)) }
    val parsed = amount.toDoubleOrNull()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = parsed != null && parsed > 0.0,
                onClick = { parsed?.let { onSave(budget.category, it) } },
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Edit ${budget.category} budget", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Monthly limit (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = InputShape,
                    colors = AppFieldColors(),
                )
                Text(
                    text = "Current spend: ${money(budget.spent)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        shape = CardShape,
    )
}

@Composable
private fun GoalEditorDialog(
    goal: SavingsGoal?,
    onDismiss: () -> Unit,
    onDelete: (SavingsGoal) -> Unit,
    onSave: (SavingsGoal) -> Unit,
) {
    var name by remember(goal?.id) { mutableStateOf(goal?.name.orEmpty()) }
    var target by remember(goal?.id) { mutableStateOf(goal?.targetAmount?.let { "%.0f".format(it) }.orEmpty()) }
    var current by remember(goal?.id) { mutableStateOf(goal?.currentAmount?.let { "%.0f".format(it) } ?: "0") }
    var targetDate by remember(goal?.id) { mutableStateOf(goal?.targetDate?.toString().orEmpty()) }
    val parsedTarget = target.toDoubleOrNull()
    val parsedCurrent = current.toDoubleOrNull() ?: 0.0
    val parsedDate = targetDate.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    val dateValid = targetDate.isBlank() || parsedDate != null
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && parsedTarget != null && parsedTarget > 0.0 && parsedCurrent >= 0.0 && dateValid,
                onClick = {
                    val amount = parsedTarget ?: return@TextButton
                    onSave(
                        SavingsGoal(
                            id = goal?.id ?: SavingsGoal(name = name.trim(), targetAmount = amount).id,
                            name = name.trim(),
                            targetAmount = amount,
                            currentAmount = parsedCurrent.coerceAtMost(amount),
                            targetDate = parsedDate,
                        ),
                    )
                },
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (goal != null) {
                    TextButton(onClick = { onDelete(goal) }) {
                        Text(
                            text = "Delete",
                            color = financePalette().expense,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },
        title = { Text(if (goal == null) "Add goal" else "Edit goal", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal name") },
                    singleLine = true,
                    shape = InputShape,
                    colors = AppFieldColors(),
                )
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Target amount (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = InputShape,
                    colors = AppFieldColors(),
                )
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Saved so far (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = InputShape,
                    colors = AppFieldColors(),
                )
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target date (YYYY-MM-DD)") },
                    singleLine = true,
                    isError = !dateValid,
                    shape = InputShape,
                    colors = AppFieldColors(),
                )
            }
        },
        shape = CardShape,
    )
}

@Composable
private fun accentForCategory(category: String): Color {
    val palette = financePalette()
    return when (category) {
        "Food", "Groceries" -> palette.income
        "Transport", "Fuel", "Travel" -> palette.sky
        "Bills", "Subscriptions" -> palette.violet
        "Shopping", "Entertainment" -> palette.amber
        "Health" -> palette.expense
        else -> palette.slate
    }
}

private fun Context.loadBudgetLimits(): Map<String, Double> {
    val prefs = getSharedPreferences(PlanningPrefs, Context.MODE_PRIVATE)
    val limits = DefaultBudgetLimits.mapValues { (category, fallback) ->
        prefs.getFloat("budget_$category", fallback.toFloat()).toDouble()
    }.toMutableMap()
    prefs.all.forEach { (key, value) ->
        if (key.startsWith("budget_") && value is Float) {
            limits[key.removePrefix("budget_")] = value.toDouble()
        }
    }
    return limits
}

private fun Context.saveBudgetLimit(category: String, limit: Double) {
    getSharedPreferences(PlanningPrefs, Context.MODE_PRIVATE)
        .edit()
        .putFloat("budget_$category", limit.toFloat())
        .apply()
}

private fun Context.loadGoals(): List<SavingsGoal> {
    val raw = getSharedPreferences(PlanningPrefs, Context.MODE_PRIVATE).getString(PlanningGoalsKey, null)
        ?: return emptyList()
    return raw
        .lineSequence()
        .mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size < 5) return@mapNotNull null
            val target = parts[2].toDoubleOrNull() ?: return@mapNotNull null
            SavingsGoal(
                id = parts[0],
                name = decodePlanningPart(parts[1]),
                targetAmount = target,
                currentAmount = parts[3].toDoubleOrNull() ?: 0.0,
                targetDate = parts[4].takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            )
        }
        .sortedBy { it.targetDate ?: LocalDate.MAX }
        .toList()
}

private fun Context.saveGoals(goals: List<SavingsGoal>) {
    val encoded = goals.joinToString("\n") { goal ->
        listOf(
            goal.id,
            encodePlanningPart(goal.name),
            goal.targetAmount.toString(),
            goal.currentAmount.toString(),
            goal.targetDate?.toString().orEmpty(),
        ).joinToString("\t")
    }
    getSharedPreferences(PlanningPrefs, Context.MODE_PRIVATE)
        .edit()
        .putString(PlanningGoalsKey, encoded)
        .apply()
}

private fun encodePlanningPart(value: String): String =
    URLEncoder.encode(value, StandardCharsets.UTF_8.name())

private fun decodePlanningPart(value: String): String =
    URLDecoder.decode(value, StandardCharsets.UTF_8.name())

@Composable
private fun EmptyActivityCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun TransactionsSummaryCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    val now = YearMonth.now()
    val monthRows = transactions.filter { YearMonth.from(it.date) == now }
    val income = monthRows.filter { it.amount > 0 }.sumOf { it.amount }
    val expenses = monthRows.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    val net = income - expenses

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
        shadowElevation = if (financePalette().darkMode) 0.dp else 2.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "This month",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = money(net, showSign = true),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                    ).merge(MoneyTextStyle),
                    color = if (net >= 0) palette.income else palette.expense,
                )
            }
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = appSurface(),
            ) {
                Text(
                    text = "${transactions.size} ${if (transactions.size == 1) "entry" else "entries"}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CompactMoneyPill("Income", income, palette.income, Modifier.weight(1f))
            CompactMoneyPill("Spend", expenses, palette.expense, Modifier.weight(1f))
        }
        }
    }
}

@Composable
private fun CompactMoneyPill(
    label: String,
    amount: Double,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = tint.copy(alpha = 0.1f),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = compactMoney(amount),
                color = tint,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold).merge(MoneyTextStyle),
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun FilterPill(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.heightIn(min = 44.dp),
        shape = ButtonShape,
        color = if (selected) MaterialTheme.colorScheme.primary else softSurface(),
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.38f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.14f),
        ),
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun InsightsContent(modifier: Modifier = Modifier) {
    val transactions = TransactionsStore.transactions
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            ScreenTitle(
                title = "Insights",
                subtitle = "Spending patterns from your activity",
                modifier = Modifier.enterMotion(),
            )
        }
        if (transactions.isEmpty()) {
            item {
                EmptyActivityCard(
                    "Insights need activity",
                    "Add or import a few entries and PennyRush will show useful spending patterns here.",
                    modifier = Modifier.enterMotion(70),
                )
            }
        } else {
            val insights = buildLocalInsights(transactions)
            val thisMonth = transactions.filter { YearMonth.from(it.date) == YearMonth.now() }
            val byCategory = thisMonth
                .filter { it.amount < 0 }
                .groupBy { CategorizationRules.categoryNameFor(it) }
                .map { (category, txns) -> category to txns.sumOf { abs(it.amount) } }
                .sortedByDescending { it.second }
            val byMerchant = thisMonth
                .filter { it.amount < 0 }
                .groupBy { it.merchant }
                .map { (merchant, txns) -> merchant to txns.sumOf { abs(it.amount) } }
                .sortedByDescending { it.second }
                .take(5)

            item {
                InsightHero(transactions, modifier = Modifier.enterMotion(70))
            }
            items(insights) { insight ->
                InsightCard(insight, modifier = Modifier.enterMotion(110))
            }
            item {
                SpendingBarsCard("Spend by category", byCategory, modifier = Modifier.enterMotion(150))
            }
            item {
                SpendingBarsCard("Merchant watchlist", byMerchant, modifier = Modifier.enterMotion(190))
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun InsightHero(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    val now = YearMonth.now()
    val monthRows = transactions.filter { YearMonth.from(it.date) == now }
    val income = monthRows.filter { it.amount > 0 }.sumOf { it.amount }
    val expenses = monthRows.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    val net = income - expenses
    // A month with nothing in it is not a tight month. Scoring zero income
    // against zero spending landed in the bottom bucket and reported "Tight" to
    // anybody who opened the app before their first entry of the month, which
    // is a claim about their finances made from no evidence at all.
    val hasActivity = monthRows.isNotEmpty()
    val score = when {
        income <= 0.0 && expenses <= 0.0 -> 0.0
        income <= 0.0 -> 0.18
        else -> ((income - expenses) / income).coerceIn(0.0, 1.0)
    }
    val label = when {
        !hasActivity -> "No activity yet"
        score >= 0.35 -> "Healthy"
        score >= 0.15 -> "Watchful"
        else -> "Tight"
    }
    val scoreProgress by animateFloatAsState(
        targetValue = if (hasActivity) score.toFloat().coerceIn(0.05f, 1f) else 0f,
        animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing),
        label = "cashflowScore",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
        shadowElevation = if (palette.darkMode) 0.dp else 2.dp,
        tonalElevation = if (palette.darkMode) 2.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Cashflow health",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    // The empty-state wording is a sentence, not a one-word
                    // verdict, so it drops a size rather than wrapping.
                    style = if (hasActivity) {
                        MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp,
                        )
                    } else {
                        MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    },
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (hasActivity) {
                    Text(
                        text = money(net, showSign = true),
                        color = if (net >= 0) palette.income else palette.expense,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold).merge(MoneyTextStyle),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(softSurface(), RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(scoreProgress)
                        .height(8.dp)
                        .background(if (score >= 0.15) palette.income else palette.expense, RoundedCornerShape(999.dp)),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassStat("Income", compactMoney(income), palette.income, Modifier.weight(1f))
                GlassStat("Spend", compactMoney(expenses), palette.expense, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GlassStat(label: String, value: String, tint: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.field),
        // Neutral container. Tinting the box as well as the figure meant three
        // stats side by side read as three unrelated states.
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(modifier = Modifier.padding(Space.md)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(Modifier.height(Space.xs))
            Text(
                text = value,
                color = tint,
                style = MaterialTheme.typography.titleLarge.merge(MoneyTextStyle),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SpendingBarsCard(
    title: String,
    rows: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
) {
    if (rows.isEmpty()) return
    val palette = financePalette()
    val barTints = listOf(
        palette.income,
        palette.sky,
        palette.violet,
        palette.amber,
        palette.expense,
        palette.slate,
    )
    val total = rows.sumOf { it.second }.coerceAtLeast(1.0)

    PrCard(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            ),
        )
        Spacer(Modifier.height(14.dp))
        rows.take(6).forEachIndexed { index, (label, amount) ->
            val tint = barTints[index % barTints.size]
            val progress by animateFloatAsState(
                targetValue = (amount / total).toFloat().coerceIn(0.04f, 1f),
                animationSpec = tween(durationMillis = 680, delayMillis = index * 55, easing = FastOutSlowInEasing),
                label = "spendingBar$index",
            )
            Column(modifier = Modifier.padding(vertical = 7.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = compactMoney(amount),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold).merge(MoneyTextStyle),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .background(softSurface(), RoundedCornerShape(999.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(7.dp)
                            .background(tint, RoundedCornerShape(999.dp)),
                    )
                }
            }
        }
    }
}

private data class FinanceInsight(
    val title: String,
    val body: String,
    val severity: InsightSeverity,
)

private enum class InsightSeverity {
    Info,
    Success,
    Warning,
}

@Composable
private fun InsightCard(
    insight: FinanceInsight,
    modifier: Modifier = Modifier,
) {
    val palette = financePalette()
    val (icon, tint) = when (insight.severity) {
        InsightSeverity.Success -> Icons.Rounded.AutoAwesome to palette.income
        InsightSeverity.Warning -> Icons.Rounded.NotificationsActive to palette.expense
        InsightSeverity.Info -> Icons.Rounded.Info to MaterialTheme.colorScheme.primary
    }
    PrCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(14.dp),
                color = tint.copy(alpha = 0.16f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = insight.body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun buildLocalInsights(transactions: List<Transaction>): List<FinanceInsight> {
    val now = YearMonth.now()
    val thisMonth = transactions.filter { YearMonth.from(it.date) == now }
    val income = thisMonth.filter { it.amount > 0 }.sumOf { it.amount }
    val expenses = thisMonth.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    val insights = mutableListOf<FinanceInsight>()

    if (income > 0) {
        val savingsRate = ((income - expenses) / income).coerceIn(-1.0, 1.0)
        insights += when {
            savingsRate >= 0.25 -> FinanceInsight(
                "Strong savings month",
                "You have kept ${(savingsRate * 100).toInt()}% of income so far this month.",
                InsightSeverity.Success,
            )
            savingsRate < 0.05 -> FinanceInsight(
                "Savings buffer is tight",
                "Expenses are close to income this month. Review flexible spending before month end.",
                InsightSeverity.Warning,
            )
            else -> FinanceInsight(
                "Savings pace is steady",
                "You are saving ${(savingsRate * 100).toInt()}% of income this month.",
                InsightSeverity.Info,
            )
        }
    }

    thisMonth.filter { it.amount < 0 }
        .maxByOrNull { abs(it.amount) }
        ?.let { largest ->
            insights += FinanceInsight(
                "Largest expense",
                "${largest.merchant} is your biggest outflow this month at ${money(largest.amount)}.",
                InsightSeverity.Info,
            )
        }

    thisMonth.filter { it.amount < 0 }
        .groupBy { CategorizationRules.categoryNameFor(it) }
        .map { (category, txns) -> category to txns.sumOf { abs(it.amount) } }
        .maxByOrNull { it.second }
        ?.let { (category, total) ->
            insights += FinanceInsight(
                "Top spending area",
                "$category leads this month at ${money(total)}.",
                if (category == "Other") InsightSeverity.Warning else InsightSeverity.Info,
            )
        }

    transactions.filter { it.amount < 0 }
        .groupBy { it.merchant.lowercase().trim() }
        .filter { it.value.size >= 2 }
        .maxByOrNull { it.value.sumOf { txn -> abs(txn.amount) } }
        ?.value
        ?.firstOrNull()
        ?.let { recurring ->
            insights += FinanceInsight(
                "Recurring merchant spotted",
                "${recurring.merchant} appears multiple times. Consider tracking it as a subscription or regular bill.",
                InsightSeverity.Info,
            )
        }

    if (insights.isEmpty()) {
        insights += FinanceInsight(
            "Add a little more activity",
            "Add or import a few more entries to unlock cashflow and recurring-spend insights.",
            InsightSeverity.Info,
        )
    }

    return insights.take(5)
}

@Composable
private fun AccountContent(
    userEmail: String?,
    userName: String?,
    userAvatarUrl: String?,
    appVersion: String,
    sync: TransactionsSync,
    canUseAppLock: () -> Boolean,
    onImportStatement: () -> Unit,
    onScanReceipt: () -> Unit,
    notificationsAllowed: Boolean,
    onRequestNotifications: () -> Unit,
    onDeleteAccount: suspend () -> Unit,
    onSignOut: suspend () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val themeMode = ThemePreferences.themeMode
    val palette = financePalette()

    val budgetAlerts = AppPreferences.budgetAlerts
    val largeTxnAlerts = AppPreferences.largeTransactions
    val biometricLock = AppPreferences.biometricLock
    val transactions = TransactionsStore.transactions

    var versionDialog by remember { mutableStateOf(false) }
    var currencyDialog by remember { mutableStateOf(false) }
    var currencyDraft by remember { mutableStateOf(AppPreferences.currencyCode) }
    var deleteActivityDialog by remember { mutableStateOf(false) }
    var deletingActivity by remember { mutableStateOf(false) }
    var deleteAccountDialog by remember { mutableStateOf(false) }
    var deletingAccount by remember { mutableStateOf(false) }
    var deleteAccountConfirmation by remember { mutableStateOf("") }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val txns = TransactionsStore.transactions
            val outcome = runCatching {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.write(buildTransactionsCsv(txns).toByteArray(Charsets.UTF_8))
                    } ?: error("Could not open destination file.")
                }
            }
            Toast.makeText(
                context,
                outcome.fold(
                    { "Exported ${txns.size} ${if (txns.size == 1) "entry" else "entries"}" },
                    { "Export failed: ${it.message ?: "unknown error"}" },
                ),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun openExternal(url: String) {
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }.onFailure { toast("Couldn't open link") }
    }

    fun sendFeedback() {
        openExternal("https://github.com/royalpinto007/PennyRush/issues")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            ScreenTitle(
                title = "Account",
                subtitle = "Settings, privacy, and data tools",
                modifier = Modifier.enterMotion(),
            )
        }
        item {
            ProfileCard(
                userEmail = userEmail,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                modifier = Modifier.enterMotion(60),
            )
        }
        item {
            DataHealthCard(transactions, modifier = Modifier.enterMotion(100))
        }

        item { SectionLabel("Account") }
        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.CurrencyRupee,
                    title = "Currency",
                    subtitle = "Used for your accounts and reports",
                    trailing = { SettingsValue(AppPreferences.currencyCode) },
                    onClick = {
                        currencyDraft = AppPreferences.currencyCode
                        currencyDialog = true
                    },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.AccountBalance,
                    title = "Primary account",
                    subtitle = "${transactions.size} saved ${if (transactions.size == 1) "entry" else "entries"}",
                )
            }
        }

        item { SectionLabel("Categories") }
        item {
            CategoryMapCard(
                transactions = transactions,
                modifier = Modifier.enterMotion(130),
            )
        }

        item { SectionLabel("Display") }
        item {
            SettingsGroup {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SettingsIconTile(Icons.Rounded.Palette)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Theme",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                "Follows ${themeMode.name.lowercase()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        ThemeSelector(
                            current = themeMode,
                            onChange = { ThemePreferences.set(it) },
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        item { SectionLabel("Data") }
        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.CameraAlt,
                    title = "Scan receipt",
                    subtitle = "Create an entry from an image",
                    onClick = onScanReceipt,
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.FileDownload,
                    title = "Import statement",
                    subtitle = "Bank statement file",
                    onClick = onImportStatement,
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.IosShare,
                    title = "Export activity",
                    subtitle = "Download a spreadsheet file",
                    onClick = {
                        val count = TransactionsStore.transactions.size
                        if (count == 0) {
                            toast("Nothing to export yet")
                        } else {
                            exportLauncher.launch("pennyrush-${LocalDate.now()}.csv")
                        }
                    },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.DeleteSweep,
                    title = "Delete activity",
                    subtitle = "Permanently remove every saved entry",
                    onClick = { deleteActivityDialog = true },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.Lock,
                    title = "Delete account",
                    subtitle = "Remove your PennyRush account and saved app data",
                    onClick = {
                        deleteAccountConfirmation = ""
                        deleteAccountDialog = true
                    },
                )
            }
        }

        item { SectionLabel("Notifications") }
        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.NotificationsActive,
                    title = "Notification access",
                    subtitle = if (notificationsAllowed) "Allowed for budget and spend alerts" else "Required for budget and big-spend alerts",
                    trailing = {
                        SettingsValue(if (notificationsAllowed) "On" else "Off")
                    },
                    onClick = onRequestNotifications,
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.BarChart,
                    title = "Budget alerts",
                    subtitle = "When you cross a category limit",
                    trailing = {
                        Switch(
                            checked = budgetAlerts,
                            onCheckedChange = {
                                AppPreferences.updateBudgetAlerts(it)
                                if (it) onRequestNotifications()
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.NotificationsActive,
                    title = "Big spends",
                    subtitle = "Notify when over ₹5,000",
                    trailing = {
                        Switch(
                            checked = largeTxnAlerts,
                            onCheckedChange = {
                                AppPreferences.updateLargeTransactions(it)
                                if (it) onRequestNotifications()
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    },
                )
            }
        }

        item { SectionLabel("Security") }
        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Biometric lock",
                    subtitle = "Require device unlock to open PennyRush",
                    trailing = {
                        Switch(
                            checked = biometricLock,
                            onCheckedChange = {
                                if (it && !canUseAppLock()) {
                                    toast("Set up fingerprint, face unlock, or a device PIN in Android settings first")
                                    return@Switch
                                }
                                AppPreferences.updateBiometricLock(it)
                                toast(if (it) "Biometric lock enabled" else "Biometric lock disabled")
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    },
                )
            }
        }

        item { SectionLabel("About") }
        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    title = "Version",
                    subtitle = "PennyRush for Android",
                    trailing = { SettingsValue(appVersion) },
                    onClick = { versionDialog = true },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.Lock,
                    title = "Privacy policy",
                    onClick = { openExternal("https://pennyrush.dev/privacy") },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.Description,
                    title = "Terms of service",
                    onClick = { openExternal("https://pennyrush.dev/terms") },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.AutoMirrored.Rounded.Send,
                    title = "Send feedback",
                    subtitle = "Tell us what to build next",
                    onClick = { sendFeedback() },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ButtonShape)
                    .clickable(role = Role.Button) { scope.launch { onSignOut() } },
                shape = ButtonShape,
                color = palette.expense.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, palette.expense.copy(alpha = 0.38f)),
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Sign out",
                        color = palette.expense,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if (versionDialog) {
        AlertDialog(
            onDismissRequest = { versionDialog = false },
            confirmButton = {
                TextButton(onClick = { versionDialog = false }) { Text("Close") }
            },
            title = { Text("PennyRush", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Version $appVersion")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Android ${android.os.Build.VERSION.RELEASE} · ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            shape = CardShape,
        )
    }

    if (currencyDialog) {
        AlertDialog(
            onDismissRequest = { currencyDialog = false },
            confirmButton = {
                Button(
                    enabled = isValidCurrencyCode(currencyDraft),
                    onClick = {
                        AppPreferences.updateCurrencyCode(currencyDraft)
                        currencyDialog = false
                        toast("Currency set to ${AppPreferences.currencyCode}")
                    },
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { currencyDialog = false }) { Text("Cancel") }
            },
            title = { Text("Choose currency", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = currencyDraft,
                        onValueChange = { value ->
                            currencyDraft = value.filter { it.isLetter() }.uppercase().take(3)
                        },
                        singleLine = true,
                        label = { Text("Currency code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = InputShape,
                        colors = AppFieldColors(),
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(CurrencyChoices) { choice ->
                            Surface(
                                modifier = Modifier.clickable(role = Role.Button) { currencyDraft = choice },
                                shape = ChipShape,
                                color = if (currencyDraft == choice) MaterialTheme.colorScheme.primary else softSurface(),
                                border = BorderStroke(
                                    1.dp,
                                    if (currencyDraft == choice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                                ),
                            ) {
                                Text(
                                    text = choice,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                    color = if (currencyDraft == choice) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                )
                            }
                        }
                    }
                    Text(
                        "This updates how amounts are shown in PennyRush. It does not convert existing amounts.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            shape = CardShape,
        )
    }

    if (deleteActivityDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!deletingActivity) deleteActivityDialog = false
            },
            confirmButton = {
                Button(
                    enabled = !deletingActivity,
                    onClick = {
                        val ids = TransactionsStore.transactions.map { it.id }
                        if (ids.isEmpty()) {
                            deleteActivityDialog = false
                            toast("No activity to delete")
                            return@Button
                        }
                        deletingActivity = true
                        scope.launch {
                            val outcome = runCatching {
                                if (sync.enabled) {
                                    ids.forEach { sync.deleteOne(it) }
                                }
                            }
                            deletingActivity = false
                            outcome
                                .onSuccess {
                                    TransactionsStore.clear()
                                    deleteActivityDialog = false
                                    Toast.makeText(context, "Activity deleted", Toast.LENGTH_LONG).show()
                                }
                                .onFailure {
                                    Toast.makeText(
                                        context,
                                        "Couldn't delete activity: ${it.message ?: "unknown error"}",
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.expense,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(if (deletingActivity) "Deleting..." else "Delete activity")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !deletingActivity,
                    onClick = { deleteActivityDialog = false },
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete all activity?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This permanently removes every activity entry from PennyRush. Export a CSV first if you want a copy.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            shape = CardShape,
        )
    }

    if (deleteAccountDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!deletingAccount) deleteAccountDialog = false
            },
            confirmButton = {
                Button(
                    enabled = !deletingAccount && deleteAccountConfirmation == "DELETE ACCOUNT",
                    onClick = {
                        deletingAccount = true
                        scope.launch {
                            runCatching { onDeleteAccount() }
                                .onSuccess {
                                    TransactionsStore.clear()
                                    deleteAccountDialog = false
                                    Toast.makeText(context, "Account deleted", Toast.LENGTH_LONG).show()
                                }
                                .onFailure {
                                    deletingAccount = false
                                    Toast.makeText(
                                        context,
                                        "Couldn't delete account: ${it.message ?: "unknown error"}",
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.expense,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(if (deletingAccount) "Deleting..." else "Delete account")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !deletingAccount,
                    onClick = { deleteAccountDialog = false },
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete PennyRush account?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This permanently removes your PennyRush account and saved app data. It does not delete your Google account. Export your activity first if you need a copy.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = deleteAccountConfirmation,
                        onValueChange = { deleteAccountConfirmation = it },
                        enabled = !deletingAccount,
                        singleLine = true,
                        label = { Text("Type DELETE ACCOUNT") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = InputShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = palette.expense,
                            focusedLabelColor = palette.expense,
                        ),
                    )
                }
            },
            shape = CardShape,
        )
    }
}

@Composable
private fun DataHealthCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    val monthRows = transactions.filter { YearMonth.from(it.date) == YearMonth.now() }
    val income = monthRows.filter { it.amount > 0 }.sumOf { it.amount }
    val spend = monthRows.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    val categorized = transactions.count { CategorizationRules.categoryNameFor(it) != "Other" }
    val categorizedRate = if (transactions.isEmpty()) 0 else (categorized * 100 / transactions.size)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Money profile",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = if (transactions.isEmpty()) "Import or add entries to unlock trends." else "Organized, categorized, and export-ready.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(11.dp).size(20.dp),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DataHealthMetric("Entries", transactions.size.toString(), Modifier.weight(1f))
                DataHealthMetric("This month", compactMoney(income - spend), Modifier.weight(1f))
                DataHealthMetric("Tagged", "$categorizedRate%", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CategoryMapCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    val categories = CategorizationRules.visibleCategories()
    val counts = transactions.groupingBy { CategorizationRules.categoryNameFor(it) }.eachCount()
    val otherCount = counts["Other"] ?: 0

    PrCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Spending groups",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                    ),
                )
                Text(
                    text = if (transactions.isEmpty()) {
                        "Groups appear as you add activity."
                    } else if (otherCount > transactions.size / 3) {
                        "Many entries are in Other. Edit merchant names to sharpen groups."
                    } else {
                        "Used across Activity, Plan, and Insights."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            SettingsIconTile(Icons.Rounded.Category)
        }
        Spacer(Modifier.height(16.dp))
        categories.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { category ->
                    CategoryCountPill(
                        name = category,
                        count = counts[category] ?: 0,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CategoryCountPill(
    name: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = softSurface(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = count.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
private fun DataHealthMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
            )
        }
    }
}

private fun buildTransactionsCsv(transactions: List<Transaction>): String {
    val sb = StringBuilder()
    sb.append("Date,Description,Merchant,Amount,Type,Kind\n")
    transactions.sortedByDescending { it.date }.forEach { t ->
        val desc = t.description.replace("\"", "\"\"")
        val merch = t.merchant.replace("\"", "\"\"")
        val type = if (t.amount >= 0) "Income" else "Expense"
        sb.append("${t.date},\"$desc\",\"$merch\",${"%.2f".format(t.amount)},$type,${t.kind}\n")
    }
    return sb.toString()
}

@Composable
private fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
        shadowElevation = 1.dp,
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val baseModifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)
    val rowModifier = (if (onClick != null) baseModifier.clickable(role = Role.Button, onClick = onClick) else baseModifier)
        .padding(horizontal = 16.dp, vertical = 14.dp)

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconTile(icon)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SettingsIconTile(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        color = softSurface(),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun SettingsValue(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 64.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        thickness = 1.dp,
    )
}

@Composable
private fun ProfileCard(
    userEmail: String?,
    userName: String?,
    userAvatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    PrCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
            ) {
                if (!userAvatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = userAvatarUrl,
                        contentDescription = userName ?: userEmail,
                        modifier = Modifier.size(72.dp).clip(CircleShape),
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initialsFor(userName, userEmail),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = userName ?: "Signed in",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                userEmail?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    current: ThemeMode,
    onChange: (ThemeMode) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ThemeMode.entries.forEach { mode ->
            val selected = mode == current
            Surface(
                modifier = Modifier.weight(1f).height(52.dp),
                shape = ButtonShape,
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else softSurface(),
                border = BorderStroke(
                    1.dp,
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                ),
                onClick = { onChange(mode) },
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = mode.name,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

// ─── Quick add sheet ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAddSheet(
    onScanReceipt: () -> Unit,
    onImportStatement: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = financePalette()
    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val amountValue = amount.toDoubleOrNull()?.let { if (isExpense) -abs(it) else abs(it) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Add entry",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                ),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = ButtonShape,
                    color = if (isExpense) palette.expense.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (isExpense) palette.expense.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline,
                    ),
                    onClick = { isExpense = true },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "Expense",
                            color = if (isExpense) palette.expense else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = ButtonShape,
                    color = if (!isExpense) palette.income.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (!isExpense) palette.income.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline,
                    ),
                    onClick = { isExpense = false },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "Income",
                            color = if (!isExpense) palette.income else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("Amount (₹)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                colors = AppFieldColors(),
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                colors = AppFieldColors(),
            )

            PrButton(
                text = "Save entry",
                onClick = {
                    val value = amountValue ?: return@PrButton
                    onSave(
                        Transaction(
                            date = LocalDate.now(),
                            description = description.trim(),
                            merchant = MerchantExtractor.analyze(description).merchant,
                            amount = value,
                            kind = MerchantExtractor.analyze(description).kind,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amountValue != null && description.isNotBlank(),
            )

            PrSecondaryButton(
                text = "Scan receipt instead",
                onClick = onScanReceipt,
                modifier = Modifier.fillMaxWidth(),
            )

            PrSecondaryButton(
                text = "Import statement instead",
                onClick = onImportStatement,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDetailSheet(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: (String) -> Unit,
) {
    val palette = financePalette()
    var isExpense by remember(transaction.id) { mutableStateOf(transaction.amount < 0) }
    var amount by remember(transaction.id) { mutableStateOf("%.2f".format(abs(transaction.amount))) }
    var description by remember(transaction.id) { mutableStateOf(transaction.description) }
    var dateText by remember(transaction.id) { mutableStateOf(transaction.date.toString()) }
    var confirmDelete by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val amountValue = amount.toDoubleOrNull()?.let { if (isExpense) -abs(it) else abs(it) }
    val parsedDate = runCatching { LocalDate.parse(dateText) }.getOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Entry details",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                ),
            )
            KindChip(transaction.kind)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = ButtonShape,
                    color = if (isExpense) palette.expense.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (isExpense) palette.expense.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline,
                    ),
                    onClick = { isExpense = true },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "Expense",
                            color = if (isExpense) palette.expense else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = ButtonShape,
                    color = if (!isExpense) palette.income.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (!isExpense) palette.income.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline,
                    ),
                    onClick = { isExpense = false },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "Income",
                            color = if (!isExpense) palette.income else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("Amount (₹)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                colors = AppFieldColors(),
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                colors = AppFieldColors(),
            )
            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Date (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                colors = AppFieldColors(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrSecondaryButton(
                    text = "Delete",
                    onClick = { confirmDelete = true },
                    modifier = Modifier.weight(1f),
                )
                PrButton(
                    text = "Save",
                    onClick = {
                        val value = amountValue ?: return@PrButton
                        val date = parsedDate ?: return@PrButton
                        val analysis = MerchantExtractor.analyze(description)
                        onSave(
                            transaction.copy(
                                date = date,
                                description = description.trim(),
                                merchant = analysis.merchant,
                                amount = value,
                                kind = analysis.kind,
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = amountValue != null && parsedDate != null && description.isNotBlank(),
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        onDelete(transaction.id)
                    },
                ) {
                    Text("Delete", color = palette.expense, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            },
            title = { Text("Delete entry?", fontWeight = FontWeight.Bold) },
            text = { Text("This removes the entry from PennyRush across your account.") },
            shape = CardShape,
        )
    }
}

// ─── Receipt image scan ────────────────────────────────────────────────────────

@Composable
private fun ReceiptScanScreen(
    state: ReceiptScanState,
    onCancel: () -> Unit,
    onRetakePhoto: () -> Unit,
    onChooseImage: () -> Unit,
    onSave: (Transaction) -> Unit,
) {
    val palette = financePalette()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Text(
                    "Scan receipt",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    state.fileName(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        when (state) {
            is ReceiptScanState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    ReceiptImagePreview(state.uri)
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        "Reading receipt text...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            is ReceiptScanState.Ready -> {
                val candidate = state.candidate
                var isExpense by remember(candidate.name, candidate.uri) { mutableStateOf(true) }
                var merchant by remember(candidate.name, candidate.uri) { mutableStateOf(candidate.merchant) }
                var amount by remember(candidate.name, candidate.uri) { mutableStateOf(candidate.amountText) }
                var dateText by remember(candidate.name, candidate.uri) { mutableStateOf(candidate.dateText) }
                var note by remember(candidate.name, candidate.uri) { mutableStateOf(candidate.note) }
                val amountValue = amount.toDoubleOrNull()
                val parsedDate = runCatching { LocalDate.parse(dateText) }.getOrNull()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item {
                        ReceiptImagePreview(candidate.uri)
                    }
                    candidate.warning?.let { warning ->
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = CardShape,
                                color = palette.amber.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, palette.amber.copy(alpha = 0.35f)),
                            ) {
                                Text(
                                    text = warning,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TypeChoice(
                                label = "Expense",
                                selected = isExpense,
                                tint = palette.expense,
                                modifier = Modifier.weight(1f),
                                onClick = { isExpense = true },
                            )
                            TypeChoice(
                                label = "Income",
                                selected = !isExpense,
                                tint = palette.income,
                                modifier = Modifier.weight(1f),
                                onClick = { isExpense = false },
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = merchant,
                            onValueChange = { merchant = it },
                            label = { Text("Merchant") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = InputShape,
                            colors = AppFieldColors(),
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Amount (₹)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            shape = InputShape,
                            colors = AppFieldColors(),
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = dateText,
                            onValueChange = { dateText = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = InputShape,
                            colors = AppFieldColors(),
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Note") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = InputShape,
                            colors = AppFieldColors(),
                        )
                    }
                    if (candidate.extractedText.isNotBlank()) {
                        item {
                            PrCard(padding = 16) {
                                Text(
                                    "Detected text",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    candidate.extractedText
                                        .lineSequence()
                                        .filter { it.isNotBlank() }
                                        .take(8)
                                        .joinToString("\n"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                    item {
                        PrButton(
                            text = "Save scan",
                            onClick = {
                                val value = amountValue ?: return@PrButton
                                val date = parsedDate ?: return@PrButton
                                val cleanMerchant = merchant.trim().ifBlank { "Scanned receipt" }
                                val cleanNote = note.trim().ifBlank { cleanMerchant }
                                val analysis = MerchantExtractor.analyze("$cleanMerchant $cleanNote")
                                onSave(
                                    Transaction(
                                        date = date,
                                        description = cleanNote,
                                        merchant = cleanMerchant,
                                        amount = if (isExpense) -abs(value) else abs(value),
                                        kind = analysis.kind,
                                    ),
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = amountValue != null && parsedDate != null && merchant.isNotBlank(),
                        )
                    }
                    item {
                        PrSecondaryButton(
                            text = "Retake photo",
                            onClick = onRetakePhoto,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        PrSecondaryButton(
                            text = "Choose existing image",
                            onClick = onChooseImage,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ReceiptImagePreview(uri: Uri) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = appSurface(),
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Selected receipt image",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun TypeChoice(
    label: String,
    selected: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.height(50.dp),
        shape = ButtonShape,
        color = if (selected) tint.copy(alpha = 0.16f) else appSurface(),
        border = BorderStroke(1.dp, if (selected) tint.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (selected) tint else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

// ─── Statement preview ─────────────────────────────────────────────────────────

@Composable
private fun StatementPreviewScreen(
    state: StatementPreviewState,
    onCancel: () -> Unit,
    onImport: (List<Transaction>) -> Unit,
) {
    val palette = financePalette()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Text(
                    "Statement preview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    state.fileName(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        when (state) {
            is StatementPreviewState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Parsing…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            is StatementPreviewState.PdfNotSupported -> Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "PDF parsing isn't built yet",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    "Most banks let you download a spreadsheet version of the same statement. Try that and import again.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                PrButton("Got it", onClick = onCancel, modifier = Modifier.fillMaxWidth())
            }
            is StatementPreviewState.Failed -> Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "Couldn't read that file",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    state.reason,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (state.sample.isNotEmpty()) {
                    PrCard(padding = 14) {
                        Text(
                            "First lines we saw:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                        state.sample.forEach {
                            Text(it, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                PrButton("Choose another file", onClick = onCancel, modifier = Modifier.fillMaxWidth())
            }
            is StatementPreviewState.Success -> {
                val income = state.transactions.filter { it.amount > 0 }.sumOf { it.amount }
                val expenses = state.transactions.filter { it.amount < 0 }.sumOf { abs(it.amount) }
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "${state.transactions.size} ${if (state.transactions.size == 1) "entry" else "entries"}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("In", income, palette.income, Icons.Rounded.ArrowDownward, Modifier.weight(1f))
                        StatCard("Out", expenses, palette.expense, Icons.Rounded.ArrowUpward, Modifier.weight(1f))
                    }
                }
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 20.dp).padding(top = 16.dp),
                ) {
                    items(state.transactions) { TransactionRow(it) }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PrSecondaryButton(
                        "Cancel",
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                    )
                    PrButton(
                        "Import ${state.transactions.size}",
                        onClick = { onImport(state.transactions) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private sealed interface StatementPreviewState {
    data class Loading(val name: String) : StatementPreviewState
    data class Success(val name: String, val transactions: List<Transaction>) : StatementPreviewState
    data class Failed(val name: String, val reason: String, val sample: List<String>) : StatementPreviewState
    data class PdfNotSupported(val name: String) : StatementPreviewState
}

private sealed interface ReceiptScanState {
    data class Loading(val name: String, val uri: Uri) : ReceiptScanState
    data class Ready(val candidate: ReceiptScanCandidate) : ReceiptScanState
}

private data class ReceiptScanCandidate(
    val name: String,
    val uri: Uri,
    val merchant: String,
    val amountText: String,
    val dateText: String,
    val note: String,
    val extractedText: String,
    val warning: String?,
)

private fun StatementPreviewState.fileName(): String = when (this) {
    is StatementPreviewState.Loading -> name
    is StatementPreviewState.Success -> name
    is StatementPreviewState.Failed -> name
    is StatementPreviewState.PdfNotSupported -> name
}

private fun ReceiptScanState.fileName(): String = when (this) {
    is ReceiptScanState.Loading -> name
    is ReceiptScanState.Ready -> candidate.name
}

private suspend fun scanReceiptImage(
    context: Context,
    uri: Uri,
    name: String,
): ReceiptScanState {
    val text = runCatching { recognizeReceiptText(context, uri) }.getOrDefault("")
    val warning = if (text.isBlank()) {
        "We couldn't confidently read this image. Review the fields below and save when it looks right."
    } else {
        null
    }
    return ReceiptScanState.Ready(buildReceiptCandidate(name, uri, text, warning))
}

private suspend fun recognizeReceiptText(context: Context, uri: Uri): String {
    val image = InputImage.fromFilePath(context, uri)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    return suspendCancellableCoroutine { continuation ->
        recognizer.process(image)
            .addOnSuccessListener { result ->
                recognizer.close()
                if (continuation.isActive) continuation.resume(result.text)
            }
            .addOnFailureListener { error ->
                recognizer.close()
                if (continuation.isActive) continuation.resumeWithException(error)
            }
        continuation.invokeOnCancellation { recognizer.close() }
    }
}

private fun buildReceiptCandidate(
    name: String,
    uri: Uri,
    extractedText: String,
    warning: String?,
): ReceiptScanCandidate {
    val lines = extractedText
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toList()
    val merchant = detectReceiptMerchant(lines)
        ?: MerchantExtractor.analyze(name).merchant.takeIf { it.isNotBlank() }
        ?: "Scanned receipt"
    val amount = detectReceiptAmount(lines)?.let { value ->
        if (value % 1.0 == 0.0) value.toLong().toString() else "%.2f".format(value)
    }.orEmpty()
    val date = detectReceiptDate(lines) ?: LocalDate.now()
    return ReceiptScanCandidate(
        name = name,
        uri = uri,
        merchant = merchant,
        amountText = amount,
        dateText = date.toString(),
        note = "Receipt scan - $merchant",
        extractedText = extractedText,
        warning = warning,
    )
}

private fun detectReceiptMerchant(lines: List<String>): String? {
    val blocked = Regex("(?i)(total|amount|paid|change|cash|card|upi|tax|gst|invoice|receipt|date|time|bill)")
    return lines
        .asSequence()
        .map { it.replace(Regex("\\s+"), " ").trim(' ', '-', ':') }
        .filter { it.length in 3..42 }
        .filterNot { blocked.containsMatchIn(it) }
        .filter { line -> line.any { it.isLetter() } }
        .firstOrNull()
}

private fun detectReceiptAmount(lines: List<String>): Double? {
    val priority = lines.filter { Regex("(?i)(grand\\s+total|total|amount|paid|debit|purchase)").containsMatchIn(it) }
    return extractAmounts(priority).maxOrNull() ?: extractAmounts(lines).maxOrNull()
}

private fun extractAmounts(lines: List<String>): List<Double> {
    val amountRegex = Regex("""(?i)(?:₹|rs\.?|inr)?\s*([0-9]{1,3}(?:,[0-9]{2,3})*(?:\.\d{1,2})?|[0-9]+(?:\.\d{1,2})?)""")
    return lines.flatMap { line ->
        amountRegex.findAll(line).mapNotNull { match ->
            match.groupValues[1].replace(",", "").toDoubleOrNull()
        }.filter { it > 0.0 && it < 1_000_000.0 }.toList()
    }
}

private fun detectReceiptDate(lines: List<String>): LocalDate? {
    val text = lines.joinToString(" ")
    Regex("""\b(20\d{2})[-/.](\d{1,2})[-/.](\d{1,2})\b""").find(text)?.let { match ->
        return runCatching {
            LocalDate.of(
                match.groupValues[1].toInt(),
                match.groupValues[2].toInt(),
                match.groupValues[3].toInt(),
            )
        }.getOrNull()
    }
    Regex("""\b(\d{1,2})[-/.](\d{1,2})[-/.](\d{2,4})\b""").find(text)?.let { match ->
        val year = match.groupValues[3].toInt().let { if (it < 100) 2000 + it else it }
        return runCatching {
            LocalDate.of(year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
        }.getOrNull()
    }
    return null
}

/** Decode bytes as text, honouring BOM, then trying strict UTF-8, then Windows-1252. */
private fun decodeBytes(buffer: ByteArray, length: Int): String {
    if (length >= 3 &&
        buffer[0] == 0xEF.toByte() && buffer[1] == 0xBB.toByte() && buffer[2] == 0xBF.toByte()
    ) {
        return String(buffer, 3, length - 3, StandardCharsets.UTF_8)
    }
    if (length >= 2 && buffer[0] == 0xFF.toByte() && buffer[1] == 0xFE.toByte()) {
        return String(buffer, 2, length - 2, StandardCharsets.UTF_16LE)
    }
    if (length >= 2 && buffer[0] == 0xFE.toByte() && buffer[1] == 0xFF.toByte()) {
        return String(buffer, 2, length - 2, StandardCharsets.UTF_16BE)
    }
    val strictUtf8 = runCatching {
        StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(buffer, 0, length))
            .toString()
    }
    return strictUtf8.getOrElse {
        String(buffer, 0, length, Charset.forName("windows-1252"))
    }
}

private suspend fun parseStatement(
    context: Context,
    uri: Uri,
    name: String,
): StatementPreviewState {
    val isPdf = name.endsWith(".pdf", ignoreCase = true) ||
        context.contentResolver.getType(uri)?.contains("pdf", ignoreCase = true) == true
    if (isPdf) return StatementPreviewState.PdfNotSupported(name)

    return withContext(Dispatchers.IO) {
        runCatching {
            val cap = StatementParser.MAX_BYTES
            val buffer = ByteArray(cap + 1)
            val read = context.contentResolver.openInputStream(uri)?.use { stream ->
                var total = 0
                while (total < buffer.size) {
                    val n = stream.read(buffer, total, buffer.size - total)
                    if (n <= 0) break
                    total += n
                }
                total
            } ?: 0
            if (read == 0) {
                return@runCatching StatementPreviewState.Failed(name, "File appears to be empty.", emptyList())
            }
            if (read > cap) {
                return@runCatching StatementPreviewState.Failed(
                    name,
                    "File is larger than 5 MB. A real bank statement is usually a few hundred KB.",
                    emptyList(),
                )
            }
            val text = decodeBytes(buffer, read)
            when (val outcome = StatementParser.parseCsv(text)) {
                is ParseOutcome.Success -> StatementPreviewState.Success(name, outcome.transactions)
                is ParseOutcome.Failed -> StatementPreviewState.Failed(name, outcome.reason, outcome.previewLines)
            }
        }.getOrElse {
            StatementPreviewState.Failed(name, it.message ?: "Could not open the file.", emptyList())
        }
    }
}

private enum class HomeDestination(
    val label: String,
    val icon: ImageVector,
) {
    Home("Home", Icons.Rounded.Home),
    Transactions("Activity", Icons.AutoMirrored.Rounded.ReceiptLong),
    Plan("Plan", Icons.Rounded.BarChart),
    Insights("Insights", Icons.Rounded.AutoAwesome),
    Account("Account", Icons.Rounded.Settings),
}

private fun Context.displayNameFor(uri: Uri): String {
    val fallback = uri.lastPathSegment ?: "Selected statement"
    return contentResolver
        .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else fallback
        } ?: fallback
}

private fun Context.createReceiptPhotoUri(): Uri {
    val directory = File(cacheDir, "receipt_scans").apply { mkdirs() }
    val file = File.createTempFile("receipt_${System.currentTimeMillis()}_", ".jpg", directory)
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
}

private fun initialsFor(name: String?, email: String?): String {
    name?.takeIf { it.isNotBlank() }?.let { n ->
        val parts = n.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "?"
        }
    }
    email?.takeIf { it.isNotBlank() }?.let { return it.first().uppercase() }
    return "?"
}
