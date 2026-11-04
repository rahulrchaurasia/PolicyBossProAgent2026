package com.policyboss.policybosspro.feature.dummyData

import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.grow.model.GrowArticleItem
import com.policyboss.policybosspro.feature.grow.model.GrowBannerItem
import com.policyboss.policybosspro.feature.grow.model.GrowContestItem
import com.policyboss.policybosspro.feature.grow.model.GrowGreetingItem
import com.policyboss.policybosspro.feature.grow.model.GrowLeadFormItem
import com.policyboss.policybosspro.feature.grow.model.GrowPosterItem
import com.policyboss.policybosspro.feature.grow.model.GrowTemplateItem
import com.policyboss.policybosspro.feature.grow.model.KnowledgeCategory
import com.policyboss.policybosspro.feature.home.model.home.EarningsSummary
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.HomeMetric
import com.policyboss.policybosspro.feature.home.model.home.InsurerQuote
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.feature.home.model.home.QuickLink
import com.policyboss.policybosspro.feature.home.model.home.TrustBulletPoint
import com.policyboss.policybosspro.feature.home.model.menu.MenuProductItem
import com.policyboss.policybosspro.feature.home.model.menu.QuoteTabItem

object AppDummyData {

    val menuProducts = listOf(
        MenuProductItem("Car", R.drawable.ic_car),
        MenuProductItem("TW", R.drawable.ic_bike),
        MenuProductItem("CV", R.drawable.ic_cv),
        MenuProductItem("Travel", R.drawable.ic_travel),
        MenuProductItem("Health", R.drawable.ic_health, isComingSoon = true),
        MenuProductItem("Life", R.drawable.ic_life, isComingSoon = true)
    )

    val quoteVehicleTabs = listOf(
        QuoteTabItem("Car", R.drawable.ic_car),
        QuoteTabItem("TW", R.drawable.ic_bike),
        QuoteTabItem("CV", R.drawable.ic_cv),
        QuoteTabItem("Travel", R.drawable.ic_travel, isComingSoon = true),
        QuoteTabItem("Health", R.drawable.ic_heart, isComingSoon = true),
        QuoteTabItem("Life", R.drawable.ic_life, isComingSoon = true)
    )

    val earningsSummary = EarningsSummary(
        thisMonthEarnings = "₹20,000",
        potentialEarnings = "₹1,20,000"
    )

    val promoBanners = listOf(
        PromoBanner(
            id = "banner_1",
            tag = "START EARNING",
            title = "Ready to kick-start your business? Sell your first policy today!",
            buttonText = "Sell Policy",
            earningTag = "₹ 8,00,000",
            imageRes = R.drawable.ic_sellpolicy
        ),
        PromoBanner(
            id = "banner_2",
            tag = "GROW YOUR NETWORK",
            title = "Invite sub-agents and earn continuous lifetime referral commissions!",
            buttonText = "Invite Agents",
            earningTag = "₹ 15,00,000",
            imageRes = R.drawable.ic_avatar2
        ),
        PromoBanner(
            id = "banner_3",
            tag = "EXCLUSIVE REWARDS",
            title = "Complete monthly sales targets and unlock exciting cash bonuses!",
            buttonText = "View Targets",
            earningTag = "₹ 50,000 Bonus",
            imageRes = R.drawable.ic_avatar3
        )
    )

    val homeMetrics = listOf(
        HomeMetric(
            id = "metric_expiring",
            value = "06",
            label = "Policy expiring in 7 days",
            isAlert = true
        ),
        HomeMetric(
            id = "metric_sold",
            value = "24",
            label = "Policies sold this month",
            isAlert = false
        )
    )

    val sampleQuotes = listOf(
        InsurerQuote(
            id = "quote_1",
            insurerName = "Acko General Insurance",
            logoRes = R.drawable.ic_acko_logo,
            premium = "₹6,100",
            idv = "₹11,50,241",
            addons = listOf("Zero Depreciation", "Roadside Assistance", "NCB Protect, +5 more"),
            brokerage = "₹ 1,403"
        ),
        InsurerQuote(
            id = "quote_2",
            insurerName = "Tata AIG General Insurance",
            logoRes = R.drawable.img_tata,
            premium = "₹6,403",
            idv = "₹11,80,000",
            addons = listOf("Zero Depreciation", "Engine Protection", "Consumables Cover"),
            brokerage = "₹ 1,520"
        ),
        InsurerQuote(
            id = "quote_3",
            insurerName = "HDFC ERGO General",
            logoRes = R.drawable.ic_acko_logo,
            premium = "₹6,850",
            idv = "₹12,10,000",
            addons = listOf("Zero Depreciation", "Key Replacement", "Return to Invoice"),
            brokerage = "₹ 1,650"
        )
    )

    val topLeads = listOf(
        HomeLead(
            id = "lead_1",
            name = "Nida Husaini",
            phoneNumber = "9820123456",
            leadType = "Renewal",
            isTopLead = true,
            vehicleType = "Car",
            vehicleModel = "Maruti Suzuki Baleno",
            registrationNumber = "MH-047-219",
            expiryText = "Expiring in 3d: 21 May, 26",
            expiryBadge = "Expires in 3 days",
            specialDealsCount = 3,
            hasSyncReward = false,
            quotesCount = 16,
            quotesFoundCount = 8,
            minPremium = "₹6,403",
            maxBrokerage = "₹2,500",
            status = "New",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_2",
            name = "Varun Kaushik",
            phoneNumber = "9819876543",
            leadType = "Sync",
            isTopLead = true,
            vehicleType = "Car",
            vehicleModel = "Hyundai Creta SX",
            registrationNumber = "DL-01-AB-1234",
            expiryText = "Expiring in 3d: 21 May, 26",
            specialDealsCount = 3,
            hasSyncReward = true,
            quotesCount = 16,
            quotesFoundCount = 8,
            minPremium = "₹7,200",
            maxBrokerage = "₹2,800",
            brokerageAmount = "₹2,800",
            status = "In Progress",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_3",
            name = "Pushpak Pawle",
            phoneNumber = "9876543210",
            leadType = "Sync",
            isTopLead = true,
            vehicleType = "Two Wheeler",
            vehicleModel = "Honda Activa 6G",
            registrationNumber = "MH-02-XY-9876",
            expiryText = "Expiring in 7d: 25 May, 26",
            specialDealsCount = 2,
            hasSyncReward = true,
            quotesCount = 12,
            quotesFoundCount = 6,
            minPremium = "₹1,850",
            maxBrokerage = "₹650",
            brokerageAmount = "₹650",
            status = "New",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_4",
            name = "Rohan Sharma",
            phoneNumber = "9988776655",
            leadType = "Renewal",
            isTopLead = false,
            vehicleType = "Two Wheeler",
            vehicleModel = "Royal Enfield Classic 350",
            registrationNumber = "KA-05-MN-4321",
            expiryText = "Expiring in 9d: 27 May, 26",
            expiryBadge = "Expires in 9 days",
            specialDealsCount = 4,
            hasSyncReward = false,
            quotesCount = 14,
            quotesFoundCount = 7,
            minPremium = "₹2,400",
            maxBrokerage = "₹850",
            status = "In Progress",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_5",
            name = "Amitabh Joshi",
            phoneNumber = "9822334455",
            leadType = "Renewal",
            isTopLead = false,
            vehicleType = "Commercial Vehicle",
            vehicleModel = "Tata Ace Gold",
            registrationNumber = "GJ-01-CV-7890",
            expiryText = "Expiring in 21d: 08 Jun, 26",
            expiryBadge = "Expires in 21 days",
            specialDealsCount = 2,
            hasSyncReward = false,
            quotesCount = 8,
            quotesFoundCount = 4,
            minPremium = "₹9,500",
            maxBrokerage = "₹3,200",
            status = "Won",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_6",
            name = "Sneha Kulkarni",
            phoneNumber = "9765432109",
            leadType = "Added by me",
            isTopLead = false,
            vehicleType = "Car",
            vehicleModel = "Kia Seltos HTX",
            registrationNumber = "MH-12-PQ-5544",
            expiryText = "Expiring in 10d: 28 May, 26",
            specialDealsCount = 3,
            hasSyncReward = false,
            quotesCount = 15,
            quotesFoundCount = 8,
            minPremium = "₹8,100",
            maxBrokerage = "₹3,000",
            status = "New",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_7",
            name = "Rajesh Gupta",
            phoneNumber = "9654321987",
            leadType = "Added by me",
            isTopLead = false,
            vehicleType = "Two Wheeler",
            vehicleModel = "TVS Jupiter 125",
            registrationNumber = "UP-32-AB-8899",
            expiryText = "Expiring in 14d: 01 Jun, 26",
            specialDealsCount = 1,
            hasSyncReward = false,
            quotesCount = 10,
            quotesFoundCount = 5,
            minPremium = "₹1,600",
            maxBrokerage = "₹550",
            status = "In Progress",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_8",
            name = "Vikram Malhotra",
            phoneNumber = "9543219876",
            leadType = "Added by me",
            isTopLead = false,
            vehicleType = "Commercial Vehicle",
            vehicleModel = "Mahindra Bolero Maxi Truck",
            registrationNumber = "RJ-14-CC-3322",
            expiryText = "Expiring in 25d: 12 Jun, 26",
            specialDealsCount = 2,
            hasSyncReward = false,
            quotesCount = 6,
            quotesFoundCount = 3,
            minPremium = "₹11,200",
            maxBrokerage = "₹3,800",
            status = "Won",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_9",
            name = "Priya Nair",
            phoneNumber = "9432198765",
            leadType = "Sync",
            isTopLead = false,
            vehicleType = "Commercial Vehicle",
            vehicleModel = "Eicher Pro 2049",
            registrationNumber = "KL-07-ZZ-1100",
            expiryText = "Expiring in 2d: 20 May, 26",
            specialDealsCount = 1,
            hasSyncReward = false,
            quotesCount = 5,
            quotesFoundCount = 2,
            minPremium = "₹14,500",
            maxBrokerage = "₹4,500",
            status = "Lost",
            quotes = sampleQuotes
        )
    )

    val renewalLeads = listOf(
        HomeLead(
            id = "lead_renewal_1",
            name = "Nida Husaini",
            leadType = "Renewal",
            isTopLead = false,
            expiryBadge = "Expires in 3 days",
            vehicleType = "Car",
            vehicleModel = "Maruti Suzuki Baleno",
            expiryText = "Expiring in 3d: 21 May, 26",
            specialDealsCount = 3,
            hasSyncReward = false,
            quotesCount = 16,
            quotesFoundCount = 8,
            minPremium = "₹6,403",
            maxBrokerage = "₹2,500",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_renewal_2",
            name = "Nida Husaini",
            leadType = "Renewal",
            isTopLead = false,
            expiryBadge = "Expires in 3 days",
            vehicleType = "Car",
            vehicleModel = "Maruti Suzuki Baleno",
            expiryText = "Expiring in 3d: 21 May, 26",
            specialDealsCount = 3,
            hasSyncReward = false,
            quotesCount = 16,
            quotesFoundCount = 8,
            minPremium = "₹6,403",
            maxBrokerage = "₹2,500",
            quotes = sampleQuotes
        ),
        HomeLead(
            id = "lead_renewal_3",
            name = "Aakash Verma",
            leadType = "Renewal",
            isTopLead = false,
            expiryBadge = "Expires in 3 days",
            vehicleType = "Car",
            vehicleModel = "Hyundai Creta SX",
            expiryText = "Expiring in 2d: 20 May, 26",
            specialDealsCount = 2,
            hasSyncReward = false,
            quotesCount = 12,
            quotesFoundCount = 6,
            minPremium = "₹7,200",
            maxBrokerage = "₹2,800",
            quotes = sampleQuotes
        )
    )

    val quickLinks = listOf(
        QuickLink(
            id = "dashboard",
            title = "Dashboard",
            iconRes = R.drawable.ic_dashboard4
        ),
        QuickLink(
            id = "claim_assistance",
            title = "Claim Assistance",
            iconRes = R.drawable.ic_claim_support
        ),
        QuickLink(
            id = "get_help",
            title = "Get Help",
            iconRes = R.drawable.ic_headset
        )
    )

    val trustBullets = listOf(
        TrustBulletPoint("bullet_1", "8+ yrs in the insurance, 1L+ happy users"),
        TrustBulletPoint("bullet_2", "Unbiased comparisons, transparent advisory"),
        TrustBulletPoint("bullet_3", "Associated with 50+ insurers")
    )

    val partnerLogos = listOf(
        R.drawable.ic_footer1,
        R.drawable.ic_footer2,
        R.drawable.ic_acko_logo,
        R.drawable.ic_footer3,
        R.drawable.img_tata,
        R.drawable.ic_footer4,
        R.drawable.ic_footer5,
        R.drawable.ic_footer6
    )

    val salesPolicies = listOf(
        com.policyboss.policybosspro.feature.sales.model.SalePolicy(
            id = "19251610",
            customerName = "Rajesh Chadha",
            customerBadge = "Friend",
            insuranceCategory = "Car Insurance",
            vehicleModel = "HONDA CITY INDIA (P) LTD: AC...",
            detailsVehicleModel = "Maruti Suzuki Baleno",
            agentName = "Rashmi Shah",
            subUserName = "Mukesh Agarwal",
            premiumAmount = "₹6,500",
            bossCoins = "25+",
            totalEarnings = "1,403",
            standardBrokerage = "₹1,000",
            specialDeal = "₹103",
            specialDealBy = "Special Deal By HDFC",
            extraSyncEarnings = "₹200",
            zaxtraEarnings = "₹100 ↑",
            customerDetailsId = "19251610",
            insuredDetails = com.policyboss.policybosspro.feature.sales.model.InsuredDetails(
                title = "YR-2025::SAOD::TVS::NTORQ::KL21::KERALA",
                vahanData = "HERO HONDA MOTORS LTD::SPLENDOR PLUS::PETROL::100CC",
                quoteOn = "17-Aug-26 13:02",
                saleOn = "17-Aug-26 13:10",
                crn = "12078562",
                cs = "CS3549723",
                zaxtra = "NA",
                premiumLevel = "Level 1",
                brokerageLevel = "Level 1",
                saleDuration = "7 Minute",
                sync = "No",
                renew = "YES",
                source = "POSP-POSP-AGENCY",
                insurer = "Edelweiss",
                customer = "Suresh Bandhaiya",
                reporting = "Umesh Pawar Uid-120572",
                ssId = "122378"
            )
        ),
        com.policyboss.policybosspro.feature.sales.model.SalePolicy(
            id = "19251611",
            customerName = "Rajesh Chadha",
            customerBadge = "Friend",
            insuranceCategory = "Car Insurance",
            vehicleModel = "HONDA CITY INDIA (P) LTD: AC...",
            detailsVehicleModel = "Honda City V Petrol",
            agentName = "Rashmi Shah",
            subUserName = "Mukesh Agarwal",
            premiumAmount = "₹6,500",
            bossCoins = "25+",
            totalEarnings = "1,403",
            standardBrokerage = "₹1,000",
            specialDeal = null,
            specialDealBy = null,
            extraSyncEarnings = "₹200",
            zaxtraEarnings = null,
            customerDetailsId = "19251611",
            insuredDetails = com.policyboss.policybosspro.feature.sales.model.InsuredDetails(
                title = "YR-2024::COMPREHENSIVE::HONDA::CITY::MH02::MUMBAI",
                vahanData = "HONDA CARS INDIA LTD::HONDA CITY 1.5 V::PETROL::1498CC",
                quoteOn = "15-Aug-26 11:20",
                saleOn = "15-Aug-26 11:45",
                crn = "12078563",
                cs = "CS3549724",
                zaxtra = "NA",
                premiumLevel = "Level 1",
                brokerageLevel = "Level 1",
                saleDuration = "25 Minute",
                sync = "Yes",
                renew = "NO",
                source = "POSP-DIRECT",
                insurer = "HDFC ERGO",
                customer = "Rajesh Chadha",
                reporting = "Umesh Pawar Uid-120572",
                ssId = "122378"
            )
        ),
        com.policyboss.policybosspro.feature.sales.model.SalePolicy(
            id = "19251612",
            customerName = "Rajesh Chadha",
            customerBadge = "Friend",
            insuranceCategory = "Car Insurance",
            vehicleModel = "HONDA CITY INDIA (P) LTD: AC...",
            detailsVehicleModel = "Hyundai i20 Asta",
            agentName = "Rashmi Shah",
            subUserName = "Mukesh Agarwal",
            premiumAmount = "₹6,500",
            bossCoins = "25+",
            totalEarnings = "1,403",
            standardBrokerage = "₹1,000",
            specialDeal = "₹103",
            specialDealBy = "Special Deal By HDFC",
            extraSyncEarnings = "₹200",
            zaxtraEarnings = "₹100 ↑",
            customerDetailsId = "19251612",
            insuredDetails = com.policyboss.policybosspro.feature.sales.model.InsuredDetails(
                title = "YR-2025::SAOD::HYUNDAI::I20::DL01::DELHI",
                vahanData = "HYUNDAI MOTOR INDIA LTD::I20 SPORTZ::PETROL::1197CC",
                quoteOn = "12-Aug-26 16:15",
                saleOn = "12-Aug-26 16:30",
                crn = "12078564",
                cs = "CS3549725",
                zaxtra = "NA",
                premiumLevel = "Level 2",
                brokerageLevel = "Level 1",
                saleDuration = "15 Minute",
                sync = "No",
                renew = "YES",
                source = "POSP-POSP-AGENCY",
                insurer = "Tata AIG",
                customer = "Rajesh Chadha",
                reporting = "Umesh Pawar Uid-120572",
                ssId = "122378"
            )
        ),
        com.policyboss.policybosspro.feature.sales.model.SalePolicy(
            id = "19251613",
            customerName = "Rajesh Chadha",
            customerBadge = "Friend",
            insuranceCategory = "Car Insurance",
            vehicleModel = "HONDA CITY INDIA (P) LTD: AC...",
            detailsVehicleModel = "Maruti Suzuki Swift",
            agentName = "Rashmi Shah",
            subUserName = "Mukesh Agarwal",
            premiumAmount = "₹6,500",
            bossCoins = "25+",
            totalEarnings = "1,403",
            standardBrokerage = "₹1,000",
            specialDeal = "₹103",
            specialDealBy = "Special Deal By HDFC",
            extraSyncEarnings = "₹200",
            zaxtraEarnings = "₹100 ↑",
            customerDetailsId = "19251613",
            insuredDetails = com.policyboss.policybosspro.feature.sales.model.InsuredDetails(
                title = "YR-2025::SAOD::MARUTI::SWIFT::MH12::PUNE",
                vahanData = "MARUTI SUZUKI INDIA LTD::SWIFT VXI::PETROL::1197CC",
                quoteOn = "10-Aug-26 09:40",
                saleOn = "10-Aug-26 09:55",
                crn = "12078565",
                cs = "CS3549726",
                zaxtra = "NA",
                premiumLevel = "Level 1",
                brokerageLevel = "Level 1",
                saleDuration = "15 Minute",
                sync = "No",
                renew = "YES",
                source = "POSP-POSP-AGENCY",
                insurer = "Bajaj Allianz",
                customer = "Rajesh Chadha",
                reporting = "Umesh Pawar Uid-120572",
                ssId = "122378"
            )
        ),
        com.policyboss.policybosspro.feature.sales.model.SalePolicy(
            id = "19251614",
            customerName = "Ananya Singh",
            customerBadge = "Client",
            insuranceCategory = "Two Wheeler Insurance",
            vehicleModel = "TVS JUPITER 125 CC...",
            detailsVehicleModel = "TVS Jupiter ZX Disc",
            agentName = "Arjun Mehta",
            subUserName = "Gopal Verma",
            premiumAmount = "₹2,450",
            bossCoins = "15+",
            totalEarnings = "650",
            standardBrokerage = "₹450",
            specialDeal = "₹50",
            specialDealBy = "Special Deal By ICICI",
            extraSyncEarnings = "₹150",
            zaxtraEarnings = null,
            customerDetailsId = "19251614",
            insuredDetails = com.policyboss.policybosspro.feature.sales.model.InsuredDetails(
                title = "YR-2025::SAOD::TVS::JUPITER::MH03::MUMBAI",
                vahanData = "TVS MOTOR COMPANY LTD::JUPITER::PETROL::124CC",
                quoteOn = "08-Aug-26 14:10",
                saleOn = "08-Aug-26 14:22",
                crn = "12078566",
                cs = "CS3549727",
                zaxtra = "NA",
                premiumLevel = "Level 1",
                brokerageLevel = "Level 1",
                saleDuration = "12 Minute",
                sync = "Yes",
                renew = "NO",
                source = "POSP-POSP-AGENCY",
                insurer = "ICICI Lombard",
                customer = "Ananya Singh",
                reporting = "Umesh Pawar Uid-120572",
                ssId = "122378"
            )
        ),
        com.policyboss.policybosspro.feature.sales.model.SalePolicy(
            id = "19251615",
            customerName = "Amit Patel",
            customerBadge = "VIP",
            insuranceCategory = "Health Insurance",
            vehicleModel = "FAMILY OPTIMA SECURE PLAN",
            detailsVehicleModel = "Family Floater 10 Lakhs",
            agentName = "Amit Patel",
            subUserName = "Ananya Singh",
            premiumAmount = "₹18,500",
            bossCoins = "50+",
            totalEarnings = "3,200",
            standardBrokerage = "₹2,500",
            specialDeal = "₹400",
            specialDealBy = "Care Special Bonus",
            extraSyncEarnings = "₹300",
            zaxtraEarnings = "₹150 ↑",
            customerDetailsId = "19251615",
            insuredDetails = com.policyboss.policybosspro.feature.sales.model.InsuredDetails(
                title = "YR-2025::HEALTH::CARE::OPTIMA::INDIVIDUAL",
                vahanData = "CARE HEALTH INSURANCE::OPTIMA SECURE::SUM INSURED 10L",
                quoteOn = "05-Aug-26 10:00",
                saleOn = "05-Aug-26 10:35",
                crn = "12078567",
                cs = "CS3549728",
                zaxtra = "NA",
                premiumLevel = "Level 1",
                brokerageLevel = "Level 2",
                saleDuration = "35 Minute",
                sync = "Yes",
                renew = "YES",
                source = "POSP-POSP-AGENCY",
                insurer = "Care Health",
                customer = "Amit Patel",
                reporting = "Umesh Pawar Uid-120572",
                ssId = "122378"
            )
        ),
        com.policyboss.policybosspro.feature.sales.model.SalePolicy(
            id = "19251616",
            customerName = "Vikram Joshi",
            customerBadge = "Friend",
            insuranceCategory = "Life Insurance",
            vehicleModel = "TERM LIFE PROTECT PLUS",
            detailsVehicleModel = "Term Life 1 Cr Cover",
            agentName = "Nitin Sharma",
            subUserName = "Ravi Kumar",
            premiumAmount = "₹14,200",
            bossCoins = "40+",
            totalEarnings = "2,840",
            standardBrokerage = "₹2,100",
            specialDeal = null,
            specialDealBy = null,
            extraSyncEarnings = "₹400",
            zaxtraEarnings = "₹340 ↑",
            customerDetailsId = "19251616",
            insuredDetails = com.policyboss.policybosspro.feature.sales.model.InsuredDetails(
                title = "YR-2025::LIFE::MAX::TERM::PURE_PROTECT",
                vahanData = "MAX LIFE INSURANCE::SMART SECURE PLUS::TERM COVER",
                quoteOn = "02-Aug-26 15:40",
                saleOn = "02-Aug-26 16:10",
                crn = "12078568",
                cs = "CS3549729",
                zaxtra = "NA",
                premiumLevel = "Level 1",
                brokerageLevel = "Level 1",
                saleDuration = "30 Minute",
                sync = "Yes",
                renew = "NO",
                source = "POSP-DIRECT",
                insurer = "Max Life",
                customer = "Vikram Joshi",
                reporting = "Umesh Pawar Uid-120572",
                ssId = "122378"
            )
        )
    )

    val filterProducts = listOf(
        "Car",
        "Two Wheeler",
        "CV",
        "Travel",
        "Health",
        "Life",
        "SME"
    )

    val filterSubUsers = listOf(
        "Gopal Verma",
        "Ananya Singh",
        "Ravi Kumar",
        "Sofia Patel",
        "Arjun Mehta",
        "Rajesh Shah",
        "Mukesh Agarwal"
    )

    val filterSubPosps = listOf(
        "Arjun Mehta",
        "Karan Singh",
        "Ravi Kumar",
        "Amit Patel",
        "Suresh Nair",
        "Deepak Reddy",
        "Nitin Sharma",
        "Vikram Joshi"
    )

    // ===================================================
    // GROW FEATURE DUMMY DATA
    // ===================================================

    val growBanner = GrowBannerItem(
        id = "banner_1",
        tag = "LEARN AND GROW",
        title = "How to grow your\nsales - Expert tips\nby PolicyBoss",
        buttonText = "Watch video",
        imageRes = R.drawable.banner_grow_business
    )

    val growContests = listOf(
        GrowContestItem(
            id = "contest_1",
            title = "Win a trip to Dubai",
            bannerRes = R.drawable.banner_grow_ongoing
        ),
        GrowContestItem(
            id = "contest_2",
            title = "Thailand Bonanza",
            bannerRes = R.drawable.banner_grow_ongoing
        ),
        GrowContestItem(
            id = "contest_3",
            title = "Goa Star Retreat",
            bannerRes = R.drawable.banner_grow_ongoing
        )
    )

    val growTemplates = listOf(
        GrowTemplateItem(
            id = "tpl_lead",
            title = "Lead Forms",
            imageRes = R.drawable.ic_grow_lead,
            isFeatured = true
        ),
        GrowTemplateItem(
            id = "tpl_greeting",
            title = "Greetings",
            imageRes = R.drawable.ic_grow_greeting,
            isFeatured = false
        ),
        GrowTemplateItem(
            id = "tpl_poster",
            title = "Daily posters",
            imageRes = R.drawable.ic_grow_daily_posture,
            isFeatured = false
        )
    )

    val growLeadForm = GrowLeadFormItem(
        id = "lead_form_1",
        title = "Zero Depreciation & Engine Protect.",
        imageRes = R.drawable.ic_grow_car
    )

    val growGreeting = GrowGreetingItem(
        id = "greeting_1",
        title = "Happy Birthday \uD83C\uDF82",
        subtitle = "Hi Everett! \uD83C\uDF89 Hope you have an amazing day ahead. May all your dreams come true.",
        imageRes = R.drawable.ic_grow_birthday
    )

    val growDailyPoster = GrowPosterItem(
        id = "poster_1",
        title = "Comprehensive Car Insurance",
        imageRes = R.drawable.ic_grow_daily_posture
    )

    val growArticles = listOf(
        GrowArticleItem(
            id = "art_1",
            readTime = "5min",
            title = "How to Sell Insurance to Millennials",
            category = KnowledgeCategory.INSIGHTS
        ),
       GrowArticleItem(
            id = "art_2",
            readTime = "20min",
            title = "Effective Strategies for Online Marketing Success",
            category = KnowledgeCategory.INSIGHTS
        ),
        GrowArticleItem(
            id = "art_3",
            readTime = "12min",
            title = "Navigating Financial Independence in Your 30s",
            category = KnowledgeCategory.INSIGHTS
        ),
        GrowArticleItem(
            id = "vid_1",
            readTime = "8min",
            title = "Pitching Motor Insurance Effectively",
            category = KnowledgeCategory.VIDEOS
        ),
        GrowArticleItem(
            id = "vid_2",
            readTime = "15min",
            title = "Closing Leads Faster with Digital Workflows",
            category = KnowledgeCategory.VIDEOS
        ),
        GrowArticleItem(
            id = "vid_3",
            readTime = "10min",
            title = "Understanding Add-on Covers in Detail",
            category = KnowledgeCategory.VIDEOS
        )
    )

    val defaultUserProfile = com.policyboss.policybosspro.feature.profile.model.UserProfile(
        fullName = "Dinesh Das",
        initials = "DD",
        accountDetails = com.policyboss.policybosspro.feature.profile.model.AccountDetails(
            ssId = "1449",
            plan = "NA",
            rmName = "Varun Kaushik",
            rmMobile = "99100 34425",
            rmEmail = "varun.kaushik@policyboss.com"
        ),
        displayInfo = com.policyboss.policybosspro.feature.profile.model.DisplayInformation(
            displayName = "Arvind Sharma",
            displayMobile = "99345 34252",
            displayEmail = "arvindsharma322@policyboss.com",
            displayPictureRes = R.drawable.avatar1
        ),
        personalInfo = com.policyboss.policybosspro.feature.profile.model.PersonalInformation(
            fullName = "Dinesh Das",
            mobile = "99100 34425",
            dob = "24 - 01 - 1988",
            email = "dineshdas@policyboss.com",
            pan = "HPFOO4389L",
            address = "12B/989, Poonam Nagar, Malad East",
            city = "Mumbai_G",
            state = "Maharastra",
            pincode = "400063"
        ),
        useSameAsPosp = false
    )

    object SyncContacts {
        const val AVG_EARNINGS = "₹1,00,000"
        const val EARNING_POTENTIAL = "₹1,25,000"
        const val BOSS_COINS = "5,000"
        const val DEFAULT_CONTACTS_COUNT = 0
    }
}
