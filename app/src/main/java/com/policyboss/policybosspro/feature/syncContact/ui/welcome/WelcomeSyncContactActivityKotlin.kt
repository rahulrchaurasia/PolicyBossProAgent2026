package com.policyboss.policybosspro.feature.syncContact.ui.welcome

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.policyboss.policybosspro.MainActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.ActivityWelcomeSyncContactKotlinBinding
import com.policyboss.policybosspro.utils.permission.openAppSettings
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactEvent
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeSyncContactActivityKotlin : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityWelcomeSyncContactKotlinBinding
    private val viewModel: WelcomeSyncContactViewModel by viewModels()

    private var dialog: Dialog? = null
    private lateinit var onPageChangeListener: ViewPager.OnPageChangeListener

    private var current = 0
    private var hasNavigated = false

    // Base padding preserved from XML layouts
    private var baseMainContentPaddingLeft = 0
    private var baseMainContentPaddingTop = 0
    private var baseMainContentPaddingRight = 0
    private var baseMainContentPaddingBottom = 0

    private var baseFooterPaddingLeft = 0
    private var baseFooterPaddingTop = 0
    private var baseFooterPaddingRight = 0
    private var baseFooterPaddingBottom = 0

    // Slide 4 interactive components
    private var btnchkagree: CheckBox? = null
    private var btnchkcommunication_sms: CheckBox? = null
    private var btnchktele_call: CheckBox? = null
    private var txtprivacy: TextView? = null
    private var txtterm: TextView? = null
    private var txtsetting: TextView? = null

    // Main layout elements
    private lateinit var viewPager: ViewPager
    private lateinit var myViewPagerAdapter: MyViewPagerAdapter
    private lateinit var dotsLayout: LinearLayout
    private lateinit var layouts: IntArray
    private lateinit var btnNext: Button
    private lateinit var dot1: ImageView
    private lateinit var dot2: ImageView
    private lateinit var dot3: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityWelcomeSyncContactKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager

        initWidgets()
        applyWindowInsets()
        setListener()

        myViewPagerAdapter = MyViewPagerAdapter(this)
        viewPager.adapter = myViewPagerAdapter

        // Restore page index and agreement checkbox state on recreation without auto-proceeding
        if (savedInstanceState != null) {
            current = savedInstanceState.getInt(KEY_CURRENT_PAGE, 0)
            val wasAgreeChecked = savedInstanceState.getBoolean(KEY_AGREE_CHECKED, false)
            if (wasAgreeChecked) {
                viewModel.onCheckboxToggled(
                    isSms = viewModel.uiState.value.isSmsChecked,
                    isCall = viewModel.uiState.value.isCallChecked,
                    isAgree = true
                )
            }
        } else {
            current = 0
        }

        setupPageChangeListener()

        if (current in layouts.indices) {
            viewPager.currentItem = current
            updateUiForPage(current)
        }

        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_PAGE, viewPager.currentItem)
        outState.putBoolean(KEY_AGREE_CHECKED, btnchkagree?.isChecked == true)
    }

    private fun initWidgets() {
        dot1 = binding.dot1
        dot2 = binding.dot2
        dot3 = binding.dot3

        layouts = intArrayOf(
            R.layout.sync_welcome_slide1,
            R.layout.sync_welcome_slide2,
            R.layout.sync_welcome_slide3,
            R.layout.sync_welcome_slide4
        )

        dotsLayout = binding.layoutDots
        btnNext = binding.btnNext
        btnNext.tag = 1

        // Record initial XML paddings so they are never overwritten or accumulated
        baseMainContentPaddingLeft = binding.mainContent.paddingLeft
        baseMainContentPaddingTop = binding.mainContent.paddingTop
        baseMainContentPaddingRight = binding.mainContent.paddingRight
        baseMainContentPaddingBottom = binding.mainContent.paddingBottom

        baseFooterPaddingLeft = binding.lyFooter.paddingLeft
        baseFooterPaddingTop = binding.lyFooter.paddingTop
        baseFooterPaddingRight = binding.lyFooter.paddingRight
        baseFooterPaddingBottom = binding.lyFooter.paddingBottom
    }

    /**
     * Applies system window insets safely without double-padding or layout jumps.
     * Status bar & camera cutout insets go to mainContent; navigation bar & gesture bar go to lyFooter.
     */
    private fun applyWindowInsets() {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // Safe Area - Top: status bar & camera cutout padding applied to mainContent
            binding.mainContent.setPadding(
                baseMainContentPaddingLeft + insets.left,
                baseMainContentPaddingTop + insets.top,
                baseMainContentPaddingRight + insets.right,
                baseMainContentPaddingBottom
            )

            // Safe Area - Bottom: navigation bar / gesture navigation pill padding applied to lyFooter
            binding.lyFooter.setPadding(
                baseFooterPaddingLeft + insets.left,
                baseFooterPaddingTop,
                baseFooterPaddingRight + insets.right,
                baseFooterPaddingBottom + insets.bottom
            )

            windowInsets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun setListener() {
        btnNext.setOnClickListener(this)
    }

    private fun setupPageChangeListener() {
        onPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                updateUiForPage(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }
        viewPager.addOnPageChangeListener(onPageChangeListener)
    }

    private fun updateUiForPage(position: Int) {
        current = position
        setSelectedDot(position + 1)

        if (position == 3) {
            // Slide 4: Control enabled state strictly based on agreement checkbox
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.footer_blue))
            binding.lyFooter.setBackgroundColor(ContextCompat.getColor(this, R.color.footer_blue))
            dotsLayout.visibility = View.GONE

            btnNext.text = "GET STARTED"
            val isChecked = btnchkagree?.isChecked == true
            val canProceed = isChecked && !viewModel.uiState.value.isLoading && !viewModel.uiState.value.isSaving
            btnNext.isEnabled = canProceed
            btnNext.alpha = if (canProceed) 1.0f else 0.4f
            btnNext.tag = 0

            // Auto-scroll to bottom of Slide 4 after a short delay
            viewPager.postDelayed({
                val scrollView = viewPager.findViewById<NestedScrollView>(R.id.slide4ScrollView)
                scrollView?.fullScroll(View.FOCUS_DOWN)
            }, 150)
        } else {
            // Slides 1, 2, 3: Button must always be ENABLED
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.lyFooter.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            dotsLayout.visibility = View.VISIBLE

            btnNext.text = "NEXT"
            btnNext.isEnabled = true
            btnNext.alpha = 1.0f
            btnNext.tag = 1
        }
    }

    private fun setSelectedDot(currentDot: Int) {
        dot1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unselected_dot))
        dot2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unselected_dot))
        dot3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unselected_dot))

        when (currentDot) {
            1 -> dot1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active))
            2 -> dot2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active))
            3, 4 -> dot3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        if (state.isLoading || state.isSaving) {
                            showAnimDialog("Please Wait...")
                        } else {
                            cancelAnimDialog()
                        }

                        // Pre-populate checkboxes if loaded from existing agreement preferences
                        if (state.isSmsChecked && btnchkcommunication_sms?.isChecked != true) {
                            btnchkcommunication_sms?.isChecked = true
                        }
                        if (state.isCallChecked && btnchktele_call?.isChecked != true) {
                            btnchktele_call?.isChecked = true
                        }

                        // Re-evaluate button state on Slide 4
                        if (viewPager.currentItem == 3) {
                            val isChecked = btnchkagree?.isChecked == true
                            val canProceed = isChecked && !state.isLoading && !state.isSaving
                            btnNext.isEnabled = canProceed
                            btnNext.alpha = if (canProceed) 1.0f else 0.4f
                        }
                    }
                }

                launch {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is WelcomeSyncContactEvent.NavigateToMain -> {
                                navigateToMainActivitySyncContact()
                            }
                            is WelcomeSyncContactEvent.ShowMessage -> {
                                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                                // Re-enable button on failure if on Slide 4
                                if (viewPager.currentItem == 3) {
                                    val isChecked = btnchkagree?.isChecked == true
                                    btnNext.isEnabled = isChecked
                                    btnNext.alpha = if (isChecked) 1.0f else 0.4f
                                }
                            }
                            is WelcomeSyncContactEvent.RequestPermissions -> {
                                // Handled on destination screen
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Executes single, idempotent navigation to MainActivity with DEST_SYNC_CONTACT extra.
     */
    private fun navigateToMainActivitySyncContact() {
        if (hasNavigated) return
        hasNavigated = true
        cancelAnimDialog()

        val intent = Intent(this@WelcomeSyncContactActivityKotlin, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_START_DESTINATION, MainActivity.DEST_SYNC_CONTACT)
        }
        startActivity(intent)
        finish()
    }

    /**
     * Invoked when user taps GET STARTED on Slide 4 with mandatory agreement checkbox checked.
     * Prevents duplicate requests and initiates ViewModel save.
     */
    private fun proceedToSyncContacts() {
        if (hasNavigated) return
        if (viewModel.uiState.value.isLoading || viewModel.uiState.value.isSaving) return

        // Must be on Slide 4 and mandatory agreement checkbox must be checked
        if (viewPager.currentItem != 3 || btnchkagree?.isChecked != true) {
            return
        }

        // Disable button immediately to prevent double-tap
        btnNext.isEnabled = false
        btnNext.alpha = 0.4f

        val smsChecked = btnchkcommunication_sms?.isChecked == true
        val callChecked = btnchktele_call?.isChecked == true

        viewModel.saveAgreements(isSms = smsChecked, isCall = callChecked)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            btnNext.id -> {
                val nextPosition = viewPager.currentItem + 1
                if (nextPosition < layouts.size) {
                    current = nextPosition
                    viewPager.setCurrentItem(nextPosition, true)
                } else {
                    proceedToSyncContacts()
                }
            }

            txtprivacy?.id -> {
                openWebUrl("https://www.policyboss.com/privacy-policy-policyboss-pro")
            }

            txtterm?.id -> {
                openWebUrl("https://www.policyboss.com/terms-condition")
            }

            txtsetting?.id -> {
                openAppSettings(this)
            }
        }
    }

    private fun openWebUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Unable to open link", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showAnimDialog(msg: String? = "Please Wait...") {
        try {
            if (isFinishing || isDestroyed) return
            if (dialog == null) {
                dialog = Dialog(this).apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setContentView(R.layout.progressdialog2_loading)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setCancelable(false)
                }
            }
            dialog?.findViewById<TextView>(R.id.txtMessage)?.text = msg
            if (dialog?.isShowing == false) {
                dialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelAnimDialog() {
        try {
            if (dialog?.isShowing == true && !isFinishing && !isDestroyed) {
                dialog?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            dialog = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAnimDialog()
        if (::onPageChangeListener.isInitialized) {
            viewPager.removeOnPageChangeListener(onPageChangeListener)
        }
    }

    inner class MyViewPagerAdapter(private val context: Context) : PagerAdapter() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(layouts[position], container, false)

            if (position == 3) {
                btnchkagree = view.findViewById(R.id.chkagree)
                btnchkcommunication_sms = view.findViewById(R.id.chkcommunication_sms)
                btnchktele_call = view.findViewById(R.id.chktele_call)
                txtterm = view.findViewById(R.id.txtterm)
                txtprivacy = view.findViewById(R.id.txtprivacy)
                txtsetting = view.findViewById(R.id.txtsetting)
                txtsetting?.text = HtmlCompat.fromHtml(
                    getString(R.string.sync_setting_text),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                val tvClickHere = view.findViewById<TextView>(R.id.tvClickHere)

                txtterm?.setOnClickListener(this@WelcomeSyncContactActivityKotlin)
                txtprivacy?.setOnClickListener(this@WelcomeSyncContactActivityKotlin)
                txtsetting?.setOnClickListener(this@WelcomeSyncContactActivityKotlin)

                tvClickHere?.setOnClickListener {
                    openWebUrl("https://www.policyboss.com/sync-contacts-info")
                }

                // Initialize checkbox values from current ViewModel state
                val currentState = viewModel.uiState.value
                btnchkcommunication_sms?.isChecked = currentState.isSmsChecked
                btnchktele_call?.isChecked = currentState.isCallChecked
                btnchkagree?.isChecked = currentState.isAgreeChecked

                // Update button enabled state strictly based on mandatory agreement checkbox
                btnchkagree?.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.onCheckboxToggled(
                        isSms = btnchkcommunication_sms?.isChecked == true,
                        isCall = btnchktele_call?.isChecked == true,
                        isAgree = isChecked
                    )
                    if (viewPager.currentItem == 3) {
                        val canProceed = isChecked && !viewModel.uiState.value.isLoading && !viewModel.uiState.value.isSaving
                        btnNext.isEnabled = canProceed
                        btnNext.alpha = if (canProceed) 1.0f else 0.4f
                    }
                }

                val communicationListener = {
                    viewModel.onCheckboxToggled(
                        isSms = btnchkcommunication_sms?.isChecked == true,
                        isCall = btnchktele_call?.isChecked == true,
                        isAgree = btnchkagree?.isChecked == true
                    )
                }
                btnchkcommunication_sms?.setOnCheckedChangeListener { _, _ -> communicationListener() }
                btnchktele_call?.setOnCheckedChangeListener { _, _ -> communicationListener() }
            }

            container.addView(view)
            return view
        }

        override fun getCount(): Int = layouts.size

        override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    companion object {
        private const val KEY_CURRENT_PAGE = "key_current_page"
        private const val KEY_AGREE_CHECKED = "key_agree_checked"
    }
}
