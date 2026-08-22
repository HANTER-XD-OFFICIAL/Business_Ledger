package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val flag: String) {
    BENGALI("BN", "Bengali", "বাংলা", "🇧🇩"),
    ENGLISH("EN", "English", "English", "🇬🇧"),
    HINDI("HI", "Hindi", "हिन्दी", "🇮🇳"),
    ARABIC("AR", "Arabic", "العربية", "🇸🇦"),
    URDU("UR", "Urdu", "اردو", "🇵🇰");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: BENGALI
        }
    }
}

object AppStrings {
    fun get(key: String, lang: AppLanguage): String {
        val map = stringMap[key] ?: return key
        return map[lang] ?: map[AppLanguage.ENGLISH] ?: map[AppLanguage.BENGALI] ?: key
    }

    private val stringMap = mapOf(
        // App Info
        "app_name" to mapOf(
            AppLanguage.BENGALI to "Business Ledger",
            AppLanguage.ENGLISH to "Business Ledger",
            AppLanguage.HINDI to "बिजनेस लेज़र",
            AppLanguage.ARABIC to "دفتر الأعمال",
            AppLanguage.URDU to "بزنس لیجر"
        ),
        "app_tagline" to mapOf(
            AppLanguage.BENGALI to "দোকান ও ব্যবসার সম্পূর্ণ ডিজিটাল হিসাব খাতা",
            AppLanguage.ENGLISH to "Complete Digital Ledger for Shop & Business",
            AppLanguage.HINDI to "दुकान और व्यापार का संपूर्ण डिजिटल बहीखाता",
            AppLanguage.ARABIC to "دفتر الحسابات الرقمي الكامل للمتجر والأعمال",
            AppLanguage.URDU to "دکان اور کاروبار کا مکمل ڈیجیٹل حساب کتاب"
        ),

        // Navigation Tabs
        "nav_dashboard" to mapOf(
            AppLanguage.BENGALI to "ড্যাশবোর্ড",
            AppLanguage.ENGLISH to "Dashboard",
            AppLanguage.HINDI to "डैशबोर्ड",
            AppLanguage.ARABIC to "لوحة التحكم",
            AppLanguage.URDU to "ڈیش بورڈ"
        ),
        "nav_transactions" to mapOf(
            AppLanguage.BENGALI to "লেনদেন",
            AppLanguage.ENGLISH to "Transactions",
            AppLanguage.HINDI to "लेन-देन",
            AppLanguage.ARABIC to "المعاملات",
            AppLanguage.URDU to "لین دین"
        ),
        "nav_khata" to mapOf(
            AppLanguage.BENGALI to "খাতা/বাকি",
            AppLanguage.ENGLISH to "Ledger/Dues",
            AppLanguage.HINDI to "खाता/उधार",
            AppLanguage.ARABIC to "دفتر الديون",
            AppLanguage.URDU to "کھاتہ/ادھار"
        ),
        "nav_stock" to mapOf(
            AppLanguage.BENGALI to "স্টক/পণ্য",
            AppLanguage.ENGLISH to "Stock/Items",
            AppLanguage.HINDI to "स्टॉक/सामान",
            AppLanguage.ARABIC to "المخزون/السلع",
            AppLanguage.URDU to "اسٹاک/اشیاء"
        ),
        "nav_reports" to mapOf(
            AppLanguage.BENGALI to "রিপোর্ট",
            AppLanguage.ENGLISH to "Reports",
            AppLanguage.HINDI to "रिपोर्ट",
            AppLanguage.ARABIC to "التقارير",
            AppLanguage.URDU to "رپورٹس"
        ),
        "nav_settings" to mapOf(
            AppLanguage.BENGALI to "সেটিংস",
            AppLanguage.ENGLISH to "Settings",
            AppLanguage.HINDI to "सेटिंग्स",
            AppLanguage.ARABIC to "الإعدادات",
            AppLanguage.URDU to "ترتیبات"
        ),

        // Auth Screen
        "login_title" to mapOf(
            AppLanguage.BENGALI to "অ্যাকাউন্টে প্রবেশ করুন",
            AppLanguage.ENGLISH to "Sign In to Account",
            AppLanguage.HINDI to "खाते में प्रवेश करें",
            AppLanguage.ARABIC to "تسجيل الدخول إلى الحساب",
            AppLanguage.URDU to "اکاؤنٹ میں داخل ہوں"
        ),
        "register_title" to mapOf(
            AppLanguage.BENGALI to "নতুন অ্যাকাউন্ট তৈরি করুন",
            AppLanguage.ENGLISH to "Create New Account",
            AppLanguage.HINDI to "नया खाता बनाएं",
            AppLanguage.ARABIC to "إنشاء حساب جديد",
            AppLanguage.URDU to "نیا اکاؤنٹ بنائیں"
        ),
        "sign_in_google" to mapOf(
            AppLanguage.BENGALI to "Google অ্যাকাউন্ট দিয়ে প্রবেশ",
            AppLanguage.ENGLISH to "Continue with Google",
            AppLanguage.HINDI to "Google से जारी रखें",
            AppLanguage.ARABIC to "المتابعة باستخدام Google",
            AppLanguage.URDU to "Google کے ساتھ جاری رکھیں"
        ),
        "or_use_phone" to mapOf(
            AppLanguage.BENGALI to "অথবা মোবাইল নাম্বার দিয়ে",
            AppLanguage.ENGLISH to "OR WITH PHONE NUMBER",
            AppLanguage.HINDI to "या फोन नंबर के साथ",
            AppLanguage.ARABIC to "أو باستخدام رقم الهاتف",
            AppLanguage.URDU to "یا فون نمبر کے ساتھ"
        ),
        "login_tab" to mapOf(
            AppLanguage.BENGALI to "লগইন",
            AppLanguage.ENGLISH to "Login",
            AppLanguage.HINDI to "लॉगिन",
            AppLanguage.ARABIC to "تسجيل الدخول",
            AppLanguage.URDU to "لاگ ان"
        ),
        "register_tab" to mapOf(
            AppLanguage.BENGALI to "রেজিস্টার",
            AppLanguage.ENGLISH to "Register",
            AppLanguage.HINDI to "रजिस्टर",
            AppLanguage.ARABIC to "إنشاء حساب",
            AppLanguage.URDU to "رجسٹر"
        ),
        "full_name" to mapOf(
            AppLanguage.BENGALI to "আপনার নাম",
            AppLanguage.ENGLISH to "Full Name",
            AppLanguage.HINDI to "आपका नाम",
            AppLanguage.ARABIC to "الاسم الكامل",
            AppLanguage.URDU to "مکمل نام"
        ),
        "shop_name" to mapOf(
            AppLanguage.BENGALI to "দোকান / ব্যবসার নাম",
            AppLanguage.ENGLISH to "Shop / Business Name",
            AppLanguage.HINDI to "दुकान / व्यापार का नाम",
            AppLanguage.ARABIC to "اسم المتجر / العمل",
            AppLanguage.URDU to "دکان / کاروبار کا نام"
        ),
        "phone_or_email" to mapOf(
            AppLanguage.BENGALI to "মোবাইল নাম্বার বা ইমেইল",
            AppLanguage.ENGLISH to "Phone Number or Email",
            AppLanguage.HINDI to "फ़ोन नंबर या ईमेल",
            AppLanguage.ARABIC to "رقم الهاتف أو البريد",
            AppLanguage.URDU to "فون نمبر یا ای میل"
        ),
        "password" to mapOf(
            AppLanguage.BENGALI to "পাসওয়ার্ড",
            AppLanguage.ENGLISH to "Password",
            AppLanguage.HINDI to "पासवर्ड",
            AppLanguage.ARABIC to "كلمة المرور",
            AppLanguage.URDU to "پاس ورڈ"
        ),
        "confirm_password" to mapOf(
            AppLanguage.BENGALI to "পাসওয়ার্ড নিশ্চিত করুন",
            AppLanguage.ENGLISH to "Confirm Password",
            AppLanguage.HINDI to "पासवर्ड की पुष्टि करें",
            AppLanguage.ARABIC to "تأكيد كلمة المرور",
            AppLanguage.URDU to "پاس ورڈ کی تصدیق کریں"
        ),
        "login_button" to mapOf(
            AppLanguage.BENGALI to "লগইন করুন",
            AppLanguage.ENGLISH to "Sign In",
            AppLanguage.HINDI to "लॉगिन करें",
            AppLanguage.ARABIC to "دخول",
            AppLanguage.URDU to "لاگ ان کریں"
        ),
        "register_button" to mapOf(
            AppLanguage.BENGALI to "অ্যাকাউন্ট তৈরি করুন",
            AppLanguage.ENGLISH to "Create Account",
            AppLanguage.HINDI to "खाता बनाएं",
            AppLanguage.ARABIC to "إنشاء الحساب",
            AppLanguage.URDU to "اکاؤنٹ بنائیں"
        ),
        "guest_mode" to mapOf(
            AppLanguage.BENGALI to "ডেমো হিসেবে ব্যবহার করুন",
            AppLanguage.ENGLISH to "Continue as Guest / Demo",
            AppLanguage.HINDI to "अतिथि / डेमो के रूप में जारी रखें",
            AppLanguage.ARABIC to "المتابعة كضيف / تجريبي",
            AppLanguage.URDU to "بطور گیسٹ / ڈیمو جاری رکھیں"
        ),
        "google_select_account" to mapOf(
            AppLanguage.BENGALI to "Google অ্যাকাউন্ট নির্বাচন করুন",
            AppLanguage.ENGLISH to "Select Google Account",
            AppLanguage.HINDI to "Google खाता चुनें",
            AppLanguage.ARABIC to "اختر حساب Google",
            AppLanguage.URDU to "Google اکاؤنٹ منتخب کریں"
        ),
        "logged_in_as" to mapOf(
            AppLanguage.BENGALI to "লগইন কৃত অ্যাকাউন্ট:",
            AppLanguage.ENGLISH to "Signed in as:",
            AppLanguage.HINDI to "लॉगिन किया गया खाता:",
            AppLanguage.ARABIC to "مسجل الدخول كـ:",
            AppLanguage.URDU to "لاگ ان بطور:"
        ),
        "logout" to mapOf(
            AppLanguage.BENGALI to "লগআউট",
            AppLanguage.ENGLISH to "Log Out",
            AppLanguage.HINDI to "लॉगआउट",
            AppLanguage.ARABIC to "تسجيل الخروج",
            AppLanguage.URDU to "لاگ آؤٹ"
        ),
        "logout_confirm" to mapOf(
            AppLanguage.BENGALI to "আপনি কি নিশ্চিতভাবে এই অ্যাকাউন্ট থেকে লগআউট করতে চান? আপনার সংরক্ষিত ডাটা সুরক্ষিত থাকবে এবং পরবর্তীতে লগইন করলে আবার পাওয়া যাবে।",
            AppLanguage.ENGLISH to "Are you sure you want to log out? Your business data is safely stored and will be restored upon your next login.",
            AppLanguage.HINDI to "क्या आप लॉगआउट करना चाहते हैं? आपका डेटा सुरक्षित रहेगा और अगले लॉगिन पर वापस मिलेगा।",
            AppLanguage.ARABIC to "هل أنت متأكد من تسجيل الخروج؟ سيتم حفظ بياناتك واستعادتها عند تسجيل الدخول القادم.",
            AppLanguage.URDU to "کیا آپ لاگ آؤٹ کرنا چاہتے ہیں؟ آپ کا ڈیٹا محفوظ رہے گا اور اگلے لاگ ان پر دوبارہ ملے گا۔"
        ),

        // Language Selector
        "select_language" to mapOf(
            AppLanguage.BENGALI to "ভাষা নির্বাচন করুন (Language)",
            AppLanguage.ENGLISH to "Select App Language",
            AppLanguage.HINDI to "भाषा चुनें (Language)",
            AppLanguage.ARABIC to "اختر لغة التطبيق",
            AppLanguage.URDU to "زبان منتخب کریں"
        ),
        "change_language" to mapOf(
            AppLanguage.BENGALI to "ভাষা পরিবর্তন",
            AppLanguage.ENGLISH to "Change Language",
            AppLanguage.HINDI to "भाषा बदलें",
            AppLanguage.ARABIC to "تغيير اللغة",
            AppLanguage.URDU to "زبان تبدیل کریں"
        ),

        // Dashboard Metrics
        "today_summary" to mapOf(
            AppLanguage.BENGALI to "আজকের সামারি",
            AppLanguage.ENGLISH to "Today's Summary",
            AppLanguage.HINDI to "आज का सारांश",
            AppLanguage.ARABIC to "ملخص اليوم",
            AppLanguage.URDU to "آج کا خلاصہ"
        ),
        "cash_in" to mapOf(
            AppLanguage.BENGALI to "টাকা আসলো (Cash In)",
            AppLanguage.ENGLISH to "Cash In (Received)",
            AppLanguage.HINDI to "पैसे आए (कैश इन)",
            AppLanguage.ARABIC to "المقبوضات النقدية",
            AppLanguage.URDU to "پیسے آئے (کیش ان)"
        ),
        "cash_out" to mapOf(
            AppLanguage.BENGALI to "টাকা গেলো (Cash Out)",
            AppLanguage.ENGLISH to "Cash Out (Paid)",
            AppLanguage.HINDI to "पैसे गए (कैश आउट)",
            AppLanguage.ARABIC to "المدفوعات النقدية",
            AppLanguage.URDU to "پیسے گئے (کیش آؤٹ)"
        ),
        "today_sales" to mapOf(
            AppLanguage.BENGALI to "আজকের মোট বিক্রি",
            AppLanguage.ENGLISH to "Today's Total Sales",
            AppLanguage.HINDI to "आज की कुल बिक्री",
            AppLanguage.ARABIC to "إجمالي مبيعات اليوم",
            AppLanguage.URDU to "آج کی کل فروخت"
        ),
        "est_profit" to mapOf(
            AppLanguage.BENGALI to "আনুমানিক নিট লাভ",
            AppLanguage.ENGLISH to "Est. Net Profit",
            AppLanguage.HINDI to "अनुमानित शुद्ध लाभ",
            AppLanguage.ARABIC to "صافي الربح التقديري",
            AppLanguage.URDU to "تخمینہ شدہ خالص منافع"
        ),
        "customer_due" to mapOf(
            AppLanguage.BENGALI to "কাস্টমারের মোট বাকি (পাবো)",
            AppLanguage.ENGLISH to "Total Customer Dues (Receivable)",
            AppLanguage.HINDI to "ग्राहकों का कुल उधार (लेना है)",
            AppLanguage.ARABIC to "ديون العملاء (مستحقات)",
            AppLanguage.URDU to "گاہکوں کا کل ادھار (لینا ہے)"
        ),
        "supplier_due" to mapOf(
            AppLanguage.BENGALI to "সাপ্লায়ারের বকেয়া (দেবো)",
            AppLanguage.ENGLISH to "Total Supplier Payable (Payable)",
            AppLanguage.HINDI to "सप्लायर का बकाया (देना है)",
            AppLanguage.ARABIC to "مستحقات الموردين (واجب الدفع)",
            AppLanguage.URDU to "سپلائر کا بقایا (دینا ہے)"
        ),
        "stock_value" to mapOf(
            AppLanguage.BENGALI to "বর্তমান স্টক মূল্য",
            AppLanguage.ENGLISH to "Current Stock Value",
            AppLanguage.HINDI to "वर्तमान स्टॉक मूल्य",
            AppLanguage.ARABIC to "قيمة المخزون الحالي",
            AppLanguage.URDU to "موجودہ اسٹاک ویلیو"
        ),
        "low_stock_warning" to mapOf(
            AppLanguage.BENGALI to "কম স্টকের পণ্য সতর্কতা",
            AppLanguage.ENGLISH to "Low Stock Alerts",
            AppLanguage.HINDI to "कम स्टॉक चेतावनी",
            AppLanguage.ARABIC to "تنبيه انخفاض المخزون",
            AppLanguage.URDU to "کم اسٹاک الرٹ"
        ),

        // Quick Actions
        "quick_actions" to mapOf(
            AppLanguage.BENGALI to "দ্রুত লেনদেন এন্ট্রি",
            AppLanguage.ENGLISH to "Quick Transaction Entry",
            AppLanguage.HINDI to "त्वरित प्रविष्टि",
            AppLanguage.ARABIC to "إجراءات سريعة",
            AppLanguage.URDU to "فوری اندراج"
        ),
        "action_sale" to mapOf(
            AppLanguage.BENGALI to "পণ্য বিক্রি",
            AppLanguage.ENGLISH to "New Sale",
            AppLanguage.HINDI to "बिक्री",
            AppLanguage.ARABIC to "بيع جديد",
            AppLanguage.URDU to "فروخت"
        ),
        "action_purchase" to mapOf(
            AppLanguage.BENGALI to "মাল ক্রয়",
            AppLanguage.ENGLISH to "Purchase",
            AppLanguage.HINDI to "खरीद",
            AppLanguage.ARABIC to "شراء",
            AppLanguage.URDU to "خریداری"
        ),
        "action_expense" to mapOf(
            AppLanguage.BENGALI to "দোকান খরচ",
            AppLanguage.ENGLISH to "Expense",
            AppLanguage.HINDI to "खर्च",
            AppLanguage.ARABIC to "مصروفات",
            AppLanguage.URDU to "اخراجات"
        ),
        "action_income" to mapOf(
            AppLanguage.BENGALI to "অন্যান্য আয়",
            AppLanguage.ENGLISH to "Other Income",
            AppLanguage.HINDI to "अन्य आय",
            AppLanguage.ARABIC to "دخل إضافي",
            AppLanguage.URDU to "دیگر آمدن"
        ),
        "action_due_collect" to mapOf(
            AppLanguage.BENGALI to "বাকি আদায়",
            AppLanguage.ENGLISH to "Collect Due",
            AppLanguage.HINDI to "उधार वसूली",
            AppLanguage.ARABIC to "تحصيل الديون",
            AppLanguage.URDU to "ادھار وصولی"
        ),
        "action_due_pay" to mapOf(
            AppLanguage.BENGALI to "বকেয়া পরিশোধ",
            AppLanguage.ENGLISH to "Pay Supplier",
            AppLanguage.HINDI to "बकाया भुगतान",
            AppLanguage.ARABIC to "سداد المورد",
            AppLanguage.URDU to "بقایا ادائیگی"
        ),
        "action_add_product" to mapOf(
            AppLanguage.BENGALI to "নতুন পণ্য",
            AppLanguage.ENGLISH to "Add Item",
            AppLanguage.HINDI to "सामान जोड़ें",
            AppLanguage.ARABIC to "إضافة سلعة",
            AppLanguage.URDU to "نیا آئٹم"
        ),
        "action_add_party" to mapOf(
            AppLanguage.BENGALI to "কাস্টমার/সাপ্লায়ার",
            AppLanguage.ENGLISH to "Add Party",
            AppLanguage.HINDI to "पार्टी जोड़ें",
            AppLanguage.ARABIC to "إضافة طرف",
            AppLanguage.URDU to "پارٹی شامل کریں"
        ),

        // Common Labels & Filters
        "search" to mapOf(
            AppLanguage.BENGALI to "অনুসন্ধান করুন...",
            AppLanguage.ENGLISH to "Search...",
            AppLanguage.HINDI to "खोजें...",
            AppLanguage.ARABIC to "بحث...",
            AppLanguage.URDU to "تلاش کریں..."
        ),
        "all" to mapOf(
            AppLanguage.BENGALI to "সব",
            AppLanguage.ENGLISH to "All",
            AppLanguage.HINDI to "सभी",
            AppLanguage.ARABIC to "الكل",
            AppLanguage.URDU to "تمام"
        ),
        "customers" to mapOf(
            AppLanguage.BENGALI to "কাস্টমার খাতা",
            AppLanguage.ENGLISH to "Customers Ledger",
            AppLanguage.HINDI to "ग्राहक बही",
            AppLanguage.ARABIC to "دفتر العملاء",
            AppLanguage.URDU to "گاہک کھاتہ"
        ),
        "suppliers" to mapOf(
            AppLanguage.BENGALI to "সাপ্লায়ার / মহাজন",
            AppLanguage.ENGLISH to "Suppliers Ledger",
            AppLanguage.HINDI to "सप्लायर बही",
            AppLanguage.ARABIC to "دفتر الموردين",
            AppLanguage.URDU to "سپلائر کھاتہ"
        ),
        "save" to mapOf(
            AppLanguage.BENGALI to "সংরক্ষণ করুন",
            AppLanguage.ENGLISH to "Save",
            AppLanguage.HINDI to "सहेजें",
            AppLanguage.ARABIC to "حفظ",
            AppLanguage.URDU to "محفوظ کریں"
        ),
        "cancel" to mapOf(
            AppLanguage.BENGALI to "বাতিল",
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.HINDI to "रद्द करें",
            AppLanguage.ARABIC to "إلغاء",
            AppLanguage.URDU to "منسوخ"
        ),
        "delete" to mapOf(
            AppLanguage.BENGALI to "মুছে ফেলুন",
            AppLanguage.ENGLISH to "Delete",
            AppLanguage.HINDI to "हटाएं",
            AppLanguage.ARABIC to "حذف",
            AppLanguage.URDU to "حذف کریں"
        ),
        "edit" to mapOf(
            AppLanguage.BENGALI to "সম্পাদনা",
            AppLanguage.ENGLISH to "Edit",
            AppLanguage.HINDI to "संपादित करें",
            AppLanguage.ARABIC to "تعديل",
            AppLanguage.URDU to "ترمیم"
        ),
        "date" to mapOf(
            AppLanguage.BENGALI to "তারিখ",
            AppLanguage.ENGLISH to "Date",
            AppLanguage.HINDI to "तारीख",
            AppLanguage.ARABIC to "التاريخ",
            AppLanguage.URDU to "تاریخ"
        ),
        "amount" to mapOf(
            AppLanguage.BENGALI to "টাকার পরিমাণ",
            AppLanguage.ENGLISH to "Amount",
            AppLanguage.HINDI to "राशि",
            AppLanguage.ARABIC to "المبلغ",
            AppLanguage.URDU to "رقم"
        ),
        "paid_amount" to mapOf(
            AppLanguage.BENGALI to "জমা / পরিশোধ",
            AppLanguage.ENGLISH to "Paid / Received",
            AppLanguage.HINDI to "जमा / भुगतान",
            AppLanguage.ARABIC to "المدفوع / المستلم",
            AppLanguage.URDU to "جمع / ادا شدہ"
        ),
        "due_amount" to mapOf(
            AppLanguage.BENGALI to "বাকি টাকা",
            AppLanguage.ENGLISH to "Due Balance",
            AppLanguage.HINDI to "बकाया राशि",
            AppLanguage.ARABIC to "المبلغ المتبقي",
            AppLanguage.URDU to "بقایا رقم"
        ),
        "note" to mapOf(
            AppLanguage.BENGALI to "মন্তব্য / বিবরণ",
            AppLanguage.ENGLISH to "Notes / Details",
            AppLanguage.HINDI to "विवरण",
            AppLanguage.ARABIC to "ملاحظات",
            AppLanguage.URDU to "نوٹس / تفصیلات"
        ),
        "view_all" to mapOf(
            AppLanguage.BENGALI to "সব দেখুন",
            AppLanguage.ENGLISH to "View All",
            AppLanguage.HINDI to "सभी देखें",
            AppLanguage.ARABIC to "عرض الكل",
            AppLanguage.URDU to "سب دیکھیں"
        ),
        // Network & Merchant Directory
        "nav_network" to mapOf(
            AppLanguage.BENGALI to "মার্কেট/নেটওয়ার্ক",
            AppLanguage.ENGLISH to "Network/Directory",
            AppLanguage.HINDI to "नेटवर्क/व्यापारी",
            AppLanguage.ARABIC to "شبكة التجار",
            AppLanguage.URDU to "بزنس نیٹ ورک"
        ),
        "nav_messages" to mapOf(
            AppLanguage.BENGALI to "মেসেজ/চ্যাট",
            AppLanguage.ENGLISH to "Messages/Chat",
            AppLanguage.HINDI to "मैसेज/चैट",
            AppLanguage.ARABIC to "الرسائل والمحادثات",
            AppLanguage.URDU to "پیغامات / بات چیت"
        ),
        "profile_customization" to mapOf(
            AppLanguage.BENGALI to "প্রোফাইল কাস্টমাইজেশন",
            AppLanguage.ENGLISH to "Profile Customization",
            AppLanguage.HINDI to "प्रोफ़ाइल अनुकूलन",
            AppLanguage.ARABIC to "تخصيص الملف الشخصي",
            AppLanguage.URDU to "پروفائل حسب ضرورت"
        ),
        "choose_avatar" to mapOf(
            AppLanguage.BENGALI to "প্রোফাইল পিক / অবতার পছন্দ করুন",
            AppLanguage.ENGLISH to "Choose Avatar / Profile Picture",
            AppLanguage.HINDI to "अवतार चुनें",
            AppLanguage.ARABIC to "اختر الصورة الرمزية",
            AppLanguage.URDU to "اوتار منتخب کریں"
        ),
        "username_handle" to mapOf(
            AppLanguage.BENGALI to "ইউজারনেম (@username)",
            AppLanguage.ENGLISH to "Username (@username)",
            AppLanguage.HINDI to "उपयोगकर्ता नाम (@username)",
            AppLanguage.ARABIC to "اسم المستخدم (@username)",
            AppLanguage.URDU to "صارف کا نام (@username)"
        ),
        "business_bio" to mapOf(
            AppLanguage.BENGALI to "দোকান ও ব্যবসায়ের বিবরণ (Bio)",
            AppLanguage.ENGLISH to "Business Details / Bio",
            AppLanguage.HINDI to "व्यवसाय विवरण",
            AppLanguage.ARABIC to "نبذة عن النشاط التجاري",
            AppLanguage.URDU to "کاروبار کی تفصیلات"
        ),
        "business_category" to mapOf(
            AppLanguage.BENGALI to "ব্যবসার ধরণ / ক্যাটাগরি",
            AppLanguage.ENGLISH to "Business Category",
            AppLanguage.HINDI to "व्यापार श्रेणी",
            AppLanguage.ARABIC to "فئة النشاط التجاري",
            AppLanguage.URDU to "کاروباری زمرہ"
        ),
        "search_merchant" to mapOf(
            AppLanguage.BENGALI to "ইউজারনেম (@handle), শপ নাম বা ফোন দিয়ে খুঁজুন...",
            AppLanguage.ENGLISH to "Search by @username, shop name or phone...",
            AppLanguage.HINDI to "@username या दुकान के नाम से खोजें...",
            AppLanguage.ARABIC to "ابحث باسم المستخدم أو المتجر...",
            AppLanguage.URDU to "صارف کے نام یا دکان کے نام سے تلاش کریں..."
        ),
        "verified_merchant" to mapOf(
            AppLanguage.BENGALI to "ভেরিফাইড মার্চেন্ট",
            AppLanguage.ENGLISH to "Verified Merchant",
            AppLanguage.HINDI to "सत्यापित व्यापारी",
            AppLanguage.ARABIC to "تاجر موثق",
            AppLanguage.URDU to "تصدیق شدہ تاجر"
        ),
        "seller_rating" to mapOf(
            AppLanguage.BENGALI to "রেটিং",
            AppLanguage.ENGLISH to "Rating",
            AppLanguage.HINDI to "रेटिंग",
            AppLanguage.ARABIC to "التقييم",
            AppLanguage.URDU to "درجہ بندی"
        ),
        "total_sales_completed" to mapOf(
            AppLanguage.BENGALI to "টি সফল সেল",
            AppLanguage.ENGLISH to "Sales Completed",
            AppLanguage.HINDI to "सफल बिक्री",
            AppLanguage.ARABIC to "مبيعات مكتملة",
            AppLanguage.URDU to "مکمل سیلز"
        ),
        "start_chat" to mapOf(
            AppLanguage.BENGALI to "মেসেজ পাঠান",
            AppLanguage.ENGLISH to "Send Message",
            AppLanguage.HINDI to "मैसेज भेजें",
            AppLanguage.ARABIC to "إرسال رسالة",
            AppLanguage.URDU to "پیغام بھیجیں"
        ),
        "type_message" to mapOf(
            AppLanguage.BENGALI to "মেসেজ লিখুন...",
            AppLanguage.ENGLISH to "Type a message...",
            AppLanguage.HINDI to "संदेश लिखें...",
            AppLanguage.ARABIC to "اكتب رسالة...",
            AppLanguage.URDU to "پیغام ٹائپ کریں..."
        ),
        "whatsapp_chat" to mapOf(
            AppLanguage.BENGALI to "হোয়াটসঅ্যাপ",
            AppLanguage.ENGLISH to "WhatsApp",
            AppLanguage.HINDI to "व्हाट्सएप",
            AppLanguage.ARABIC to "واتساب",
            AppLanguage.URDU to "واٹس ایپ"
        ),
        "telegram_chat" to mapOf(
            AppLanguage.BENGALI to "টেলিগ্রাম",
            AppLanguage.ENGLISH to "Telegram",
            AppLanguage.HINDI to "टेलीग्राम",
            AppLanguage.ARABIC to "تيليجرام",
            AppLanguage.URDU to "ٹیلی گرام"
        ),
        "call_now" to mapOf(
            AppLanguage.BENGALI to "কল করুন",
            AppLanguage.ENGLISH to "Call Now",
            AppLanguage.HINDI to "कॉल करें",
            AppLanguage.ARABIC to "اتصل الآن",
            AppLanguage.URDU to "ابھی کال کریں"
        )
    )
}
